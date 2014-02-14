#!/bin/bash
source /usr/local/rvm/environments/default
#Need to change environment to tests or something. Evolved into something other than environment
show_usage() {
    echo "Usage: $0 [-Dh] -d DIR -e prod|sandbox|stage|test [-g GROUP [-s SERVER]]"
    echo
    echo "Options:"
    echo "  -d DIR         : Directory containing code files"
    echo "  -e ENV         : Tests for which this script should run"
    echo "  -m MODE        : Mode of the applications. production or sandbox"
    echo "  -a APPS        : List of Applications to deploy"
    echo "  -w WORKSPACE   : Specify Workspace if jenkins is not the one running this script"
    echo "  -h             : Show this usage summary and exit"
}

process_opts() {
    while getopts "d:e:m:a:w:h:" opt; do
        case $opt in
            d)
                CODEDIR=$OPTARG
                ;;
            e)
                if [[ "$OPTARG" != "ci" && \
                      "$OPTARG" != "ci_e2e_prod" && \
                      "$OPTARG" != "ci_e2e_sandbox" && \
                      "$OPTARG" != "api_contextual_roles" ]]; then
                    echo "Error: Environment must be one of ci|ci_e2e_prod|ci_e2e_sandbox|api_contextual_roles"
                    show_usage
                    exit 1
                fi
                ENV=$OPTARG
                ;;
             m)
                if [[ "$OPTARG" != "production" && \
                      "$OPTARG" != "sandbox" ]]; then
                    echo "Error: Mode must be one of production|sandbox"
                    show_usage
                    exit 1
                fi
                MODE=$OPTARG
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
    if [[ -z "$ENV" || -z "$APPS" || -z "$MODE" ]]; then
        echo "-e, -a, -m are required"
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
setMode()
{
    if [[ "$MODE" == "production" ]]; then
        sudo unlink /etc/datastore/sli.properties
        sudo unlink /etc/datastore/admin-config.yml
        sudo ln -s /etc/datastore/prod-sli.properties /etc/datastore/sli.properties
        sudo ln -s /etc/datastore/prod-admin-config.yml /etc/datastore/admin-config.yml
        echo "Switched to production mode"
    elif [[ "$MODE" == "sandbox" ]]; then
        sudo unlink /etc/datastore/sli.properties
        sudo unlink /etc/datastore/admin-config.yml
        sudo ln -s /etc/datastore/sandbox-sli.properties /etc/datastore/sli.properties
        sudo ln -s /etc/datastore/sandbox-admin-config.yml /etc/datastore/admin-config.yml
        echo "Switched to sandbox mode"
    else
        echo "You have specified an invalid mode. Must be either production or sandbox"
    fi
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
  setMode $MODE
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
  runTests PROPERTIES=/etc/datastore/test-properties.yml TOGGLE_TABLESCANS=true smokeTests
  EXITCODE=$?
fi

if [[ "$ENV" == "ci_e2e_prod" ]]; then
  noTableScan
  cleanTomcat
  cleanRails
  adminUnitTests
  databrowserUnitTests
  resetDatabases
  setMode $MODE
  deployAdmin
  deployDatabrowser
  startSearchIndexer

  for APP in $APPS; do
    deployTomcat $APP ${deployHash[$APP]}
  done
  echo "Waiting for APPS to finish deploying"
  sleep 120
  runTests PROPERTIES=/etc/datastore/test-properties.yml rcTests
  EXITCODE=$?
fi

if [[ "$ENV" == "ci_e2e_sandbox" ]]; then
  noTableScan
  cleanTomcat
  cleanRails
  adminUnitTests
  databrowserUnitTests
  resetDatabases
  setMode $MODE
  deployAdmin
  deployDatabrowser
  startSearchIndexer

  for APP in $APPS; do
    deployTomcat $APP ${deployHash[$APP]}
  done
  echo "Waiting for APPS to finish deploying"
  sleep 120
  runTests PROPERTIES=/etc/datastore/test-properties.yml rcSandboxTests
  EXITCODE=$?
fi

if [[ "$ENV" == "api_contextual_roles" ]]; then
	noTableScan
	cleanTomcat
	cleanRails
	resetDatabases
	setMode $MODE
	startSearchIndexer

	for APP in $APPS; do
  	deployTomcat $APP ${deployHash[$APP]}
  done
	echo "Waiting for APPS to finish deploying"
	sleep 120
	runTests PROPERTIES=/etc/datastore/test-properties.yml apiContextualRolesTests
  EXITCODE=$?
fi
exit $EXITCODE