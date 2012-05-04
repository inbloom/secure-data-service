#!/bin/sh

if [ -z "$SLI_ROOT" ]; then
    echo "Need to set SLI_ROOT to the fully qualified path of your local SLI git source tree."
	exit 1
fi

cd ${SLI_ROOT}/acceptance-tests

export MAVEN_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.conf=${SLI_ROOT}/config/properties/sli.properties -Dsli.env=local -Dsli.encryption.keyStore=${SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks -Dsli.encryption.properties=${SLI_ROOT}/data-access/dal/keyStore/localEncryption.properties"
export TOMCAT_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.conf=${SLI_ROOT}/config/properties/sli.properties -Dsli.env=local -Dsli.encryption.keyStore=${SLI_ROOT}/data-access/dal/keyStore/localKeyStore.jks -Dsli.encryption.properties=${SLI_ROOT}/data-access/dal/keyStore/localEncryption.properties"

echo 'Running acceptance tests...'
mkdir -p target/logs
mvn integration-test 

