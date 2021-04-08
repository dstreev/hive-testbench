for i in q*.sql; do
  queryarr=(${queryarr[@]} "${i}")
done

Q_LEN=$(( ${#queryarr[@]}+1 ))

echo "!record \${OUTPUT_FILE};"
echo "use \${DB};"
for (( i=0; i<$Q_LEN; i++ ))
do
  echo "SELECT \"MARKER\", \"${queryarr[i]}\", \"${queryarr[i-1]}\",  unix_timestamp();"
  if [ "X${queryarr[i]}" != "X" ]; then
    echo "!run ${queryarr[i]};"
  fi
done

echo "!record"

