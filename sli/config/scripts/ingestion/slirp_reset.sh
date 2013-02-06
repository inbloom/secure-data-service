#!/bin/bash
#
# This script resets SLIRP Mongo and ActiveMQ
# For use between Day 1 ingestion tests
#
#set -x

if [ $# -ne 2 ] ; then
  echo "Usage: scripts/ingestion/slirp_reset NUMBER_OF_SERVERS NUMBER_OF_TENANTS"
  exit 1
fi

NUMBER_OF_SERVERS=$1
NUMBER_OF_TENANTS=$2

if [ -z "$ING_LOG_DIR" ] ; then
  ING_LOG_DIR=/opt/logs
fi
if [ -z "$LZ" ] ; then
  LZ=/ingestion/lz
fi
if [ -z "$PUBSCRIPT" ] ; then
  PUBSCRIPT=/opt/sli/bin/publish_file_uploaded.rb
fi

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

######################
#   Primary Config   #
######################

PRIMARIES="slirpmongo03.slidev.org slirpmongo05.slidev.org slirpmongo09.slidev.org slirpmongo11.slidev.org"
ISDB="slirpmongo99.slidev.org"
TOTAL_INGESTION_SERVERS=3

### The script!
echo "******************************************************************************"
echo "**  Resetting SLIRP at `date`" for $NUMBER_OF_SERVERS ingestion servers
echo "******************************************************************************"

for (( NUM=1; NUM<=$TOTAL_INGESTION_SERVERS; NUM++ )) ; do
  echo " ***** Stopping Tomcat on server #$NUM"
  ssh slirpingest0$NUM service tomcat stop
  ssh slirpingest0$NUM rm -fr /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest
  ssh slirpingest0$NUM rm -f /opt/logs/gc.out /opt/logs/ingestion.log
done
rm -r -f /ingestion/lz/inbound/*

echo " ***** Dropping databases"
ALL_DBS=`mongo --quiet --eval 'db.getMongo().getDBNames()' | sed -e 's/,/ /g'`
for DB in $ALL_DBS ; do
  if [ "$DB" != "test" -a "$DB" != "config" -a "$DB" != "local" ] ; then
    echo "Dropping database $DB"
    mongo $DB --quiet --eval 'db.dropDatabase()'
  fi
done

echo " ***** Dropping ingestion_batch_job"
ALL_DBS=`mongo $ISDB --quiet --eval 'db.getMongo().getDBNames()' | sed -e 's/,/ /g'`
for DB in $ALL_DBS ; do
  if [ "$DB" != "test" -a "$DB" != "config" -a "$DB" != "local" ] ; then
    echo "Dropping database $DB on $ISDB"
    mongo $ISDB/$DB --quiet --eval 'db.dropDatabase()'
  fi
done

for (( NUM=1; NUM<=$TOTAL_INGESTION_SERVERS; NUM++ )) ; do
  echo " ***** Restarting Mongos on server #$NUM"
  ssh slirpingest0$NUM service mongos restart
done

#echo " ***** Setting slow query logging"
#for PRIMARY in $PRIMARIES ; do
#  mongo $PRIMARY <<END
#use sli
#db.setProfilingLevel($SLOW_QUERY_PARAMS);
#use d36f43474916ad310100c9711f21b65bd8231cc6
#db.setProfilingLevel($SLOW_QUERY_PARAMS);
#END
#done

echo " ***** Adding Indexes to sli db"
unzip -p /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest.war WEB-INF/classes/sli_indexes.js > /tmp/sli_indexes.js
mongo sli < /tmp/sli_indexes.js

echo " ***** Setting up indexes on $ISDB"
unzip -p /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest.war WEB-INF/classes/ingestion_batch_job_indexes.js > /tmp/ingestion_batch_job_indexes.js
#mongo $ISDB/ingestion_batch_job --quiet --eval "db.setProfilingLevel($SLOW_QUERY_PARAMS);"
mongo $ISDB/ingestion_batch_job < /tmp/ingestion_batch_job_indexes.js

#
# Clear ActiveMQ
#
echo " ***** Attempting to clear ActiveMQ"
CURLCMD="curl -c /tmp/cookiejar -b /tmp/cookiejar"
ACTIVEMQ_SECRET=`$CURLCMD -s http://slirpingest01.slidev.org:8161/admin/queues.jsp |grep secret|cut -d = -f 5|cut -d \" -f 1|tail -n 1`
echo " ***** Using ActiveMQ Secret $ACTIVEMQ_SECRET"
$CURLCMD "http://slirpingest01.slidev.org:8161/admin/deleteDestination.action?JMSDestination=ingestion.maestro&JMSDestinationType=queue&secret="$ACTIVEMQ_SECRET >>/dev/null
$CURLCMD "http://slirpingest01.slidev.org:8161/admin/deleteDestination.action?JMSDestination=ingestion.pit&JMSDestinationType=queue&secret="$ACTIVEMQ_SECRET >>/dev/null
$CURLCMD "http://slirpingest01.slidev.org:8161/admin/deleteDestination.action?JMSDestination=ingestion.workItem&JMSDestinationType=queue&secret="$ACTIVEMQ_SECRET  >>/dev/null

#
# Start one Tomcat server so we can onboard
#
echo " ***** Restarting Tomcat on server #1, localhost, then waiting for 1 minute"
service tomcat start
sleep 60

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
  echo " " > /opt/logs/ingestion.log
done

#
# Start remaining Tomcat severs
#
for (( NUM=2; NUM<=$NUMBER_OF_SERVERS; NUM++ )) ; do
  echo " ***** Restarting Tomcat on server #$NUM"
  ssh slirpingest0$NUM service tomcat start
done
if [ $NUMBER_OF_SERVERS -gt 1 ] ; then
  echo "***** Waiting for Tomcat to start on other ingestion servers"
  sleep 60
fi
