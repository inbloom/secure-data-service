curl http://tomcat:s3cret@localhost/manager/text/stop?path=/api
sleep 5
curl http://tomcat:s3cret@localhost/manager/text/start?path=/api