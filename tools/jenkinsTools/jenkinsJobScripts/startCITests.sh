#!/bin/bash
source /usr/local/rvm/environments/default

show_usage() {
    echo "Usage: $0 [-Dh] -d DIR -e prod|sandbox|stage|test [-g GROUP [-s SERVER]]"
    echo
    echo "Options:"
    echo "  -d DIR         : Directory containing code files"
    echo "  -e ENV         : Environment for which this script should run against. ci,prod-rc,sandbox-rc"
    echo "  -a APPS        : List of Applications to deploy"
    echo "  -w WORKSPACE   : Specify Workspace if jenkins is not the one running this script"
    echo "  -h             : Show this usage summary and exit"
}

process_opts() {
    while getopts "d:e:a:w:h:" opt; do
        case $opt in
            d)
                CODEDIR=$OPTARG
                ;;
            e)
                if [[ "$OPTARG" != "ci" && \
                      "$OPTARG" != "ci_e2e_prod" && \
                      "$OPTARG" != "sandbox-rc" ]]; then
                    echo "Error: Environment must be one of ci|ci_e2e_prod|sandbox-rc"
                    show_usage
                    exit 1
                fi
                ENV=$OPTARG
                ;;
            a)
                IFS=","
                #Implement some type of checking to make sure the specified apps match up to something in the deployHash
                APPS=$OPTARG
                ;;
            w)
                WORKSPACE=$OPTARG
                ;;
            h)
                show_usage
                exit
                ;;
            *)
                echo "Error: unknown option"
                show_usage
                exit 254
                ;;
        esac
    done
    # -e and -a are required
    if [[ -z "$ENV" || -z "$APPS" ]]; then
        echo "-e and -a are required"
        show_usage
        exit 1
    fi
}

process_opts $@

HOSTNAME=`hostname -s`

declare -A deployHash
deployHash=( [api]="$WORKSPACE/sli/api/target/api.war"
             [dashboard]="$WORKSPACE/sli/dashboard/target/dashboard.war"
             [simple-idp]="$WORKSPACE/sli/simple-idp/target/simple-idp.war"
             [sample]="$WORKSPACE/sli/SDK/sample/target/sample.war"
             [ingestion-service]="$WORKSPACE/sli/ingestion/ingestion-service/target/ingestion-service.war"
             [mock-zis]="$WORKSPACE/sli/sif/mock-zis/target/mock-zis.war"
             [sif-agent]="$WORKSPACE/sli/sif/sif-agent/target/sif-agent.war"
)

