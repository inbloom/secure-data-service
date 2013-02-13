#!/bin/bash
#
# This script gets logs after an ingestion test
#
#set -x

if [ $# -lt 2 ] ; then
  echo "Usage: getlogs NUMBER_OF_SERVERS RUN_NAME [FILE DATABASE]"
  exit 1
fi

NUMBER_OF_SERVERS=$1
NAME=$2

if [ -n "`hostname | grep slirp`" ] ; then
  # SLIRP
  PRIMARIES="slirpmongo01.slidev.org slirpmongo02.slidev.org slirpmongo03.slidev.org slirpmongo04.slidev.org"
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
  LZ=/ingestion/lz
fi

###################

# GetSeconds token file num
function GetSeconds {
  DATE=`grep $1 $2 | head -n $3 | tail -n 1 | sed -e 's/^.*ISODate("\(.*\)\..*$/\1/' -e 's/T/ /'`
  if [ -f /mach_kernel ] ; then
    ABS=`date -j -f "%d %b %Y %H:%M:%S" "$DATE" +%s`
  else
    ABS=`date -d "$DATE" +%s`
  fi
  echo $ABS
}

###################

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
NUM_JOBS=`grep jobStartTimestamp $NAME/time | wc -l`
for (( NUM=1; $NUM<=$NUM_JOBS; NUM++ )) ; do
  START=`GetSeconds jobStartTimestamp $NAME/time $NUM`
  STOP=`GetSeconds jobStopTimestamp $NAME/time $NUM`
  let "SECONDS = $STOP - $START"
  let "MINUTES = $SECONDS / 60"
  let "REM_SECONDS = $SECONDS - $MINUTES * 60"
  let "HOURS = $MINUTES / 60"
  let "REM_MINUTES = $MINUTES - $HOURS * 60"
  echo "${HOURS}h, ${REM_MINUTES}m, ${REM_SECONDS}s ($SECONDS seconds)" >> $NAME/time
done
cat $NAME/time

#
# Get errors
#
mongo --quiet $STAGING/ingestion_batch_job --eval 'db.error.find({}, {"jobStartTimestamp":1,"jobStopTimestamp":1}).forEach(function(x){printjson(x);})' > $NAME/error
mongo --quiet $STAGING/ingestion_batch_job --eval '"Errors: " + db.error.count()'

#
# Copy GC log
#
if [ -n "`hostname | grep slirp`" ] ; then
  if [ "$1" == "1" ] ; then
    echo " ***** Copying GC log"
    cp /opt/logs/gc.log $NAME
    $SCRIPTS/parse_gc_log.sh $NAME/gc.log > $NAME/gc_summary
  else
    for (( NUM=1; NUM<=$NUMBER_OF_SERVERS; NUM++ )) ; do
      echo " ***** Copying GC log from $NUM"
      scp slirpingest0$NUM:/opt/logs/gc.log $NAME/gc$NUM.log
      $SCRIPTS/parse_gc_log.sh $NAME/gc$NUM.log > $NAME/gc${NUM}_summary
    done
  fi
fi

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
if [ "$NUMBER_OF_SERVERS" == "1" ] ; then
  cp -f $ING_LOG_DIR/ingestion.log $LZ/inbound/*/job* $NAME
  if [ -z "`grep 'Landing Zone message detected' $NAME/ingestion.log`" ] ; then
    # get previous day's log 
    PREV=`ls -t $ING_LOG_DIR/ingestion-* | head -n 1`
    cat $PREV $NAME/ingestion.log > $NAME/tmp
    mv $NAME/tmp $NAME/ingestion.log
  fi
else
  for (( NUM=1; NUM<=$NUMBER_OF_SERVERS; NUM++ )) ; do
    scp slirpingest0$NUM:/$ING_LOG_DIR/ingestion.log $NAME/ingestion$NUM.log
  done
  cat $NAME/ingestion*.log | sort -n > $NAME/ingestion.log
fi
$SCRIPTS/parse_ingestion_log.sh $NAME/ingestion.log $NUMBER_OF_SERVERS > $NAME/ingestion_summary

#
# Verify
#
ALL_DBS=""
while [ $# -gt 3 ] ; do
  FILE=$3
  DATABASE=$4
  ALL_DBS="$ALL_DBS $DATABASE"
  shift
  shift
  $SCRIPTS/verify_ingestion.sh $FILE $DATABASE > $NAME/verification
  cat $NAME/verification
done

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
