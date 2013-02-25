#!/bin/sh
if [ -z "$1" ]; then
    echo "Branch defaulted to master"
    branch="master"
else
    branch=$1
fi
kill `ps aux | grep 'rails server' | grep -v grep | awk {'print $2'}`
kill `ps aux | grep 'target/search-indexer-1.0-SNAPSHOT.jar' | grep -v grep | awk {'print $2'}`
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/api
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/api
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/dashboard
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/dashboard
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/ingestion-service
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/ingestion-service
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/mock-zis
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/mock-zis
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/sample
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/sample
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/sif-agent
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/sif-agent
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/simple-idp
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/simple-idp

cd /opt/megatron/sli/
set -e
git fetch
git checkout $branch
git pull
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/devmegatron_application_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/application_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/devmegatron_realm_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/realm_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/devmegatron_securityEvent_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/securityEvent_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/features/utils/teamProps/devmegatron_properties.yml /opt/megatron/sli/sli/acceptance-tests/test/features/utils/properties.yml
cp /opt/megatron/sli/sli/admin-tools/admin-rails/config/devmegatron_admin_config.yml /opt/megatron/sli/sli/admin-tools/admin-rails/config/config.yml
cp /opt/megatron/sli/sli/databrowser/config/devmegatron_databrowser_config.yml /opt/megatron/sli/sli/databrowser/config/config.yml
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/devmegatron.properties /opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/devmegatron.properties /opt/megatron/sli/sli/config/properties/sli.properties

