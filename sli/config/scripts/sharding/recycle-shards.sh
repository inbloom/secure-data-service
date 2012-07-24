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
if [ -z $3 ]; then
  num_per_shard=1
else
  num_per_shard=$3
fi


sh $SLI_HOME/config/scripts/sharding/kill-shard.sh
echo Shards killed.

sh $SLI_HOME/config/scripts/sharding/kill-mongo.sh 27011
echo Mongo killed.

sh $SLI_HOME/config/scripts/sharding/start-shard.sh $mongos_port $num_shards $num_per_shard
echo Shards started.

sh $SLI_HOME/config/scripts/sharding/start-mongo.sh 27011
echo Mongo started.

mongo admin $SLI_HOME/config/shards/sli_shards.js
mongo admin $SLI_HOME/config/shards/is_shards.js
echo shards configured.

mongo sli $SLI_HOME/config/indexes/sli_indexes.js
echo sli indexed.

mongo is $SLI_HOME/config/indexes/is_indexes.js
echo is indexed.

mongo --port 27011 ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
echo ingestion_batch_job indexed.
