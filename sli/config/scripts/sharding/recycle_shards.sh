#!/bin/bash

sh $SLI_HOME/config/scripts/sharding/kill-shard.sh
echo -e Shards killed."\n"

sh $SLI_HOME/config/scripts/sharding/start-shard.sh
echo -e Shards started."\n"

mongo sli $SLI_HOME/config/indexes/sli_indexes.js
echo -e sli indexed"\n" 

mongo is $SLI_HOME/config/indexes/is_indexes.js
echo -e is indexed."\n"

mongo ingestion_batch_job $SLI_HOME/config/indexes/ingestion_batch_job_indexes.js
echo -e ingestion_batch_job indexed."\n"

mongo admin $SLI_HOME/config/shards/sli_shards.js
echo -e shards configured."\n"
