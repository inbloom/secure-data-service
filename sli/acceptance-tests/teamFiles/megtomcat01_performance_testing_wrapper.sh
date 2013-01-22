#!/bin/sh
set -e
now=$(date +"%Y_%m_%d")
filename="/opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01_logs/ingestion_performance_$now.log"
echo "Starting automated performance testing, logging to $filename"
nohup sh megtomcat01_ingestion_performance.sh $1 > $filename &
