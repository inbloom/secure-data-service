#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

startSearchIndexer

processApps $APPSTODEPLOY

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org api_ssl_server_url=https://$NODE_NAME.slidev.org:8443 apiAndSecurityTests TOGGLE_TABLESCANS=true

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE
