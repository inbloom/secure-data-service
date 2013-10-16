curlStop()
{
  APP=$1
  curl "http://tomcat:s3cret@localhost:8080/manager/text/stop?path=/$APP"
}

curlUndeploy()
{
  APP=$1
  curl "http://tomcat:s3cret@localhost:8080/manager/text/undeploy?path=/$APP"
}

curlDeploy()
{
  APP=$1
  APPFILEPATH=$2
  echo "Deploy app $APP to path $APPFILEPATH"
  resp=`curl "http://tomcat:s3cret@localhost:8080/manager/text/deploy?path=/$APP&war=file:$APPFILEPATH"`
  if [[ $resp =~ FAIL.* ]] ; then echo "Application $APP failed to deploy" ; exit 1 ; fi
}

processApps()
{
  apps=$@
  echo "apps are $apps"
  for var in $apps
  do
    echo $var
    curlStop $var
    curlUndeploy $var
    curlDeploy $var ${deployHash[$var]}
  done
}

profileSwapAndPropGen()
{
  cd $WORKSPACE/sli
  sh profile_swap.sh $NODE_NAME
  cd config/scripts
  ruby webapp-provision.rb ../config.in/canonical_config.yml team /opt/tomcat/conf/sli.properties
  ruby webapp-provision.rb ../config.in/canonical_config.yml team ../properties/sli.properties
  cp $WORKSPACE/sli/data-access/dal/keyStore/ci* /opt/tomcat/encryption/
  cp $WORKSPACE/sli/common/common-encrypt/trust/* /opt/tomcat/trust/
  cp $WORKSPACE/sli/data-access/dal/keyStore/trustey.jks /opt/tomcat/encryption/ciTruststore.jks
}

profileSwapAndPropGenSB()
{
  cd $WORKSPACE/sli
  sh profile_swap.sh $NODE_NAME
  cd config/scripts
  ruby webapp-provision.rb ../config.in/canonical_config.yml sandbox /opt/tomcat/conf/sli.properties
  cp $WORKSPACE/sli/data-access/dal/keyStore/ci* /opt/tomcat/encryption/ 
  cp $WORKSPACE/sli/common/common-encrypt/trust/* /opt/tomcat/trust/
  cp $WORKSPACE/sli/data-access/dal/keyStore/trustey.jks /opt/tomcat/encryption/ciTruststore.jks
}

resetDatabases()
{
  cd $WORKSPACE/sli/config/scripts
  sh resetAllDbs.sh
}

startSearchIndexer()
{
  cd $WORKSPACE/sli/search-indexer
  scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/opt/tomcat/conf/sli.properties -Dsli.encryption.keyStore=/opt/tomcat/encryption/ciKeyStore.jks -Dlock.dir=data/

}

noTableScanAndCleanTomcat()
{
  mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"
  /usr/sbin/cleanup_tomcat
}

adminUnitTests()
{
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --path $WORKSPACE/../vendors/
  bundle exec rake ci:setup:testunit test
  code=$?
  if [ "$code" != "0" ]; then
    exit $code
  fi
}

databrowserUnitTests()
{
  cd $WORKSPACE/sli/databrowser
  bundle install --deployment
  bundle exec rake ci:setup:testunit test
  code=$?
  if [ "$code" != "0" ]; then
    exit $code
  fi
}

profileSwap(){
  cd $WORKSPACE/sli/
  sh profile_swap.sh $NODE_NAME
}

deployAdmin()
{
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --path $WORKSPACE/../vendors/
  bundle exec thin start -C config/thin.yml -e team
}

unDeployAdmin()
{
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --path $WORKSPACE/../vendors/
  bundle exec thin stop -C config/thin.yml

  ln=`ls /tmp/pid/ | wc -l`

  if [ "$ln" -ne "0" ]
  then
    echo "admin is still running, killing"
    pid=`cat /tmp/pid/thin-admin.pid`
    sudo kill $pid
    rm /tmp/pid/thin-admin.pid
  fi
  echo "Admin is shutdown"
}

deployAdminSB()
{
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --path $WORKSPACE/../vendors/
  bundle exec thin start -C config/thin.yml -e team_sb
}

deployDatabrowser()
{
  cd $WORKSPACE/sli/databrowser
  bundle install --deployment
  bundle exec cap team deploy -s subdomain=$NODE_NAME -S branch=$GITCOMMIT
}

buildApi()
{
  cd $WORKSPACE/sli
  /jenkins/tools/Maven/bin/mvn -pl api -am -ff -P team -Dmaven.test.failure.ignore=false -Dsli.env=team -Dsli.dev.subdomain=$NODE_NAME clean install -DskipTests=true
}

buildApiDocumentationArtifacts()
{
cd $WORKSPACE/sli/config/scripts/documentation
./generate_doc_artifacts.sh
}