#Defined Functions (These can be in a seperate file, but for now in this one to make creation/testing easier)
noTableScan()
{
    mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"
    echo "Table Scanning Disabled"
}
cleanTomcat()
{
    sudo /etc/init.d/tomcat stop
    for i in `ls /opt/apache-tomcat-7.0.47/webapps |grep -v "manager" |grep -v "ROOT" |grep -v "docs"`; do
       sudo rm -r -f /opt/apache-tomcat-7.0.47/webapps/$i
    done
    sudo rm -r -f /opt/apache-tomcat-7.0.47/temp/*
    sudo rm -r -f /opt/apache-tomcat-7.0.47/Catalina/localhost/*
    sudo /etc/init.d/tomcat start
    echo "Removed deployed tomcat apps"
}
cleanRails()
{
    sudo rm -rf /opt/rails/admin/*
    sudo rm -rf /opt/rails/databrowser/*
    echo "Cleaned up rails apps"
}
resetDatabases()
{
  cd $WORKSPACE/sli/config/scripts
  sh resetAllDbs.sh
  echo "Dropped Databases"
}
#this function needs to be refactored to actually work with our URL schema
generateFixtureData()
{
    echo "Altering fixture data for applications to match..."
    sed -i "s/https:\/\/ci.slidev.org/https:\/\/$HOSTNAME.dev.inbloom.org/g" acceptance-tests/test/data/application_fixture.json
    sed -i "s/https:\/\/ci.slidev.org/https:\/\/$HOSTNAME.dev.inbloom.org/g" acceptance-tests/test/data/realm_fixture.json
    sed -i "s/http:\/\/local.slidev.org:8082/https:\/\/$HOSTNAME.dev.inbloom.org/g" acceptance-tests/test/data/realm_fixture.json
    sed -i "s/https:\/\/ci.slidev.org/https:\/\/$HOSTNAME.dev.inbloom.org/g" acceptance-tests/test/data/application_denial_fixture.json
}
adminUnitTests()
{
  echo "Executing admin unit tests"
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --path $WORKSPACE/../vendors/
  bundle exec rake ci:setup:testunit test
  code=$?
  if [ "$code" != "0" ]; then
    exit $code
  fi
  echo "Finished running admin unit tests."
}
databrowserUnitTests()
{
  echo "Executing databrowser unit tests"
  cd $WORKSPACE/sli/databrowser
  bundle install --full-index --deployment
  bundle exec rake ci:setup:testunit test
  code=$?
  if [ "$code" != "0" ]; then
    exit $code
  fi
  echo "Finished running databrowser unit tests"
}
#should convert these two into a single deployRails()
deployAdmin()
{
  echo "Deploying Admin code"
  sudo cp -R $WORKSPACE/sli/admin-tools/* /opt/rails/admin/
  sudo chown -R jenkins:jenkins /opt/rails/admin/
  sudo ln -sf /etc/datastore/keyfile /opt/rails/admin/keyfile
  sudo ln -sf /etc/datastore/admin-config.yml /opt/rails/admin/admin-rails/config/config.yml
  cd /opt/rails/admin/admin-rails/
  bundle install --deployment
  bundle exec rake -s assets:precompile
  sudo chown -R rails:rails /opt/rails/admin/
  sudo apachectl graceful
  echo "Admin Code Deployment Complete"
}
deployDatabrowser()
{
  echo "Deploying Databrowser code"
  sudo cp -R $WORKSPACE/sli/databrowser/* /opt/rails/databrowser/
  sudo chown -R jenkins:jenkins /opt/rails/databrowser/
  sudo ln -sf /etc/datastore/databrowser-config.yml /opt/rails/databrowser/config/config.yml
  cd /opt/rails/databrowser/
  bundle install --deployment
  bundle exec rake -s assets:precompile
  sudo chown -R rails:rails /opt/rails/admin/
  sudo apachectl graceful
  echo "Databrowser Deployment Complete"
}
deployTomcat()
{
  APP=$1
  SOURCE=$2
  sudo cp $2 /opt/apache-tomcat-7.0.47/webapps/
  sudo chown tomcat7:tomcat7 /opt/apache-tomcat-7.0.47/webapps/*
  echo "Deployed $APP"
}

startSearchIndexer()
{
  cd $WORKSPACE/sli/search-indexer
  scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/etc/datastore/sli.properties -Dsli.encryption.keyStore=/etc/datastore/sli-keystore.jks -Dlock.dir=data/
  echo "Started Search Indexer"
}

runTests()
{
  Xvfb :4 -screen 0 1024x768x24 >/dev/null 2>&1 &
  export DISPLAY=:4.0
  cd $WORKSPACE/sli/acceptance-tests
  export LANG=en_US.UTF-8
  bundle install --deployment
  bundle exec rake FORCE_COLOR=true $@
}


if [[ "$ENV" == "ci" ]]; then
  noTableScan
  cleanTomcat
  cleanRails
  resetDatabases
  adminUnitTests
  databrowserUnitTests
  deployAdmin
  deployDatabrowser
  startSearchIndexer

  for APP in $APPS; do
    deployTomcat $APP ${deployHash[$APP]}
  done
  echo "Waiting for APPS to finish deploying"
  sleep 120
  runTests PROPERTIES=/etc/datastore/test-properties.yml sampleApp_server_address=https://$HOSTNAME-sample.dev.inbloom.org/ dashboard_server_address=https://cislave-1-dashboard.dev.inbloom.org dashboard_api_server_uri=https://cislave-1-api.dev.inbloom.org realm_page_url=https://cislave-1-api.dev.inbloom.org/api/oauth/authorize admintools_server_url=https://cislave-1-admin.dev.inbloom.org api_server_url=https://cislave-1-api.dev.inbloom.org api_ssl_server_url=https://cislave-1-api.dev.inbloom.org ingestion_landing_zone=/ingestion/lz sif_zis_address_trigger=http://$NODE_NAME.slidev.org:8080/mock-zis/trigger bulk_extract_script=$WORKSPACE/sli/bulk-extract/scripts/local_bulk_extract.sh bulk_extract_properties_file=/etc/datastore/sli.properties bulk_extract_keystore_file=/etc/datastore/sli-keystore.jks bulk_extract_jar_loc=$WORKSPACE/sli/bulk-extract/target/bulk_extract.tar.gz TOGGLE_TABLESCANS=true smokeTests
  EXITCODE=$?
fi

if [[ "$ENV" == "ci_e2e_prod" ]]; then
  noTableScan
  cleanTomcat
  cleanRails
  adminUnitTests
  databrowserUnitTests
  resetDatabases
  deployAdmin
  deployDatabrowser
  startSearchIndexer

  for APP in $APPS; do
    deployTomcat $APP ${deployHash[$APP]}
  done
  echo "Waiting for APPS to finish deploying"
  sleep 120
  runTests PROPERTIES=/etc/datastore/test-properties.yml sampleApp_server_address=https://$HOSTNAME-sample.dev.inbloom.org/ dashboard_server_address=https://cislave-1-dashboard.dev.inbloom.org dashboard_api_server_uri=https://cislave-1-api.dev.inbloom.org realm_page_url=https://cislave-1-api.dev.inbloom.org/api/oauth/authorize admintools_server_url=https://cislave-1-admin.dev.inbloom.org api_server_url=https://cislave-1-api.dev.inbloom.org api_ssl_server_url=https://cislave-1-api.dev.inbloom.org ingestion_landing_zone=/ingestion/lz sif_zis_address_trigger=http://$NODE_NAME.slidev.org:8080/mock-zis/trigger bulk_extract_script=$WORKSPACE/sli/bulk-extract/scripts/local_bulk_extract.sh bulk_extract_properties_file=/etc/datastore/sli.properties bulk_extract_keystore_file=/etc/datastore/sli-keystore.jks bulk_extract_jar_loc=$WORKSPACE/sli/bulk-extract/target/bulk_extract.tar.gz rcTests
  EXITCODE=$?
fi

if [[ "$ENV" == "sandbox-rc" ]]; then
  echo "sandbox-rc foo"
fi
exit $EXITCODE