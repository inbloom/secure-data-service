#!/bin/sh

# wip

if [ -z "$SLI_ROOT" ]; then
    echo "Need to set SLI_ROOT to the fully qualified path of your local SLI git source tree."
    echo "Example: export SLI_ROOT=/users/chung/Documents/workspace/sli/sli"
	exit 1
fi

echo "Generating properties file..."
${SLI_ROOT}/config/scripts/webapp-provision.rb ${SLI_ROOT}/config/config.in/canonical_config.yml local-acceptance-tests ${SLI_ROOT}/config/properties/sli.properties
echo "Done."

cd ${SLI_ROOT}/acceptance-tests

export ADMIN_RAILS_DIR="${SLI_ROOT}/admin-tools/admin-rails"
export DATABROWSER_DIR="${SLI_ROOT}/databrowser"

export MAVEN_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -DADMIN_RAILS_DIR=${ADMIN_RAILS_DIR} -DDATABROWSER_DIR=${DATABROWSER_DIR} -Dsli.conf=${SLI_ROOT}/config/properties/sli.properties -Dsli.env=local -Dsli.encryption.keyStore=${SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks"

export BUNDLE_GEMFILE=Gemfile

echo 'Running acceptance tests...'
mkdir -p target/logs

if [ -z ${1} ]; then
   export TESTS_TO_RUN="integrationTests"
else
   export TESTS_TO_RUN=${1}
fi

echo "Starting rails apps..."
cd ${ADMIN_RAILS_DIR}
bundle install
bundle exec rails server -d -p 2000 -e local-integration-tests

cd ${DATABROWSER_DIR}
bundle install
bundle exec rails server -d -p 2001 -e local-integration-tests

cd ${SLI_ROOT}/acceptance-tests

echo "Running maven tests"
mvn integration-test

echo "Stopping rails apps..."
echo "Shutting down databrowser and admin-rails..."
if [ -f ${SLI_ROOT}/databrowser/tmp/pids/server.pid ]; then
	kill -9 < ${SLI_ROOT}/databrowser/tmp/pids/server.pid
	rm -f ${SLI_ROOT}/databrowser/tmp/pids/server.pid
fi

if [ -f ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid ]; then
	kill -9 < ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid
	rm -f ${SLI_ROOT}/admin-tools/admin-rails/tmp/pids/server.pid
fi
