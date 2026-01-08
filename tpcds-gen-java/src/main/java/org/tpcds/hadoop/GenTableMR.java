package org.tpcds.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.tpcds.DsGen;
import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.output.TableWriter;
import org.tpcds.tables.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Hadoop MapReduce driver for parallel TPC-DS data generation.
 * This runs the Java data generator across multiple mappers for scalability.
 */
public class GenTableMR extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        int res = ToolRunner.run(conf, new GenTableMR(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        String[] remainingArgs = new GenericOptionsParser(getConf(), args).getRemainingArgs();

        // Parse command line arguments
        int scale = 0;
        String table = "all";
        String dir = null;
        int parallel = 0;

        for (int i = 0; i < remainingArgs.length; i++) {
            switch (remainingArgs[i]) {
                case "-s":
                case "--scale":
                    scale = Integer.parseInt(remainingArgs[++i]);
                    break;
                case "-t":
                case "--table":
                    table = remainingArgs[++i];
                    break;
                case "-d":
                case "--dir":
                    dir = remainingArgs[++i];
                    break;
                case "-p":
                case "--parallel":
                    parallel = Integer.parseInt(remainingArgs[++i]);
                    break;
                default:
                    // Skip unknown options
                    break;
            }
        }

        if (scale == 0 || dir == null) {
            System.err.println("Usage: GenTableMR -s <scale> -d <dir> [-t <table>] [-p <parallel>]");
            System.err.println("  -s, --scale <n>    Scale factor in GB");
            System.err.println("  -d, --dir <path>   Output directory on HDFS");
            System.err.println("  -t, --table <name> Generate only this table (default: all)");
            System.err.println("  -p, --parallel <n> Number of parallel mappers (default: scale)");
            return 1;
        }

        if (parallel == 0) {
            parallel = scale;
        }

        if (parallel == 1 || scale == 1) {
            System.err.println("The MR task does not work for scale=1 or parallel=1");
            System.err.println("Use the standalone jar directly: java -jar tpcds-gen-java.jar -s 1 -d <dir>");
            return 1;
        }

        Path out = new Path(dir);
        Path in = genInput(table, scale, parallel);

        Configuration conf = getConf();
        conf.setInt("mapred.task.timeout", 0);
        conf.setInt("mapreduce.task.timeout", 0);
        conf.setBoolean("mapreduce.map.output.compress", true);
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.GzipCodec");

        // Pass parameters to mappers
        conf.setInt("tpcds.scale", scale);
        conf.setInt("tpcds.parallel", parallel);
        conf.set("tpcds.table", table);

        Job job = Job.getInstance(conf, "TPC-DS GenTable " + table + "_" + scale);
        job.setJarByClass(getClass());
        job.setNumReduceTasks(0);
        job.setMapperClass(TpcdsGenMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setInputFormatClass(NLineInputFormat.class);
        NLineInputFormat.setNumLinesPerSplit(job, 1);

        FileInputFormat.addInputPath(job, in);
        FileOutputFormat.setOutputPath(job, out);

        // Use multiple output to only write the named files
        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
        MultipleOutputs.addNamedOutput(job, "text",
                TextOutputFormat.class, LongWritable.class, Text.class);

        boolean success = job.waitForCompletion(true);

        // Cleanup input file
        FileSystem fs = FileSystem.get(getConf());
        fs.delete(in, false);

        return success ? 0 : 1;
    }

    /**
     * Generate input file with one line per mapper task.
     * Each line contains: child_number parallel_count scale table
     */
    public Path genInput(String table, int scale, int parallel) throws Exception {
        long epoch = System.currentTimeMillis() / 1000;
        Path in = new Path("/tmp/tpcds_" + table + "_" + scale + "-" + epoch);
        FileSystem fs = FileSystem.get(getConf());
        FSDataOutputStream out = fs.create(in);

        for (int i = 1; i <= parallel; i++) {
            // Format: child parallel scale table
            out.writeBytes(String.format("%d %d %d %s\n", i, parallel, scale, table));
        }
        out.close();
        return in;
    }

    /**
     * Mapper that generates TPC-DS data for a single parallel chunk.
     */
    public static class TpcdsGenMapper extends Mapper<LongWritable, Text, Text, Text> {
        private MultipleOutputs<Text, Text> mos;

        @Override
        protected void setup(Context context) throws IOException {
            mos = new MultipleOutputs<>(context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String[] parts = value.toString().trim().split("\\s+");
            int child = Integer.parseInt(parts[0]);
            int parallel = Integer.parseInt(parts[1]);
            int scale = Integer.parseInt(parts[2]);
            String table = parts[3];

            try {
                // Load distribution data from classpath
                InputStream distStream = getClass().getResourceAsStream("/tpcds.idx");
                if (distStream == null) {
                    // Try from configuration - may be in distributed cache
                    throw new IOException("Distribution file not found in classpath");
                }

                DistributionManager distMgr = new DistributionManager();
                distMgr.loadFromStream(distStream);
                distStream.close();

                RandomNumberGenerator rng = new RandomNumberGenerator();
                distMgr.setRng(rng);

                // Create table generator and generate data
                TableId[] tablesToGenerate;
                if ("all".equals(table)) {
                    tablesToGenerate = TableId.values();
                } else {
                    tablesToGenerate = new TableId[]{TableId.fromName(table)};
                }

                for (TableId tableId : tablesToGenerate) {
                    if (tableId == null) continue;

                    Table<?> tableGen = TableFactory.createTable(tableId, rng, distMgr, scale);
                    if (tableGen == null) continue;

                    String tableName = tableGen.getName();
                    long rowCount = tableGen.getRowCount(scale);

                    // Calculate row range for this child
                    long rowsPerChild = rowCount / parallel;
                    long startRow = (child - 1) * rowsPerChild + 1;
                    long endRow = (child == parallel) ? rowCount : child * rowsPerChild;

                    // Generate rows
                    for (long row = startRow; row <= endRow; row++) {
                        Object rowData = tableGen.buildRow(row);
                        String rowStr = tableGen.formatRowObject(rowData, "|");
                        mos.write("text", new Text(rowStr), NullWritable.get(), tableName + "/data");
                    }
                }
            } catch (Exception e) {
                throw new IOException("Failed to generate TPC-DS data", e);
            }
        }
    }
}
