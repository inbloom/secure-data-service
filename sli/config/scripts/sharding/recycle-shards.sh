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

echo indexing sli.
mongo sli $SLI_HOME/config/indexes/sli_indexes.js
echo

echo indexing ingestion_batch_job.
mongo ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
echo
