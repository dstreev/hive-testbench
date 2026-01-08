/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.output.TableWriter;
import org.tpcds.tables.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * TPC-DS Data Generator - Java Port
 *
 * This is a Java port of the TPC-DS dsdgen tool, originally written in C.
 * It generates benchmark data according to the TPC-DS specification.
 */
@Command(
    name = "dsdgen",
    mixinStandardHelpOptions = true,
    version = "TPC-DS Data Generator (Java) 1.0",
    description = "Generates TPC-DS benchmark data"
)
public class DsGen implements Callable<Integer> {

    @Option(names = {"-s", "--scale"}, description = "Scale factor in GB (default: 1)")
    private int scale = 1;

    @Option(names = {"-d", "--dir"}, description = "Output directory (default: current dir)")
    private String outputDir = ".";

    @Option(names = {"-t", "--table"}, description = "Generate only this table")
    private String tableName;

    @Option(names = {"--distributions"}, description = "Path to tpcds.idx file")
    private String distributionsFile = "tpcds.idx";

    @Option(names = {"--delimiter"}, description = "Field delimiter (default: |)")
    private String delimiter = "|";

    @Option(names = {"-f", "--force"}, description = "Overwrite existing files")
    private boolean force = false;

    @Option(names = {"--parallel"}, description = "Number of parallel streams")
    private int parallel = 1;

    @Option(names = {"--child"}, description = "Which parallel child (1 to parallel)")
    private int child = 1;

    @Option(names = {"-q", "--quiet"}, description = "Quiet mode")
    private boolean quiet = false;

    @Override
    public Integer call() throws Exception {
        // Create output directory if needed
        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        // Check for distribution file
        File distFile = new File(distributionsFile);
        if (!distFile.exists()) {
            // Try in output dir
            distFile = new File(outputDir, distributionsFile);
            if (!distFile.exists()) {
                System.err.println("Error: Distribution file not found: " + distributionsFile);
                System.err.println("Please specify --distributions path/to/tpcds.idx");
                return 1;
            }
            distributionsFile = distFile.getAbsolutePath();
        }

        if (!quiet) {
            System.out.println("TPC-DS Data Generator (Java Port)");
            System.out.println("Scale factor: " + scale + " GB");
            System.out.println("Output directory: " + outputDir);
            System.out.println("Distributions file: " + distributionsFile);
            System.out.println();
        }

        // Initialize RNG and distribution manager
        RandomNumberGenerator rng = new RandomNumberGenerator();
        rng.init();

        DistributionManager distMgr = new DistributionManager(distributionsFile, rng);

        // Create table writer
        TableWriter writer = new TableWriter(outputDir, delimiter, ".dat");

        // Get list of tables to generate
        List<Table<?>> tables = new ArrayList<>();

        if (tableName != null) {
            // Generate single table
            TableId tableId = TableId.fromName(tableName);
            if (tableId == null) {
                System.err.println("Unknown table: " + tableName);
                System.err.println("Valid tables: ");
                for (TableId t : TableId.values()) {
                    System.err.println("  " + t.getName() + " (" + t.getAbbreviation() + ")");
                }
                return 1;
            }
            Table<?> table = createTable(tableId, rng, distMgr);
            if (table != null) {
                tables.add(table);
            }
        } else {
            // Generate all tables
            for (TableId tableId : TableId.values()) {
                Table<?> table = createTable(tableId, rng, distMgr);
                if (table != null) {
                    tables.add(table);
                }
            }
        }

        // Generate data
        for (Table<?> table : tables) {
            if (!quiet) {
                System.out.println("Generating " + table.getName() + "...");
            }
            try {
                writer.writeTable(table, scale);
            } catch (Exception e) {
                System.err.println("Error generating " + table.getName() + ": " + e.getMessage());
                if (!quiet) {
                    e.printStackTrace();
                }
            }
        }

        if (!quiet) {
            System.out.println("Done!");
        }

        return 0;
    }

    private Table<?> createTable(TableId tableId, RandomNumberGenerator rng, DistributionManager distMgr) {
        switch (tableId) {
            // Dimension tables
            case TIME_DIM:
                return new TimeDimTable(rng, distMgr, scale);
            case DATE_DIM:
                return new DateDimTable(rng, distMgr, scale);
            case REASON:
                return new ReasonTable(rng, distMgr, scale);
            case SHIP_MODE:
                return new ShipModeTable(rng, distMgr, scale);
            case INCOME_BAND:
                return new IncomeBandTable(rng, distMgr, scale);
            case WAREHOUSE:
                return new WarehouseTable(rng, distMgr, scale);
            case CUSTOMER_ADDRESS:
                return new CustomerAddressTable(rng, distMgr, scale);
            case CUSTOMER_DEMOGRAPHICS:
                return new CustomerDemographicsTable(rng, distMgr, scale);
            case HOUSEHOLD_DEMOGRAPHICS:
                return new HouseholdDemographicsTable(rng, distMgr, scale);
            case CUSTOMER:
                return new CustomerTable(rng, distMgr, scale);
            case STORE:
                return new StoreTable(rng, distMgr, scale);
            case ITEM:
                return new ItemTable(rng, distMgr, scale);
            case PROMOTION:
                return new PromotionTable(rng, distMgr, scale);
            case CALL_CENTER:
                return new CallCenterTable(rng, distMgr, scale);
            case CATALOG_PAGE:
                return new CatalogPageTable(rng, distMgr, scale);
            case WEB_PAGE:
                return new WebPageTable(rng, distMgr, scale);
            case WEB_SITE:
                return new WebSiteTable(rng, distMgr, scale);

            // Fact tables
            case INVENTORY:
                return new InventoryTable(rng, distMgr, scale);
            case STORE_SALES:
                return new StoreSalesTable(rng, distMgr, scale);
            case STORE_RETURNS:
                return new StoreReturnsTable(rng, distMgr, scale);
            case CATALOG_SALES:
                return new CatalogSalesTable(rng, distMgr, scale);
            case CATALOG_RETURNS:
                return new CatalogReturnsTable(rng, distMgr, scale);
            case WEB_SALES:
                return new WebSalesTable(rng, distMgr, scale);
            case WEB_RETURNS:
                return new WebReturnsTable(rng, distMgr, scale);

            default:
                if (!quiet) {
                    System.out.println("Table not yet implemented: " + tableId.getName());
                }
                return null;
        }
    }

    public static void main(String[] args) throws Exception {
        // Check if this is a Hadoop/YARN invocation requesting GenTableMR
        // This happens when hadoop jar specifies a class but the manifest Main-Class takes precedence
        if (args.length > 0 && "org.tpcds.hadoop.GenTableMR".equals(args[0])) {
            // Remove the class name and delegate to GenTableMR
            String[] remainingArgs = new String[args.length - 1];
            System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);
            org.tpcds.hadoop.GenTableMR.main(remainingArgs);
            return;
        }

        int exitCode = new CommandLine(new DsGen()).execute(args);
        System.exit(exitCode);
    }
}
