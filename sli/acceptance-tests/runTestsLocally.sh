#!/bin/sh

# wip

if [ -z "$SLI_ROOT" ]; then
    echo "Need to set SLI_ROOT to the fully qualified path of your local SLI git source tree."
    echo "Example: export SLI_ROOT=/users/chung/Documents/workspace/sli/sli"
	exit 1
fi

echo "Shutting down databrowser and admin-rails..."
if [ -f ${SLI_ROOT}/databrowser/tmp/pids/server.pid ]; then
	kill -9 < ${SLI_ROOT}/databrowser/tmp/pids/server.pid
	rm -f ${SLI_ROOT}/databrowser/tmp/pids/server.pid
fi

if [ -f ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid ]; then
	kill -9 < ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid
	rm -f ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid
fi

echo "Generating properties file..."
${SLI_ROOT}/config/scripts/webapp-provision.rb ${SLI_ROOT}/config/config.in/canonical_config.yml local-acceptance-tests ${SLI_ROOT}/config/properties/sli.properties
echo "Done."

cd ${SLI_ROOT}/acceptance-tests

export MAVEN_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.conf=${SLI_ROOT}/config/properties/sli.properties -Dsli.env=local -Dsli.encryption.keyStore=${SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks"

echo 'Running acceptance tests...'
mkdir -p target/logs

if [ -z ${1} ]; then
   export TESTS_TO_RUN="integrationTests"
else
   export TESTS_TO_RUN=${1}
fi
echo "Running test: ${TESTS_TO_RUN}"
mvn integration-test

echo "Shutting down databrowser and admin-rails..."
if [ -f ${SLI_ROOT}/databrowser/tmp/pids/server.pid ]; then
	kill -9 < ${SLI_ROOT}/databrowser/tmp/pids/server.pid
	rm -f ${SLI_ROOT}/databrowser/tmp/pids/server.pid
fi

if [ -f ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid ]; then
	kill -9 < ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid
	rm -f ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid
fi
