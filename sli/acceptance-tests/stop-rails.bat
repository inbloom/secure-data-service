@echo off
if "%1"=="" goto MISSING_TITLE

:STOP
echo Stopping process %PID%
taskkill /T /F /FI "WINDOWTITLE eq %1*"
goto :eof

:MISSING_TITLE
echo You must provide a window title as the first argument to this script
echo Usage stop-rails.bat "Window Title"
goto :eof
