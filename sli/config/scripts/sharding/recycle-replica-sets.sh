#!/bin/bash

mongos_port=27017
num_shards=2

if [ -e ../../../pom.xml -a -d ../../../config ]; then
    SLI_HOME=`pwd`/../../..
fi

sh $SLI_HOME/config/scripts/sharding/kill-shard.sh
echo Shards killed.

sh $SLI_HOME/config/scripts/sharding/start-replica-shard.sh $mongos_port $num_shards
echo Shards started.

sleep 3
mongo --port 10001 admin $SLI_HOME/config/shards/sli_shards.js
mongo --port 10002 admin $SLI_HOME/config/shards/sli_shards.js
mongo --port 10004 admin $SLI_HOME/config/shards/sli_shards.js
mongo --port 10005 admin $SLI_HOME/config/shards/sli_shards.js
echo shards configured.

sleep 3
mongo --port 10001 sli $SLI_HOME/config/indexes/sli_indexes.js
mongo --port 10002 sli $SLI_HOME/config/indexes/sli_indexes.js
mongo --port 10004 sli $SLI_HOME/config/indexes/sli_indexes.js
mongo --port 10005 sli $SLI_HOME/config/indexes/sli_indexes.js
echo sli indexed.

sleep 3
mongo --port 10001 is $SLI_HOME/config/indexes/is_indexes.js
mongo --port 10002 is $SLI_HOME/config/indexes/is_indexes.js
mongo --port 10004 is $SLI_HOME/config/indexes/is_indexes.js
mongo --port 10005 is $SLI_HOME/config/indexes/is_indexes.js
echo is indexed.

sleep 3
mongo --port 10001 ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
mongo --port 10002 ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
mongo --port 10004 ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
mongo --port 10005 ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
echo ingestion_batch_job indexed.

