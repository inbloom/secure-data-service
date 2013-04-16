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
tail -8 megtomcat01_logs/auto_perf_results.log| mailx -A wgen -s "Megatron Mini Slirp Performance Testing" Sliders-MegatronDev@wgen.net 2>/dev/null
echo "Done!"

