#!/bin/bash
#
# This script resets Local Mongo and ActiveMQ
# For use between Day 1 ingestion tests
#
#set -x

if [ $# -ne 1 ] ; then
  echo "Usage: scripts/ingestion/slirp_reset NUMBER_OF_TENANTS"
  exit 1
fi

NUMBER_OF_TENANTS=$1

#
# Threshold for logging slow queries, ms
#
SLOW_QUERY=100
# There is a bug with the slow query log see https://jira.mongodb.org/browse/CS-5365
SLOW_QUERY=0
if [ $SLOW_QUERY -gt 0 ] ; then
  SLOW_QUERY_PARAMS="1,$SLOW_QUERY"
else
  SLOW_QUERY_PARAMS="0"
fi

echo "******************************************************************************"
echo "**  Resetting at `date`"
echo "******************************************************************************"

echo " ***** Stop Jetty"
PID=`ps -e | grep jetty | grep -v grep | sed -e's/^ *\([0-9]*\) .*$/\1/'`
if [ -n "$PID" ] ; then
  echo "Jetty PID = $PID"
  kill -9 $PID
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
#activemq restart

echo " ***** Removing ingestion log"
mkdir -p $ING_LOG_DIR
rm -f $ING_LOG_DIR/*

echo " ***** Start Jetty"
export MAVEN_OPTS="-Xmx4096m -XX:MaxPermSize=256m -XX:+UseParallelGC"
pushd $ING
mvn jetty:run > $ING_LOG_DIR/jetty.log 2>&1 &
popd
echo "Waiting for Jetty to start..."
sleep 60
echo "Done."

#
# Onboard tenants
#
TENANTS[1]="Hyrule-NYC"
TENANTS[2]="Midgar-DAYBREAK"
TENANTS[3]="Tenant_1-State"
TENANTS[4]="Tenant_2-State"
TENANTS[5]="Tenant_3-State"
TENANTS[6]="Tenant_4-State"
TENANTS[7]="Tenant_5-State"
for (( NUM=1; NUM<=$NUMBER_OF_TENANTS; NUM++ )) ; do
  TENANT=${TENANTS[$NUM]}
  echo "***** Onboarding tenant #$NUM - $TENANT"
  echo "@purge" > /tmp/MainControlFile.ctl
  pushd /tmp
  zip $LZ/inbound/$TENANT/purge.zip MainControlFile.ctl
  popd
  $PUBSCRIPT STOR $LZ/inbound/$TENANT/purge.zip localhost
  while [ "`grep "Clearing cache at job completion" $ING_LOG_DIR/ingestion.log | wc -l`" -ne 1 ]; do
    echo -n .
    sleep 5
  done
  echo
  echo "***** Truncating ingestion log"
  echo " " > $ING_LOG_DIR/ingestion.log
done

