#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

startSearchIndexer

processApps $APPSTODEPLOY

Xvfb :6 -screen 0 1024x768x24 >/dev/null 2>&1 &
export DISPLAY=:6.0
cd $WORKSPACE/sli/acceptance-tests/
export LANG=en_US.UTF-8
export DEBUG=true
bundle install --deployment
bundle exec rake importUnifiedData
bundle exec rake realmInitNoPeople
bundle exec rake FORCE_COLOR=true sampleApp_server_address=https://$NODE_NAME.slidev.org/ api_server_url=https://$NODE_NAME.slidev.org realm_page_url=https://$NODE_NAME.slidev.org JavaSDKTests TOGGLE_TABLESCANS=1

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE

