#!/bin/bash
#
# This script resets Local Mongo and ActiveMQ
# For use between Day 1 ingestion tests
#
#set -x

if [ $# -gt 1 ] ; then
  echo "Usage: local_reset.sh [SLOW_QUERY_TIME]"
  exit 1
fi

#
# Threshold for logging slowq queries, ms
#
if [ $# -eq 1 ] ; then
  SLOW_QUERY=$1
else
  SLOW_QUERY=100
  # There is a bug with the slow query log see https://jira.mongodb.org/browse/CS-5365
  SLOW_QUERY=0
fi
if [ $SLOW_QUERY -gt 0 ] ; then
  SLOW_QUERY_PARAMS="1,$SLOW_QUERY"
else
  SLOW_QUERY_PARAMS="0"
fi

echo "******************************************************************************"
echo "**  Resetting at `date`"
echo "******************************************************************************"

echo " ***** Stop Jetty"
PID=`ps -e | grep jetty | grep -v grep | sed -e's/^\([0-9]*\) .*$/\1/'`
if [ -n "$PID" ] ; then
  echo "Jetty PID = $PID"
fi
rm -f $ING_LOG_DIR/jetty.log

echo " ***** Clearing LZ!"
rm -fr $LZ/*/*

echo " ***** Drop datbases"
ALL_DBS=`mongo --quiet --eval 'db.getMongo().getDBNames()' | sed -e 's/,/ /g'`
for DB in $ALL_DBS ; do
  if [ "$DB" != "test" -a "$DB" != "config" ] ; then
    echo "Dropping database $DB"
    mongo $DB --quiet --eval 'db.dropDatabase()'
  fi
done

echo " ***** Adding Indexes to sli db"
mongo sli < $ING/src/main/resources/sli_indexes.js

echo " ***** Setting up indexes on $ISDB"
mongo ingestion_batch_job < $ING/src/main/resources/ingestion_batch_job_indexes.js

echo " ***** Attempting to clear ActiveMQ"
activemq restart

echo " ***** Removing ingestion log"
rm -f $ING_LOG_DIR/ingestion.log

echo " ***** Start Jetty"
export MAVEN_OPTS="-Xmx4096m -XX:MaxPermSize=256m -XX:+UseParallelGC"
pushd $ING
mvn jetty:run > $ING_LOG_DIR/jetty.log 2>&1 &
popd
echo "Waiting for Jetty to start..."
sleep 60
echo "Done."
