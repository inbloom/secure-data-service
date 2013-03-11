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
bundle exec rake FORCE_COLOR=true ingestion_log_directory=/home/ingestion/logs ingestion_landing_zone=/home/ingestion/lz/inbound ingestion_healthcheck_url=https://$NODE_NAME.slidev.org/ingestion-service/healthcheck apiOdinTests TOGGLE_TABLESCANS=false

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE





