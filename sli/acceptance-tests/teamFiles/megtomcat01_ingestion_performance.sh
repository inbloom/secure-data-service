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
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/application_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/realm_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/data/securityEvent_fixture.json
git checkout -- /opt/megatron/sli/sli/acceptance-tests/test/features/utils/properties.yml
git checkout -- /opt/megatron/sli/sli/admin-tools/admin-rails/config/config.yml
git checkout -- /opt/megatron/sli/sli/databrowser/config/config.yml
set -e
git fetch
git checkout $branch
git pull
cd /opt/megatron/sli/build-tools/
mvn clean package install
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/megtomcat01_application_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/application_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/megtomcat01_realm_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/realm_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/data/teamData/megtomcat01_securityEvent_fixture.json /opt/megatron/sli/sli/acceptance-tests/test/data/securityEvent_fixture.json
cp /opt/megatron/sli/sli/acceptance-tests/test/features/utils/teamProps/megtomcat01_properties.yml /opt/megatron/sli/sli/acceptance-tests/test/features/utils/properties.yml
cp /opt/megatron/sli/sli/admin-tools/admin-rails/config/megtomcat01_admin_config.yml /opt/megatron/sli/sli/admin-tools/admin-rails/config/config.yml
cp /opt/megatron/sli/sli/databrowser/config/megtomcat01_databrowser_config.yml /opt/megatron/sli/sli/databrowser/config/config.yml
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01.properties /opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties
cp /opt/megatron/sli/sli/acceptance-tests/teamFiles/megtomcat01.properties /opt/megatron/sli/sli/config/properties/sli.properties
cd /opt/megatron/sli/sli/config/scripts/
sh resetAllDbs.sh
cd /opt/megatron/sli/sli/admin-tools/admin-rails/
bundle install --deployment
bundle exec rails server -d
cd /opt/megatron/sli/sli/databrowser/
bundle install --deployment
bundle exec rails server -d
cd /opt/megatron/sli/sli/
mvn clean package install -DskipTests -Dpmd.skip=true
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/api&war=file:/opt/megatron/sli/sli/api/target/api.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/dashboard&war=file:/opt/megatron/sli/sli/dashboard/target/dashboard.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/ingestion-service&war=file:/opt/megatron/sli/sli/ingestion/ingestion-service/target/ingestion-service.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/mock-zis&war=file:/opt/megatron/sli/sli/sif/mock-zis/target/mock-zis.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/sample&war=file:/opt/megatron/sli/sli/SDK/sample/target/sample.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/sif-agent&war=file:/opt/megatron/sli/sli/sif/sif-agent/target/sif-agent.war'
curl 'http://tomcat:s3cret@localhost/manager/text/deploy?path=/simple-idp&war=file:/opt/megatron/sli/sli/simple-idp/target/simple-idp.war'
cd /opt/megatron/sli/sli/search-indexer/
scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties -Dsli.encryption.keyStore=/opt/tomcat/apache-tomcat-7.0.34/encryption/ciKeyStore.jks -Dlock.dir=data/
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 1 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 2 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
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
cd /opt/megatron/sli/sli/config/scripts/
sh resetAllDbs.sh
cd /opt/megatron/sli/sli/admin-tools/admin-rails/
bundle install --deployment
bundle exec rails server -d
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
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 1 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 2 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
