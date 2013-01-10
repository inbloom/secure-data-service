#!/bin/bash




echo Checking state of tomcat
TC=`ps aux | grep tomcat | grep -v grep | wc -l`
echo "Number of tomcats $TC"
if [ $TC -gt 1 ]
then
  echo "Warning! Too many tomcats running"
  exit -1
fi;

echo "Good kitty..."


echo clearing ingested data from nxmongo sli db using "remove" operations
mongo nxmongo3.slidev.org/sli mongo_scripts/clear_ingested_data.js
mongo nxmongo3.slidev.org/sli mongo_scripts/sli_indexes.js

lz_folder=/home/ingestion/lz/inbound/IL-STATE-DAYBREAK
echo -e clearing landingzone $lz_folder/* "\n"
rm -rf $lz_folder/*

perf_folder=/home/ingestion/perf/$1
echo -e copying $perf_folder/*.xml "\n"to $lz_folder "\n"
cp $perf_folder/*.xml $lz_folder

date=`date +'%Y.%m.%d-%H:%M'`
echo -e copying $perf_folder/*.ctl to $lz_folder "\n"
cp $perf_folder/*.ctl $lz_folder/$1_$date.ctl

