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
  curl "http://tomcat:s3cret@localhost:8080/manager/text/deploy?path=/$APP&war=file:$APPFILEPATH"
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
  cp $WORKSPACE/sli/data-access/dal/keyStore/ci* /opt/tomcat/encryption/ 
  cp $WORKSPACE/sli/common/common-encrypt/trust/* /opt/tomcat/trust/
}

profileSwapAndPropGenSB()
{
  cd $WORKSPACE/sli
  sh profile_swap.sh $NODE_NAME
  cd config/scripts
  ruby webapp-provision.rb ../config.in/canonical_config.yml sandbox /opt/tomcat/conf/sli.properties
  cp $WORKSPACE/sli/data-access/dal/keyStore/ci* /opt/tomcat/encryption/ 
  cp $WORKSPACE/sli/common/common-encrypt/trust/* /opt/tomcat/trust/
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
  bundle install --deployment
  bundle exec rake ci:setup:testunit test
}

databrowserUnitTests()
{
  cd $WORKSPACE/sli/databrowser
  bundle install --deployment
  bundle exec rake ci:setup:testunit test
}

deployAdmin()
{
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --deployment
  bundle exec cap team deploy -s subdomain=$NODE_NAME -S branch=$GITCOMMIT
}

deployAdminSB()
{
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundle install --deployment
  bundle exec cap team_sb deploy -s subdomain=$NODE_NAME -S branch=$GITCOMMIT
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










