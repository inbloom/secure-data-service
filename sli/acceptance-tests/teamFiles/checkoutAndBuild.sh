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
#curl http://tomcat:s3cret@localhost/manager/text/stop?path=/mock-zis
#curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/mock-zis
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/sample
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/sample
#curl http://tomcat:s3cret@localhost/manager/text/stop?path=/sif-agent
#curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/sif-agent
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/simple-idp
curl http://tomcat:s3cret@localhost/manager/text/undeploy?path=/simple-idp
cd /opt/megatron/sli/
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/application_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/realm_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/securityEvent_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/features/utils/properties.yml
git checkout -- /opt/megatron/sli/sli/admin-tools/admin-rails/config/config.yml
git checkout -- /opt/megatron/sli/sli/databrowser/config/config.yml
git checkout -- /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01.properties
set -e
git fetch
git checkout $branch
git pull
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/megtomcat01_application_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/application_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/megtomcat01_realm_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/realm_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/megtomcat01_securityEvent_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/securityEvent_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/features/utils/teamProps/megtomcat01_properties.yml /opt/megatron/sli/sli/acceptance-tests/test/features/utils/properties.yml
cp /opt/megatron/sli/sli/admin-tools/admin-rails/config/megtomcat01_admin_config.yml /opt/megatron/sli/sli/admin-tools/admin-rails/config/config.yml
cp /opt/megatron/sli/sli/databrowser/config/megtomcat01_databrowser_config.yml /opt/megatron/sli/sli/databrowser/config/config.yml
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01.properties /opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01.properties /opt/megatron/sli/sli/config/properties/sli.properties
#cd /opt/megatron/sli/sli/acceptance-tests/test/data/
#sed -i.bk 's#\:8[0-9][0-9][0-9]/#/#g' application_fixture.json
#sed -i.bk2 's#lY83c5HmTPX#XY83c5HmTPX#g' application_fixture.json
#sed -i.bk3 's#local.slidev.org#megtomcat01.slidev.org#g' application_fixture.json
#sed -i.bk 's#\:8[0-9][0-9][0-9]/#/#g' realm_fixture.json
#sed -i.bk2 's#local.slidev.org#megtomcat01.slidev.org#g' realm_fixture.json
#sed -i.bk 's#\:8[0-9][0-9][0-9]/#/#g' securityEvent_fixture.json
#sed -i.bk2 's#local.slidev.org#megtomcat01.slidev.org#g' securityEvent_fixture.json
#cd /opt/megatron/sli/sli/databrowser/config/
#sed -i.bk 's#/jsonws/headerfooter#/jsonws/headerfooter-not#g' config.yml
#sed -i.bk2 's#:8080/#/#g' config.yml
#sed -i.bk3 's#local.slidev.org#megtomcat01.slidev.org#g' config.yml
#cd /opt/megatron/sli/sli/admin-tools/admin-rails/config/
#sed -i.bk 's#:8080/#/#g' config.yml
#sed -i.bk2 's#local.slidev.org#megtomcat01.slidev.org#g' config.yml
#cd /opt/megatron/sli/sli/config/scripts/
#ruby webapp-provision.rb ../config.in/canonical_config.yml local ../properties/sli.properties
#cd ../properties/
#sed -i.bk 's#\:8[0-9][0-9][0-9]/#/#g' sli.properties
#sed -i.bk2 's#api.perf.log.path = target/apilogs/logs#api.perf.log.path = /opt/tomcat/apache-tomcat-7.0.34/logs/#g' sli.properties
#sed -i.bk3 's#log.path = target/logs#log.path = /opt/tomcat/apache-tomcat-7.0.34/logs/#g' sli.properties
#sed -i.bk4 's#sli.search.indexer.log.path = logs#sli.search.indexer.log.path = /opt/tomcat/apache-tomcat-7.0.34/logs/#g' sli.properties
#sed -i.bk5 's#sli.tenant.landingZoneMountPoint = target/ingestion/lz/inbound/#sli.tenant.landingZoneMountPoint = /opt/ingestion/lz/inbound/#g' sli.properties
#sed -i.bk6 's#landingzone.inbounddir = target/ingestion/lz/inbound#landingzone.inbounddir = /opt/ingestion/lz/inbound/#g' sli.properties
#sed -i.bk7 's#logging.path = target/ingestion/logs#logging.path = /opt/ingestion/logs/#g' sli.properties
#sed -i.bk8 's#dashboard.minify.js = false#dashboard.minify.js = true#g' sli.properties
#sed -i.bk9 's#sli.dev.subdomain = ci#sli.dev.subdomain = megtomcat01#g' sli.properties
#sed -i.bk10 's#:8080##g' sli.properties
#sed -i.bk11 's#sli.trust.certificates = ../common/common-encrypt/trust/trustedCertificates#sli.trust.certificates = /opt/tomcat/apache-tomcat-7.0.34/trust/trustedCertificates#g' sli.properties
#sed -i.bk12 's#dashboard.encryption.keyStore = ../data-access/dal/keyStore/ciKeyStore.jks#dashboard.encryption.keyStore = /opt/tomcat/apache-tomcat-7.0.34/encryption/ciKeyStore.jks#g' sli.properties
#sed -i.bk13 's#sli.encryption.keyStore = ../data-access/dal/keyStore/ciKeyStore.jks#sli.encryption.keyStore = /opt/tomcat/apache-tomcat-7.0.34/encryption/ciKeyStore.jks#g' sli.properties
#sed -i.bk14 's#local.slidev.org#megtomcat01.slidev.org#g' sli.properties
#sed -i.bk15 's#bootstrap.app.sif.url = http://megtomcat01.slidev.org:1338/#bootstrap.app.sif.url = http://megtomcat01.slidev.org/sif-agent#g' sli.properties
#sed -i.bk16 's#bootstrap.app.sif.apiUrl = http://megtomcat01.slidev.org/#bootstrap.app.sif.apiUrl = http://megtomcat01.slidev.org/api#g' sli.properties
#cp sli.properties /opt/tomcat/apache-tomcat-7.0.34/conf/
#cd /opt/megatron/sli/sli/acceptance-tests/test/features/utils/
#sed -i.bk 's#:8[0-9][0-9][0-9]##g' properties.yml
#sed -i.bk2 's#ingestion_properties_file: "../config/properties/sli.properties"#ingestion_properties_file: "/opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties"#g' properties.yml
#sed -i.bk3 's#ingestion_log_directory: "../ingestion/ingestion-service/target/ingestion/logs/"#ingestion_log_directory: "/opt/ingestion/logs/"#g' properties.yml
#sed -i.bk4 's#ci_idp_redirect_url: https://localhost/simple-idp?realm=IL-Daybreak#ci_idp_redirect_url: https://local.slidev.org/simple-idp?realm=IL-Daybreak#g' properties.yml
#sed -i.bk5 's#local.slidev.org#megtomcat01.slidev.org#g' properties.yml
cd /opt/megatron/sli/sli/config/scripts/
sh resetAllDbs.sh
cd /opt/megatron/sli/sli/admin-tools/admin-rails/
bundle install --deployment
bundle exec rails server -d
cd /opt/megatron/sli/sli/databrowser/
bundle install --deployment
bundle exec rails server -d
#cd /opt/megatron/sli/tools/odin
#bundle install --deployment
#bundle exec rake test
cd /opt/megatron/sli/sli/
export M2_HOME=/usr/local/apache-maven/apache-maven-3.0.4
export M2=$M2_HOME/bin
export PATH=$M2:$PATH
mvn -version
mvn clean package install -DskipTests -Dpmd.skip=true
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/api&war=file:/opt/megatron/sli/sli/api/target/api.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/dashboard&war=file:/opt/megatron/sli/sli/dashboard/target/dashboard.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/ingestion-service&war=file:/opt/megatron/sli/sli/ingestion/ingestion-service/target/ingestion-service.war'
#curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/mock-zis&war=file:/opt/megatron/sli/sli/sif/mock-zis/target/mock-zis.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/sample&war=file:/opt/megatron/sli/sli/SDK/sample/target/sample.war'
#curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/sif-agent&war=file:/opt/megatron/sli/sli/sif/sif-agent/target/sif-agent.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/simple-idp&war=file:/opt/megatron/sli/sli/simple-idp/target/simple-idp.war'
cd /opt/megatron/sli/sli/search-indexer/
scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties -Dsli.encryption.keyStore=/opt/tomcat/apache-tomcat-7.0.34/encryption/ciKeyStore.jks -Dlock.dir=data/
