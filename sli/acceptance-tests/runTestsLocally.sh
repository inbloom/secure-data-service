#!/bin/sh

mkdir -p target/lz/inbound
mkdir -p target/logs

export MAVEN_OPTS='-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.env=local -Dsli.encryption.keyStore=../data-access/dal/keyStore/localKeyStore.jks -Dsli.encryption.properties=../data-access/dal/keyStore/localEncryption.properties -Dlogging.path=target/logs -Dlandingzone.inbounddir=/Users/ingestion/lz/inbound'

echo 'Running acceptance tests...'
mvn integration-test > target/logs/jetty6x.log 2>&1

