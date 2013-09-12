#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

sed -i "s/sli\.app\.buildTag/$BUILD_TAG/g" /opt/tomcat/conf/sli.properties

startSearchIndexer

processApps $APPSTODEPLOY

Xvfb :6 -screen 0 1024x768x24 >/dev/null 2>&1 &
export DISPLAY=:6.0
cd $WORKSPACE/sli/acceptance-tests/
export LANG=en_US.UTF-8
export DEBUG=true
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org dashboard_server_address=https://$NODE_NAME.slidev.org dashboard_api_server_url=https://$NODE_NAME.slidev.org realm_page_url=https://$NODE_NAME.slidev.org ingestion_landing_zone=/home/ingestion/lz/inbound localDashboardTests TOGGLE_TABLESCANS=true

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE
