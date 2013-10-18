#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGenSB

unDeployAdmin
profileSwap
deployAdminSB

deployDatabrowser

startSearchIndexer

processApps $APPSTODEPLOY

Xvfb :4 -screen 0 1024x768x24 >/dev/null 2>&1 &
export DISPLAY=:4.0
cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true sampleApp_server_address=https://$NODE_NAME.slidev.org/ dashboard_server_address=https://$NODE_NAME.slidev.org dashboard_api_server_uri=https://$NODE_NAME.slidev.org realm_page_url=https://$NODE_NAME.slidev.org/api/oauth/authorize admintools_server_url=https://$NODE_NAME.slidev.org:2001 api_server_url=https://$NODE_NAME.slidev.org api_ssl_server_url=https://$NODE_NAME.slidev.org:8443 databrowser_server_url=https://$NODE_NAME.slidev.org:2000 ingestion_landing_zone=/home/ingestion/lz/inbound portal_server_address=https://$NODE_NAME.slidev.org ldap_base=ou=${LDAP_NODE},dc=slidev,dc=org ci_idp_redirect_url=https://$NODE_NAME.slidev.org/simple-idp?realm=IL-Daybreak bulk_extract_script=$WORKSPACE/sli/bulk-extract/scripts/local_bulk_extract.sh bulk_extract_properties_file=/opt/tomcat/conf/sli.properties bulk_extract_keystore_file=/opt/tomcat/encryption/ciKeyStore.jks bulk_extract_jar_loc=$WORKSPACE/sli/bulk-extract/target/bulk_extract.tar.gz rcSandboxTests

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"
unDeployAdmin
exit $EXITCODE