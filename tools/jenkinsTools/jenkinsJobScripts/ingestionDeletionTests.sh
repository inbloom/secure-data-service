#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

processApps $APPSTODEPLOY


cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
nbundle install --full-index --deployment
bundle exec rake FORCE_COLOR=true ingestion_log_directory=/home/ingestion/logs ingestion_landing_zone=/home/ingestion/lz/inbound ingestion_healthcheck_url=https://$NODE_NAME.slidev.org/ingestion-service/healthcheck ingestionDeletionTests TOGGLE_TABLESCANS=true

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE
