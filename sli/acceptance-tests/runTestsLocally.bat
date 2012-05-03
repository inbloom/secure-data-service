@Echo off
setlocal

if "%SLI_ROOT%"=="" goto :root_not_set

pushd %SLI_ROOT\acceptance-tests

SET MAVEN_OPTS=-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.conf=%SLI_ROOT%\config\properties\sli.properties -Dsli.env=local -Dsli.encryption.keyStore=%SLI_ROOT%\data-access\dal\keyStore\localKeyStore.jks -Dsli.encryption.properties=%SLI_ROOT%\data-access\dal\keyStore\localEncryption.properties
SET TOMCAT_OPTS=-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.conf=%SLI_ROOT%\config\properties\sli.properties -Dsli.env=local -Dsli.encryption.keyStore=%SLI_ROOT%\data-access\dal\keyStore\localKeyStore.jks -Dsli.encryption.properties=%SLI_ROOT%\data-access\dal\keyStore\localEncryption.properties

echo 'Running acceptance tests...'
MD target\logs
mvn integration-test 
goto :eof

:root_not_set
ECHO Need to set SLI_ROOT to the fully qualified path of your local SLI git source tree.

