#sudo chown -R sshan /opt/megatron/
#sudo chown -R sshan /opt/tomcat/apache-tomcat-7.0.34/
cd /opt/megatron/sli/sli/acceptance-tests/

rm ./test/data/teamData/devmegatron_application_fixture.json
rm ./test/data/teamData/devmegatron_realm_fixture.json
rm ./test/data/teamData/devmegatron_securityEvent_fixture.json
rm ./test/features/utils/teamProps/devmegatron_properties.yml
rm ./teamFiles/devmegatron_sandbox.properties
rm ./teamFiles/devmegatron.properties
rm ./teamFiles/devmegatron.sh
rm ./teamFiles/devmegatron_ingestion_performance.sh
rm ./teamFiles/devmegatron_performance_testing_wrapper.sh
rm /opt/megatron/sli/sli/admin-tools/admin-rails/config/devmegatron_admin_config.yml
rm /opt/megatron/sli/sli/databrowser/config/devmegatron_databrowser_config.yml
rm /opt/megatron/sli/sli/acceptance-tests/teamFiles/checkoutAndBuild_devmegatron.sh

sed 's/megtomcat01/devmegatron/g' ./test/data/teamData/megtomcat01_application_fixture.json >>./test/data/teamData/devmegatron_application_fixture.json 
sed 's/megtomcat01/devmegatron/g' ./test/data/teamData/megtomcat01_realm_fixture.json >> ./test/data/teamData/devmegatron_realm_fixture.json
sed 's/megtomcat01/devmegatron/g' ./test/data/teamData/megtomcat01_securityEvent_fixture.json >> ./test/data/teamData/devmegatron_securityEvent_fixture.json
sed 's/megtomcat01/devmegatron/g' ./test/features/utils/teamProps/megtomcat01_properties.yml >> ./test/features/utils/teamProps/devmegatron_properties.yml
#sed 's/megtomcat01/devmegatron/g' ./teamFiles/megtomcat01_sandbox.properties >> ./teamFiles/devmegatron_sandbox.properties
sed 's/megtomcat01/devmegatron/g' ./teamFiles/megtomcat01.properties >> ./teamFiles/devmegatron.properties
sed 's/megtomcat01/devmegatron/g' ./teamFiles/megtomcat01.sh >> ./teamFiles/devmegatron.sh
sed 's/megtomcat01/devmegatron/g' ./teamFiles/megtomcat01_ingestion_performance.sh >> ./teamFiles/devmegatron_ingestion_performance.sh
sed 's/megtomcat01/devmegatron/g' ./teamFiles/megtomcat01_performance_testing_wrapper.sh >> ./teamFiles/devmegatron_performance_testing_wrapper.sh
sed 's/megtomcat01/devmegatron/g' /opt/megatron/sli/sli/admin-tools/admin-rails/config/megtomcat01_admin_config.yml >> /opt/megatron/sli/sli/admin-tools/admin-rails/config/devmegatron_admin_config.yml
sed 's/megtomcat01/devmegatron/g' /opt/megatron/sli/sli/databrowser/config/megtomcat01_databrowser_config.yml >> /opt/megatron/sli/sli/databrowser/config/devmegatron_databrowser_config.yml
sed 's/megtomcat01/devmegatron/g' /opt/megatron/sli/sli/acceptance-tests/teamFiles/checkoutAndBuild.sh >> /opt/megatron/sli/sli/acceptance-tests/teamFiles/checkoutAndBuild_devmegatron.sh
cd /opt/megatron/sli/sli/acceptance-tests/
find . -name "devmegatron*"
