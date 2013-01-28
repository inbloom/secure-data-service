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
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/application_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/realm_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/securityEvent_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/features/utils/properties.yml
git checkout -- /opt/megatron/sli/sli/admin-tools/admin-rails/config/config.yml
git checkout -- /opt/megatron/sli/sli/databrowser/config/config.yml
git checkout $branch
git pull
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
now=$(date +"%Y_%m_%d")
filename="/opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01_logs/ingestion_performance_$now.log"
echo "Starting automated performance testing, logging to $filename"
nohup sh megtomcat01_ingestion_performance.sh $branch >> $filename &