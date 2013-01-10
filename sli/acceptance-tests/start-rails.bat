@ECHO OFF
setlocal
SET BUNDLE_GEMFILE=Gemfile.win32

if "%1"=="" goto MISSING_TITLE

if "%2"=="" goto MISSING_PATH
SET WORKING_DIR=%2
goto SET_ENVIRONMENT

:MISSING_PATH
echo The second argument to start-rails.bat must be a path to a directory
echo containing the bundle Gemfile.
goto :eof

:SET_ENVIRONMENT
if "%3"=="" goto DEFAULT_ENVIRONMENT
SET ENVIRONMENT=%3
goto SET_PORT

:DEFAULT_ENVIRONMENT
SET ENVIRONMENT=Development

:SET_PORT
if "%4"=="" goto DEFAULT_PORT
SET PORT="%4"
goto START

:DEFAULT_PORT
SET PORT="2001"

:START
pushd %WORKING_DIR%
cmd.exe /C bundle install --gemfile=gemfile.win32
start "%1" /MIN /D%WORKING_DIR% bundle exec rails server -e %ENVIRONMENT% -p %PORT%
popd
goto :eof

:MISSING_TITLE
echo You must provide a window title as the first argument to this script
echo Usage start-rails.bat "Window Title" [working dir] [environment] [port]
goto :eof
