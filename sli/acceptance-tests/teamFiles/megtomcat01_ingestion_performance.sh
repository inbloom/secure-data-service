#!/bin/sh
set -e
sh checkoutAndBuild.sh $2
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
sh ingestDataset.sh $1 1
sh ingestDataset.sh $1 2
sh log_durations.sh $1
sh ingestDataset.sh $3 3
sh log_delete_duration.sh $3
sh ingestDataset.sh purge.zip 4
sh log_purge_duration.sh
sh resetEnvironment.sh
sh ingestDataset.sh $1 1
sh ingestDataset.sh $1 2
sh log_durations.sh $1
sh ingestDataset.sh $3 3
sh log_delete_duration.sh $3
sh ingestDataset.sh purge.zip 4
sh log_purge_duration.sh
echo "Done!"

