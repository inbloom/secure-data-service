#!/bin/bash

noTableScanAndCleanTomcat

resetDatabases

profileSwapAndPropGen

buildApi

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org versioningTests OTHER_TAGS=@DB_MIGRATION_BEFORE_API_STARTS


processApps $APPSTODEPLOY

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org versioningTests OTHER_TAGS=@DB_MIGRATION_AFTER_API_STARTS




cd $WORKSPACE/sli/acceptance-tests/test/features/utils
ruby schema-migration-prep.rb ../../../../domain/src/main/resources/sliXsd/ComplexTypes.xsd ../../../../data-access/dal/src/test/resources/migration/acc-test-migration-config.json ../../../../data-access/dal/src/main/resources/migration/migration-config.json


buildApi

processApps $APPSTODEPLOY

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org versioningTests OTHER_TAGS=@DB_MIGRATION_AFTER_UPVERSIONING

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"
/usr/sbin/restart_tomcat

exit $EXITCODE








