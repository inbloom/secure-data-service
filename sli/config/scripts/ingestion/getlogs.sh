#!/bin/bash
#
# This script gets logs after an ingestion test
#
#set -x

if [ $# -lt 1 ] ; then
  echo "Usage: getlogs RUN_NAME [FILE DATABASE]"
  exit 1
fi

if [ -n "`hostname | grep slirp`" ] ; then
  # SLIRP
  PRIMARIES="slirpmongo03.slidev.org slirpmongo05.slidev.org slirpmongo09.slidev.org slirpmongo11.slidev.org"
  STAGING="slirpmongo99.slidev.org"
else
  # Local
  PRIMARIES="localhost"
  STAGING="localhost"
fi

if [ -z "$SCRIPTS" ] ; then
  SCRIPTS=scripts/ingestion
fi
if [ -z "$ING_LOG_DIR" ] ; then
  ING_LOG_DIR=/opt/logs
fi
if [ -z "$LZ" ] ; then
  LZ=/opt/lz
fi

NAME=$1

echo "******************************************************************************"
echo "**  Getting logs to $NAME at `date`"
echo "******************************************************************************"

#
# Create dir
#
echo "Create directory..."
mkdir $NAME

#
# Get start/stop time
#
echo "Job Start/Stop..."
mongo --quiet $STAGING/ingestion_batch_job --eval 'db.newBatchJob.find({}, {"jobStartTimestamp":1,"jobStopTimestamp":1}).forEach(function(x){printjson(x);})' > $NAME/time
mongo --quiet $STAGING/ingestion_batch_job --eval 'db.error.find({}, {"jobStartTimestamp":1,"jobStopTimestamp":1}).forEach(function(x){printjson(x);})' > $NAME/error

#
# Get start/stop time TO SCREEN
#
mongo --quiet $STAGING/ingestion_batch_job --eval '"Errors: " + db.error.count()'
cat $NAME/time

#
# Copy GC log
#
if [ -n "`hostname | grep slirp`" ] ; then
  cp /opt/logs/gc.out $NAME
fi

#
# Verify
#
ALL_DBS=""
while [ $# -gt 2 ] ; do
  FILE=$2
  DATABASE=$3
  ALL_DBS="$ALL_DBS $DATABASE"
  shift
  shift
  $SCRIPTS/verify_ingestion.sh $FILE $DATABASE > $NAME/verification
  cat $NAME/verification
done

#
# Get stats
#
echo "Stats..."
mongo --quiet $STAGING/ingestion_batch_job --eval "db.batchJobStage.find().forEach(function(x){printjson(x);})" > $NAME/batchJobStage

#
# Get slow query logs
#
echo "Slow query logs, staging..."
mongo --quiet $STAGING/ingestion_batch_job --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_ingestion_batch_job.log
echo "ingestion_batch_job: `$SCRIPTS/analyze_slow_query_log.sh $NAME/slow_ingestion_batch_job.log`" >> $NAME/slow_summary

for PRIMARY in $PRIMARIES ; do
  echo "Slow query logs, $PRIMARY/sli..."
  mongo --quiet $PRIMARY/sli --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_${PRIMARY}_sli.log
  echo "$PRIMARY/sli: `$SCRIPTS/analyze_slow_query_log.sh $NAME/slow_${PRIMARY}_sli.log`" >> $NAME/slow_summary
  for DB in $ALL_DBS ; do
    echo "Slow query logs, $PRIMARY/$DB..."
    mongo --quiet $PRIMARY/$DB --eval "db.system.profile.find().forEach(function(x){printjson(x);})" > $NAME/slow_${PRIMARY}_${DB}.log
    echo "$PRIMARY/${DB}: `$SCRIPTS/analyze_slow_query_log.sh $NAME/slow_${PRIMARY}_${DB}.log`" >> $NAME/slow_summary
  done
done

#
# Copy ingestion logs
#
echo "Copy logs..."
cp -f $ING_LOG_DIR/ingestion.log $LZ/inbound/*/job* $NAME
if [ -z "`grep 'Zip file detected' $NAME/ingestion.log`" ] ; then
  # get previous day's log 
  PREV=`ls -t $ING_LOG_DIR/ingestion-* | head -n 1`
  cat $PREV $NAME/ingestion.log > $NAME/tmp
  mv $NAME/tmp $NAME/ingestion.log
fi
$SCRIPTS/parse_ingestion_log.sh $NAME/ingestion.log > $NAME/ingestion_summary

#
# Crerate zip file
#
echo "Create zip..."
zip -qr $NAME.zip $NAME

#
# Display contents of zip
#
echo "ZIP file..."
ls -lht $NAME.zip
