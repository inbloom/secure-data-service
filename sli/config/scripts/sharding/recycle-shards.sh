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


sh $SLI_HOME/config/scripts/sharding/kill-shard.sh
echo Shards killed.

sh $SLI_HOME/config/scripts/sharding/start-shard.sh $mongos_port $num_shards
echo Shards started.

mongo sli $SLI_HOME/config/indexes/sli_indexes.js
echo sli indexed.

mongo is $SLI_HOME/config/indexes/is_indexes.js
echo is indexed.

mongo ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
echo ingestion_batch_job indexed.

mongo admin $SLI_HOME/config/shards/sli_shards.js
echo shards configured.
