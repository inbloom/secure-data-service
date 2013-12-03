#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

startSearchIndexer

processApps $APPSTODEPLOY

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org elastic_search_address=http://$NODE_NAME.slidev.org:9200 searchTests

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE