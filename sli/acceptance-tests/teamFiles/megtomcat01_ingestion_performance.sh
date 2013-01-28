#!/bin/sh
set -e
sh checkoutAndBuild.sh $2
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
sh ingestDataset.sh $1 1
sh ingestDataset.sh $1 2
sh log_durations.sh
sh resetEnvironment.sh
sh ingestDataset.sh $1 1
sh ingestDataset.sh $1 2
sh log_durations.sh $1
echo "Done!"

