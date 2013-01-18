#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

startSearchIndexer

processApps $APPSTODEPLOY

# Generate and Ingest Odin data
cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true ingestion_log_directory=/home/ingestion/logs ingestion_landing_zone=/home/ingestion/lz/inbound ingestion_healthcheck_url=https://$NODE_NAME.slidev.org/ingestion-service/healthcheck jmeterOdinTests TOGGLE_TABLESCANS=false


# Dump fixture data to authorize Jmeter application and realm/tenant
#cp /opt/data/api_perf/slave.tar.gz ./
#tar xvzf slave.tar.gz
#Get rid of the admin data
#rm -f dump/sli/realm.bson
#rm -f dump/sli/application.bson
#rm -f dump/sli/applicationAuthorization.bson
#rm -f dump/sli/adminDelegation.bson
#mongorestore --drop slave
#cd $WORKSPACE/tools/jmeter/ci-setup/
cd $WORKSPACE/tools/jmeter/odin-ci
source ci-jmeter-realm.sh

cd $WORKSPACE/tools/jmeter/
rm -f *.jtl
cd $WORKSPACE/sli/acceptance-tests
rm -f *.jtl
bundle install --deployment

#run the jmeter acceptance test wrapper with ci properties
bundle exec rake FORCE_COLOR=true apiJMeterTests jmeter_jmx_path=../../tools/jmeter/ jmeter_bin=/opt/apache-jmeter-2.7/bin/jmeter jmeter_properties=ci.properties jmeter_regression_threshold=0.5

EXITCODE=$?

#move any generated jtl files to the workspace for the performance reports
find . -name "*.jtl" -maxdepth 1 -exec mv '{}' $WORKSPACE/. \;


mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE





