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
                      "$OPTARG" != "prod-rc" && \
                      "$OPTARG" != "sandbox-rc" ]]; then
                    echo "Error: Environment must be one of ci|proc-rc|sandbox-rc"
                    show_usage
                    exit 1
                fi
                ENV=$OPTARG
                ;;
            a)
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
hostname=`hostname -s`

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
    sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.dev.inbloom.org/g" acceptance-tests/test/data/application_fixture.json
    sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.dev.inbloom.org/g" acceptance-tests/test/data/realm_fixture.json
    sed -i "s/http:\/\/local.slidev.org:8082/https:\/\/$hostname.dev.inbloom.org/g" acceptance-tests/test/data/realm_fixture.json
    sed -i "s/https:\/\/ci.slidev.org/https:\/\/$hostname.dev.inbloom.org/g" acceptance-tests/test/data/application_denial_fixture.json
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
  sudo chown -R rails:rails /opt/rails/admin/
  sudo ln -sf /etc/datastore/keyfile /opt/rails/admin/keyfile
  sudo ln -sf /etc/datastore/admin-config.yml /opt/rails/admin/admin-rails/config/config.yml
  rvmsudo bundle install
  sudo apachectl graceful
  sleep 10
  rvmsudo passenger-status
  echo "Admin Code Deployment Complete"
}
deployDatabrowser()
{
  echo "Deploying Databrowser code"
  sudo cp -R $WORKSPACE/sli/databrowser/* /opt/rails/databrowser/
  sudo chown -R rails:rails /opt/rails/databrowser/
  sudo ln -sf /etc/datastore/databrowser-config.yml /opt/rails/databrowser/config/config.yml
  rvmsudo bundle install --deployment
  sleep 10
  rvmsudo passenger-status
  echo "Databrowser Deployment Complete"
}

process_opts $@


if [[ "$ENV" == "ci" ]]; then
  noTableScan
  cleanTomcat
  cleanRails
  resetDatabases
  adminUnitTests
  databrowserUnitTests
  deployAdmin
  deployDatabrowser
fi

if [[ "$ENV" == "prod-rc" ]]; then
  echo "prod-rc foo"
fi

if [[ "$ENV" == "sandbox-rc" ]]; then
  echo "sandbox-rc foo"
fi
