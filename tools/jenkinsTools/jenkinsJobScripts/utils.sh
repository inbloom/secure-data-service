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