#!/bin/bash

if [ -z $1 ]; then
  mongos_port=27017
else
  mongos_port=$1
fi
if [ -z $2 ]; then
  num_shards=2
else
  num_shards=$2
fi

echo killing shards.
sh $SLI_HOME/config/scripts/sharding/kill-shard.sh
echo

echo starting shards.
$SLI_HOME/config/scripts/sharding/start-shard.sh $mongos_port $num_shards
echo

echo setting the maximum number of open file descriptors to 20000
ulimit -n 20000
echo

echo executing sharding and presplitting configuration. 
mongo admin --eval "var tenant='02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a'" $SLI_HOME/config/shards/sli-shard-presplit.js
#mongo admin $SLI_HOME/config/shards/is_shards.js
echo

echo indexing sli.
mongo sli $SLI_HOME/config/indexes/sli_indexes.js
echo

echo indexing Midgar tenant.
mongo 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a $SLI_HOME/config/indexes/tenantDB_indexes.js
echo

echo ingexing is.
mongo is $SLI_HOME/config/indexes/is_indexes.js
echo

echo indexing ingestion_batch_job.
mongo ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
echo
