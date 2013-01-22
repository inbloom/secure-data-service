#!/bin/sh
if [ -z "$1" ]; then
    echo "Branch defaulted to master"
    branch="master"
else
    branch=$1
fi
set -e
cd /opt/megatron/sli/sli/
git fetch
git checkout $branch
git pull
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
now=$(date +"%Y_%m_%d")
filename="/opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01_logs/ingestion_performance_$now.log"
echo "Starting automated performance testing, logging to $filename"
nohup sh megtomcat01_ingestion_performance.sh $branch > $filename &
