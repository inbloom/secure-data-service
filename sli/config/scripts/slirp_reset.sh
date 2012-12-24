#!/bin/bash
#
# This script resets SLIRP Mongo and ActiveMQ
# For use between Day 1 ingestion tests
#
#set -x

if [ $# -gt 1 ] ; then
  echo "Usage: scripts/slirp_reset [SLOW_QUERY_TIME] (run from the config/ directory)"
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

######################
#   Primary Config   #
######################

PRIMARIES="slirpmongo03.slidev.org slirpmongo05.slidev.org slirpmongo09.slidev.org slirpmongo11.slidev.org"
ISDB="slirpmongo99.slidev.org"

### The script!
echo "******************************************************************************"
echo "**  Resetting SLIRP at `date`"
echo "******************************************************************************"

echo " ***** Stopping Tomcat!"
service tomcat stop
rm -fr /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest
rm -f /opt/logs/gc.out

echo " ***** Clearing LZ!"

rm -r -f /opt/lz/inbound/*

echo " ***** Identifying Collections and dropping each one"

for i in $PRIMARIES;
do
  mongo $i <<END
use sli
db.setProfilingLevel(0);
END
done

# Identify collections
COLLECTIONS=`mongo sli<<END
show collections
END`
COLLECTIONS=`echo $COLLECTIONS| sed s/bye// |sed s/MongoDB\ shell\ version:\ .*\ connecting\ to:\ sli//|sed s/system.indexes//`
for i in $COLLECTIONS;
do
  mongo sli <<END
db.$i.drop()
END
done

echo " ***** Attempting to drop databases (SLI and Hyrule.NYC)"
mongo <<END
use sli
db.dropDatabase();
use d36f43474916ad310100c9711f21b65bd8231cc6
db.dropDatabase();
use 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a
db.dropDatabase();
use ff501cb38db19529bc3eb7fd5759f3844626fdf6
db.dropDatabase();
END

echo " ***** Ensuring Primary servers no longer have the SLI or Kyrule.NYC Database."

for i in $PRIMARIES;
do
  mongo $i <<END
use sli
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel($SLOW_QUERY_PARAMS);
use d36f43474916ad310100c9711f21b65bd8231cc6
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel($SLOW_QUERY_PARAMS);
use 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel($SLOW_QUERY_PARAMS);
use ff501cb38db19529bc3eb7fd5759f3844626fdf6
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel($SLOW_QUERY_PARAMS);
END
done

echo " ***** Adding Indexes to sli db"
unzip -p /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest.war WEB-INF/classes/sli_indexes.js > /tmp/sli_indexes.js
mongo sli < /tmp/sli_indexes.js

echo " ***** Clearing databases off $ISDB"
mongo $ISDB/ingestion_batch_job << END
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel($SLOW_QUERY_PARAMS);
END
echo " ***** Setting up indexes on $ISDB"
unzip -p /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest.war WEB-INF/classes/ingestion_batch_job_indexes.js > /tmp/ingestion_batch_job_indexes.js
mongo $ISDB/ingestion_batch_job < /tmp/ingestion_batch_job_indexes.js

echo " ***** Restarting Mongos"
killall mongos
service mongos start

echo " ***** Attempting to clear ActiveMQ"
CURLCMD="curl -c /tmp/cookiejar -b /tmp/cookiejar"
ACTIVEMQ_SECRET=`$CURLCMD -s http://slirpingest01.slidev.org:8161/admin/queues.jsp |grep secret|cut -d = -f 5|cut -d \" -f 1|tail -n 1`
echo " ***** Using ActiveMQ Secret $ACTIVEMQ_SECRET"
$CURLCMD "http://slirpingest01.slidev.org:8161/admin/deleteDestination.action?JMSDestination=ingestion.maestro&JMSDestinationType=queue&secret="$ACTIVEMQ_SECRET >>/dev/null
$CURLCMD "http://slirpingest01.slidev.org:8161/admin/deleteDestination.action?JMSDestination=ingestion.pit&JMSDestinationType=queue&secret="$ACTIVEMQ_SECRET >>/dev/null
$CURLCMD "http://slirpingest01.slidev.org:8161/admin/deleteDestination.action?JMSDestination=ingestion.workItem&JMSDestinationType=queue&secret="$ACTIVEMQ_SECRET  >>/dev/null

echo " ***** Removing ingestion log"
rm -f /opt/logs/ingestion.log

echo " ***** Restarting Tomcat"
service tomcat start
