#!/bin/sh
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
set -e
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01.properties /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01_sandbox.properties
sed -i.bk1 's#sli.sandbox.enabled = false#sli.sandbox.enabled = true#g' megtomcat01.properties
sed -i.bk2 's#sli.autoRegisterApps = false#sli.autoRegisterApps = true#g' megtomcat01.properties
sed -i.bk3 's#bootstrap.sandbox.createSandboxRealm = false#bootstrap.sandbox.createSandboxRealm = true#g' megtomcat01.properties
sed -i.bk4 's#bootstrap.app.databrowser.authorized_for_all_edorgs = false#bootstrap.app.databrowser.authorized_for_all_edorgs = true#g' megtomcat01.properties
sed -i.bk5 's#bootstrap.app.dashboard.authorized_for_all_edorgs = false#bootstrap.app.dashboard.authorized_for_all_edorgs = true#g' megtomcat01.properties
sed -i.bk6 's#sli.simple-idp.sandboxImpersonationEnabled = false#sli.simple-idp.sandboxImpersonationEnabled = true#g' megtomcat01.properties
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01_sandbox.properties /opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01_sandbox.properties /opt/megatron/sli/sli/config/properties/sli.properties
cd /opt/megatron/sli/sli/config/scripts/
sh resetAllDbs.sh
cd /opt/megatron/sli/sli/admin-tools/admin-rails/
bundle install --deployment
bundle exec rails server -d -e development_sb
cd /opt/megatron/sli/sli/databrowser/
bundle install --deployment
bundle exec rails server -d
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/api&war=file:/opt/megatron/sli/sli/api/target/api.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/dashboard&war=file:/opt/megatron/sli/sli/dashboard/target/dashboard.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/ingestion-service&war=file:/opt/megatron/sli/sli/ingestion/ingestion-service/target/ingestion-service.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/mock-zis&war=file:/opt/megatron/sli/sli/sif/mock-zis/target/mock-zis.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/sample&war=file:/opt/megatron/sli/sli/SDK/sample/target/sample.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/sif-agent&war=file:/opt/megatron/sli/sli/sif/sif-agent/target/sif-agent.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/simple-idp&war=file:/opt/megatron/sli/sli/simple-idp/target/simple-idp.war'
cd /opt/megatron/sli/sli/search-indexer/
scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties -Dsli.encryption.keyStore=/opt/tomcat/apache-tomcat-7.0.34/encryption/ciKeyStore.jks -Dlock.dir=data/
cd /opt/megatron/sli/sli/acceptance-tests/
Xvfb :4 -screen 0 1024x768x24 >/dev/null 2>&1 &
export DISPLAY=:4.0
bundle exec rake FORCE_COLOR=true sampleApp_server_address=http://megtomcat01.slidev.org/ dashboard_server_address=http://megtomcat01.slidev.org dashboard_api_server_uri=http://megtomcat01.slidev.org realm_page_url=http://megtomcat01.slidev.org/api/oauth/authorize admintools_server_url=http://megtomcat01.slidev.org:3001 api_server_url=http://megtomcat01.slidev.org databrowser_server_url=http://megtomcat01.slidev.org:3000 ingestion_landing_zone=/opt/ingestion/lz/inbound sif_zis_address_trigger=http://megtomcat01.slidev.org:8080/mock-zis/trigger elastic_search_address=http://megtomcat01.slidev.org:9200 sandboxTests





