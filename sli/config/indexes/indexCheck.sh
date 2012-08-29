
while getopts "s:i:bh" opt; do
case $opt in 
    s)
        cat _indexParserBegin.js sli_indexes.js _indexParserEnd.js > tmp_del_sliCheck.js
        mongo --quiet $OPTARG:27017/sli tmp_del_sliCheck.js
    ;;
    i)
        cat _indexParserBegin.js is_indexes.js _indexParserEnd.js > tmp_del_isCheck.js
        mongo --quiet $OPTARG:27017/is tmp_del_isCheck.js
    ;;
    b)
        cat _indexParserBegin.js ingestion_batch_job_indexes.js _indexParserEnd.js > tmp_del_ibjCheck.js
        mongo --quiet $OPTARG:27017/ingestion_batch_job tmp_del_ibjCheck.js
    ;;
    h)
        echo "./indexCheck.sh -i localhost  #will check is                  indexes"
        echo "./indexCheck.sh -b localhost  #will check sli                 indexes"
        echo "./indexCheck.sh -s localhost  #will check ingestion_batch_job indexes"
    ;;
esac
done
