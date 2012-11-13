#!/bin/bash
#
# This script gets logs from SLIRP
#
#set -x

if [ $# -ne 1 ];
then
  echo "Usage: scripts/slirp_getlogs RUN_NAME (run from the config/ directory)"
  echo "This script uses scripts in the indexes/ and shards/ folders"
  echo "NOTICE: ALL Sharding jobs will execute with the following configuration, it can be modified in the $0 script."
  echo "     \"var num_years=1, tenant='Hyrule'\" "
  exit 1
fi

NAME=$1

######################
#   Primary Config   #
######################

PRIMARIES="slirpmongo03.slidev.org slirpmongo05.slidev.org slirpmongo09.slidev.org slirpmongo11.slidev.org"
ISDB="slirpmongo99.slidev.org"

#
# Create dir
#
echo "Create directory..."
mkdir $NAME

#
# Get start/stop time TO SCREEN
#
echo "Job Start/Stop..."
mongo $ISDB/ingestion_batch_job --eval 'db.newBatchJob.find({}, {"jobStartTimestamp":1,"jobStopTimestamp":1}).forEach(function(x){printjson(x);})'

#
# Get start/stop time
#
mongo $ISDB/ingestion_batch_job --eval 'db.newBatchJob.find({}, {"jobStartTimestamp":1,"jobStopTimestamp":1}).forEach(function(x){printjson(x);})' > $NAME/time

#
# Get stats
#
echo "Stats..."
mongo $ISDB/is --eval "db.batchJobStage.find.forEach(function(x){printjson(x);})" > $NAME/batchJobStage

#
# Get slow query logs
#
echo "Slow query logs, is..."
mongo $ISDB/is --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_is.log
echo "Slow query logs, staging..."
mongo $ISDB/ingestion_batch_job --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_ingestion_batch_job.log

for i in $PRIMARIES;
do
  echo "Slow query logs, ${i}/sli..."
  mongo $i/sli --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_${i}_sli.log
  echo "Slow query logs, $i/Hyrule..."
  mongo $i/d36f43474916ad310100c9711f21b65bd8231cc6 --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_${i}_hyrule.log
  echo "Slow query logs, $i/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a..."
  mongo $i/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_${i}_tenant2.log
  echo "Slow query logs, $i/ff501cb38db19529bc3eb7fd5759f3844626fdf6..."
  mongo $i/ff501cb38db19529bc3eb7fd5759f3844626fdf6 --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_${i}_tenant3.log
done

#
# Copy ingestion logs
#
echo "Copy logs..."
cp /opt/logs/ingestion.log /opt/lz/inbound/Hyrule-NYC/job* /opt/lz/inbound/Hyrule-NYC/error* $NAME

#
# Crerate zip file
#
echo "Create zip..."
zip -r $NAME.zip $NAME

#
# Display contents of zip
#
echo "ZIP file..."
ls -lht $NAME.zip
