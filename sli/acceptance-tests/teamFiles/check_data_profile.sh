#!/bin/sh
if [ -z "$1" ]; then
    echo "Defaulted to SmallSampleDataSet.zip"
    dataset="SmallSampleDataSet.zip"
else
    dataset=$1
fi

SLI_HOME="/opt/megatron/sli"
PROFILE_STORAGE_DIR="/opt/ingestion/data_profiles"
DB="02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"

PROFILER="${SLI_HOME}/tools/data-tools/mongo_data_profile.rb"
PROFILER_OPTIONS="-created"

BASELINE_DATA_PROF_FILE="${PROFILE_STORAGE_DIR}/${dataset}_baseline.profile"
CUR_DATA_PROF_FILE="${PROFILE_STORAGE_DIR}/${dataset}_current.profile"

DIFF_OUTPUT_FILE="${PROFILE_STORAGE_DIR}/${dataset}.diff"

# Generate new data profile
$PROFILER $PROFILER_OPTIONS $DB > $CUR_DATA_PROF_FILE

result=0

# Compare with previous
if [ -f $BASELINE_DATA_PROF_FILE ]; then
    diff -C 5 $BASELINE_DATA_PROF_FILE $CUR_DATA_PROF_FILE > $DIFF_OUTPUT_FILE
    result=$?
else
    # the first time for a dataset just create a baseline file
    mv -f $CUR_DATA_PROF_FILE $BASELINE_DATA_PROF_FILE
fi

# Clean up
if [ $result -eq 0 ]; then
    rm -f $CUR_DATA_PROF_FILE $DIFF_OUTPUT_FILE
fi

exit $result
