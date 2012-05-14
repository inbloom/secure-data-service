@Echo off

if "%SLI_ROOT%"=="" goto :root_not_set

echo "Generating properties file..."
ruby.exe %SLI_ROOT%\config\scripts\webapp-provision.rb %SLI_ROOT%\config\config.in\canonical_config.yml local-acceptance-tests %SLI_ROOT%\config\properties\sli.properties
echo "Done."

pushd %SLI_ROOT%\acceptance-tests

SET ADMIN_RAILS_DIR="%SLI_ROOT%\admin-tools\admin-rails"
SET DATABROWSER_DIR="%SLI_ROOT%\databrowser"

SET MAVEN_OPTS=-XX:PermSize=256m -XX:MaxPermSize=1024m -DADMIN_RAILS_DIR=%ADMIN_RAILS_DIR% -DDATABROWSER_DIR=%DATABROWSER_DIR% -Dsli.conf=%SLI_ROOT%\config\properties\sli.properties -Dsli.env=local -Dsli.encryption.keyStore=%SLI_ROOT%\data-access\dal\keyStore\localKeyStore.jks

echo 'Running acceptance tests...'
mkdir target\logs

if "%1"=="" goto :default
SET TESTS_TO_RUN=%1
goto :run

:default
SET TESTS_TO_RUN="integrationTests"
goto :run

:run
echo Running test: %TESTS_TO_RUN%

echo Note: the first time you run this script, maven downloads a Tomcat 7 package. 
echo This may take several minutes depending on the speed of your connection. 
echo Deleting the 'acceptance-tests\target' directory will cause maven to download 
echo this file again.

call start-rails.bat admin-rails %SLI_ROOT%\admin-tools\admin-rails local-integration-tests 2000
call start-rails.bat databrowser %SLI_ROOT%\databrowser local-integration-tests 2001

call mvn integration-test

call stop-rails.bat admin-rails
call stop-rails.bat databrowser
goto :eof

:root_not_set
ECHO Need to set SLI_ROOT to the fully qualified path of your local SLI source tree.
