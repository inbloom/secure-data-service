@Echo off

if "%SLI_ROOT%"=="" goto :root_not_set

echo "Generating properties file..."
ruby.exe %SLI_ROOT%\config\scripts\webapp-provision.rb %SLI_ROOT%\config\config.in\canonical_config.yml local-acceptance-tests %SLI_ROOT%\config\properties\sli.properties
echo "Done."

pushd %SLI_ROOT\acceptance-tests

SET MAVEN_OPTS="-XX:PermSize=256m -XX:MaxPermSize=1024m -Dsli.conf=%SLI_ROOT%\config\properties\sli.properties -Dsli.env=local -Dsli.encryption.keyStore=%SLI_ROOT%\data-access\dal\keyStore\localKeyStore.jks"

echo 'Running acceptance tests...'
mkdir -p target\logs

if "%1"=="" goto :default
SET TESTS_TO_RUN=%1
goto :run

:default
SET TESTS_TO_RUN="integrationTests"
goto :run

:run
echo "Running test: %TESTS_TO_RUN%"
mvn integration-test
goto :eof

:root_not_set
ECHO Need to set SLI_ROOT to the fully qualified path of your local SLI%SLI_ROOT% source tree.
