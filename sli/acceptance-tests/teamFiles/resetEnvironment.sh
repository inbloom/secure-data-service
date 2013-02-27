#!/bin/sh
kill `ps aux | grep 'rails server' | grep -v grep | awk {'print $2'}`
kill `ps aux | grep 'target/search-indexer-1.0-SNAPSHOT.jar' | grep -v grep | awk {'print $2'}`
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/api
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/dashboard
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/ingestion-service
#curl http://tomcat:s3cret@localhost/manager/text/stop?path=/mock-zis
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/sample
#curl http://tomcat:s3cret@localhost/manager/text/stop?path=/sif-agent
curl http://tomcat:s3cret@localhost/manager/text/stop?path=/simple-idp
cd /opt/megatron/sli/sli/config/scripts/
sh resetAllDbs.sh
cd /opt/megatron/sli/sli/admin-tools/admin-rails/
bundle install --deployment
bundle exec rails server -d
cd /opt/megatron/sli/sli/databrowser/
bundle install --deployment
bundle exec rails server -d
cd /opt/megatron/sli/sli/search-indexer/
scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/opt/tomcat/apache-tomcat-7.0.34/conf/sli.properties -Dsli.encryption.keyStore=/opt/tomcat/apache-tomcat-7.0.34/encryption/ciKeyStore.jks -Dlock.dir=data/
curl http://tomcat:s3cret@localhost/manager/text/start?path=/api
curl http://tomcat:s3cret@localhost/manager/text/start?path=/dashboard
curl http://tomcat:s3cret@localhost/manager/text/start?path=/ingestion-service
#curl http://tomcat:s3cret@localhost/manager/text/start?path=/mock-zis
curl http://tomcat:s3cret@localhost/manager/text/start?path=/sample
#curl http://tomcat:s3cret@localhost/manager/text/start?path=/sif-agent
curl http://tomcat:s3cret@localhost/manager/text/start?path=/simple-idp