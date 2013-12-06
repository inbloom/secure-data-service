# function to abstract away 'bundle install', as we have a special way to do
# that for US5980 (use RubyGems mirror)
bundleInstall()
{
  nbundle install --full-index $*
}

curlStop()
{
  echo "**** begin curlStop with args $* ****"
  APP=$1
  curl "http://tomcat:s3cret@localhost:8080/manager/text/stop?path=/$APP"
  echo "**** end curlStop with args $* ****"
}

curlUndeploy()
{
  echo "**** begin curlUndeploy with args $* ****"
  APP=$1
  curl "http://tomcat:s3cret@localhost:8080/manager/text/undeploy?path=/$APP"
  echo "**** end curlUndeploy with args $* ****"
}

curlDeploy()
{
  echo "**** begin curlDeploy with args $* ****"
  APP=$1
  APPFILEPATH=$2
  echo "Deploy app $APP to path $APPFILEPATH"
  resp=`curl "http://tomcat:s3cret@localhost:8080/manager/text/deploy?path=/$APP&war=file:$APPFILEPATH"`
  if [[ $resp =~ FAIL.* ]] ; then echo "Application $APP failed to deploy" ; exit 1 ; fi
  echo "**** end curlDeploy with args $* ****"
}

processApps()
{
  echo "**** begin processApps with args $* ****"
  apps=$@
  echo "apps are $apps"
  for var in $apps
  do
    echo $var
    curlStop $var
    curlUndeploy $var
    curlDeploy $var ${deployHash[$var]}
  done
  echo "**** end processApps with args $* ****"
}

profileSwapAndPropGen()
{
  echo "**** begin profileSwapAndPropGen with args $* ****"
  cd $WORKSPACE/sli
  sh profile_swap.sh $NODE_NAME
  cd config/scripts
  ruby webapp-provision.rb ../config.in/canonical_config.yml team /opt/tomcat/conf/sli.properties
  ruby webapp-provision.rb ../config.in/canonical_config.yml team ../properties/sli.properties
  cp $WORKSPACE/sli/data-access/dal/keyStore/ci* /opt/tomcat/encryption/
  cp $WORKSPACE/sli/common/common-encrypt/trust/* /opt/tomcat/trust/
  cp $WORKSPACE/sli/data-access/dal/keyStore/trustey.jks /opt/tomcat/encryption/ciTruststore.jks
  echo "**** end profileSwapAndPropGen with args $* ****"
}

profileSwapAndPropGenSB()
{
  echo "**** begin profileSwapAndPropGenSB with args $* ****"
  cd $WORKSPACE/sli
  sh profile_swap.sh $NODE_NAME
  cd config/scripts
  ruby webapp-provision.rb ../config.in/canonical_config.yml sandbox /opt/tomcat/conf/sli.properties
  cp $WORKSPACE/sli/data-access/dal/keyStore/ci* /opt/tomcat/encryption/ 
  cp $WORKSPACE/sli/common/common-encrypt/trust/* /opt/tomcat/trust/
  cp $WORKSPACE/sli/data-access/dal/keyStore/trustey.jks /opt/tomcat/encryption/ciTruststore.jks
  echo "**** end profileSwapAndPropGenSB with args $* ****"
}

resetDatabases()
{
  echo "**** begin resetDatabases with args $* ****"
  cd $WORKSPACE/sli/config/scripts
  sh resetAllDbs.sh
  echo "**** end resetDatabases with args $* ****"
}

startSearchIndexer()
{
  echo "**** begin startSearchIndexer with args $* ****"
  cd $WORKSPACE/sli/search-indexer
  scripts/local_search_indexer.sh restart target/search_indexer.tar.gz -Dsli.conf=/opt/tomcat/conf/sli.properties -Dsli.encryption.keyStore=/opt/tomcat/encryption/ciKeyStore.jks -Dlock.dir=data/
  echo "**** end startSearchIndexer with args $* ****"
}

noTableScanAndCleanTomcat()
{
  echo "**** begin noTableScanAndCleanTomcat with args $* ****"
  mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"
  /usr/sbin/cleanup_tomcat
  echo "**** end noTableScanAndCleanTomcat with args $* ****"
}

adminUnitTests()
{
  echo "**** begin adminUnitTests with args $* ****"
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundleInstall --path $WORKSPACE/../vendors/
  bundle exec rake ci:setup:testunit test
  code=$?
  if [ "$code" != "0" ]; then
    exit $code
  fi
  echo "**** end adminUnitTests with args $* ****"
}

databrowserUnitTests()
{
  echo "**** begin databrowserUnitTests with args $* ****"
  cd $WORKSPACE/sli/databrowser
  bundleInstall --full-index --deployment
  bundle exec rake ci:setup:testunit test
  code=$?
  if [ "$code" != "0" ]; then
    exit $code
  fi
  echo "**** end databrowserUnitTests with args $* ****"
}

profileSwap()
{
  echo "**** begin profileSwap with args $* ****"
  cd $WORKSPACE/sli/
  sh profile_swap.sh $NODE_NAME
  echo "**** end profileSwap with args $* ****"
}

deployAdmin()
{
  echo "**** begin deployAdmin with args $* ****"
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundleInstall --path $WORKSPACE/../vendors/
  bundle exec thin start -C config/thin.yml -e team
  echo "**** end deployAdmin with args $* ****"
}

unDeployAdmin()
{
  echo "**** begin unDeployAdmin with args $* ****"
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundleInstall --path $WORKSPACE/../vendors/
  bundle exec thin stop -C config/thin.yml

  ln=`ls /tmp/pid-admin/thin-admin.pid | wc -l`

  if [ "$ln" -ne "0" ]
  then
    echo "admin is still running, killing"
    pid=`cat /tmp/pid-admin/thin-admin.pid`
    sudo kill $pid
    rm /tmp/pid-admin/thin-admin.pid
  fi
  echo "Admin is shutdown"
  echo "**** end unDeployAdmin with args $* ****"
}

deployAdminSB()
{
  echo "**** begin deployAdminSB with args $* ****"
  cd $WORKSPACE/sli/admin-tools/admin-rails
  bundleInstall --path $WORKSPACE/../vendors/
  bundle exec thin start -C config/thin.yml -e team_sb
  echo "**** end deployAdminSB with args $* ****"
}

deployDatabrowser()
{
  echo "**** begin deployDatabrowser with args $* ****"
  cd $WORKSPACE/sli/databrowser
  bundleInstall --deployment
  bundle exec cap team deploy -s subdomain=$NODE_NAME -S branch=$GITCOMMIT
  echo "**** end deployDatabrowser with args $* ****"
}

buildApi()
{
  echo "**** begin buildApi with args $* ****"
  cd $WORKSPACE/sli
  /jenkins/tools/Maven/bin/mvn -pl api -am -ff -P team -Dmaven.test.failure.ignore=false -Dsli.env=team -Dsli.dev.subdomain=$NODE_NAME clean install -DskipTests=true
  echo "**** end buildApi with args $* ****"
}

buildApiDocumentationArtifacts()
{
  echo "**** begin buildApiDocumentationArtifacts with args $* ****"
  cd $WORKSPACE/sli/config/scripts/documentation
  ./generate_doc_artifacts.sh
  echo "**** end buildApiDocumentationArtifacts with args $* ****"
}







