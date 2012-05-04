#!/bin/sh

# wip

if [ -z "$SLI_ROOT" ]; then
    echo "Need to set SLI_ROOT to the fully qualified path of your local SLI git source tree."
    echo "Example: export SLI_ROOT=/users/chung/Documents/workspace/sli/sli"
	exit 1
fi

echo "Generating properties file..."
${SLI_ROOT}/config/scripts/webapp-provision.rb ${SLI_ROOT}/config/config.in/canonical_config.yml local ${SLI_ROOT}/config/properties/sli.properties
echo "Done."

echo "Building SLI project..."
mvn clean package install -DskipTests -f ${SLI_ROOT}/pom.xml

cd ${SLI_ROOT}/acceptance-tests

export MAVEN_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -Doauth.redirect=http://local.slidev.org:8080/dashboard/callback -Dsli.conf=${SLI_ROOT}/config/properties/sli.properties -Dsli.env=local -Dsli.encryption.keyStore=${SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks -Dsli.encryption.properties=${SLI_ROOT}/data-access/dal/keyStore/localEncryption.properties"
export TOMCAT_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -Doauth.redirect=http://local.slidev.org:8080/dashboard/callback -Dsli.conf=${SLI_ROOT}/config/properties/sli.properties -Dsli.env=local -Dsli.encryption.keyStore=${SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks -Dsli.encryption.properties=${SLI_ROOT}/data-access/dal/keyStore/localEncryption.properties"

echo 'Running acceptance tests...'
mkdir -p target/logs
mongod --logpath target/logs/mongod.log &
mvn integration-test
mongo admin
db.shutdownServer()