#!/bin/sh
set -e
sh checkoutAndBuild.sh $1
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
sh ingestDataset.sh 1
sh ingestDataset.sh 2
sh log_durations.sh
sh resetEnvironment.sh
sh ingestDataset.sh 1
sh ingestDataset.sh 2
sh log_durations.sh
echo "Done!"

