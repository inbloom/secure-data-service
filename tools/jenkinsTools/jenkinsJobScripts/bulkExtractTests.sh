#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

processApps $APPSTODEPLOY

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true bulk_extract_script=$WORKSPACE/sli/bulk-extract/scripts/local_bulk_extract.sh bulk_extract_properties_file=/opt/tomcat/conf/sli.properties bulk_extract_keystore_file=/opt/tomcat/encryption/ciKeyStore.jks bulk_extract_jar_loc=$WORKSPACE/sli/bulk-extract/target/bulk_extract.tar.gz bulkExtractTests

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE