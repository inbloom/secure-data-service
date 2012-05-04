#
# Get list of indexes we have
#
echo "----------------- COLLECTION INDEXES (SLI) -----------------";
mongo igmongo.slidev.org:27017/sli --eval "
        var cols = db.getCollectionNames();
        for (var i = 0; i < cols.length; i++) {
                printjson(db[cols[i]].getIndexes());
        }
";
#
# Database Size
#
echo "----------------- COLLECTION SIZE (SLI) -----------------";
mongo igmongo.slidev.org:27017/sli --eval "
        var cols = db.getCollectionNames();
        for (var i = 0; i < cols.length; i++) {
                print(db[cols[i]]);
                printjson(db[cols[i]].count());
        }
";
#
#
#
echo "----------------- LAST EXECUTIONS OF INGESTION -----------------";
mongo igmongo.slidev.org:27017/ingestion_batch_job --eval "
var dbName = 'newBatchJob';
var skipCount = db[dbName].count() - 1;
db[dbName].find().skip(skipCount).forEach(printjson);
";
#
#
#