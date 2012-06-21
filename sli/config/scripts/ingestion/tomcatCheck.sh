

echo Checking state of tomcat 
TC=`ps aux | grep tomcat | grep -v grep | wc -l`
echo "Number of tomcats $TC"
if [ $TC -gt 1 ] 
then
  echo "Warning! Too many tomcats running"
  exit -1
fi;

echo "Good kitty..."

