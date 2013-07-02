#!/bin/sh
set -e
sh checkoutAndBuild.sh $2
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
sh ingestDataset.sh $1 1
./checkIngestedCounts.sh $1
sh ingestDataset.sh $1 2
./checkIngestedCounts.sh $1
sh log_durations.sh $1
sh ingestDataset.sh $3 3
sh log_delete_duration.sh $3
sh ingestDataset.sh purge.zip 4
sh log_purge_duration.sh
sh resetEnvironment.sh
sh ingestDataset.sh $1 1
./checkIngestedCounts.sh $1
sh ingestDataset.sh $1 2
./checkIngestedCounts.sh $1
sh log_durations.sh $1
#sh ingestDataset.sh $3 3
#sh log_delete_duration.sh $3
#sh ingestDataset.sh purge.zip 4
#sh log_purge_duration.sh

# Send PDF report
./ingestion-report.py < megtomcat01_logs/auto_perf_results.log > message.txt
/usr/local/bin/ingestion-mailx \
    -s "Megatron Mini Slirp Performance Testing" \
    -a IngestionPerformanceDaily.pdf \
    -a raw_data.txt \
    Sliders-megatron@wgen.net \
    okrook@wgen.net \
    rmiller@wgen.net \
    cwilson@contractor.wgen.net \
    rfarris@wgen.net \
    < message.txt

set +e
# Check for data profile changes in the db
./check_data_profile.sh $1
if [ $? -eq 1 ]; then
    DATA_PROFILE_DIFF="/opt/ingestion/data_profiles/${1}.diff"
    /usr/local/bin/ingestion-mailx \
        -s "Megatron Mini Slirp Data Profile Change for $1" \
        Sliders-megatron@wgen.net \
        < $DATA_PROFILE_DIFF
fi

echo "Done!"

