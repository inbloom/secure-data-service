#!/bin/bash
#
# This script resets SLIRP Mongo and ActiveMQ
# For use between Day 1 ingestion tests
#

if [ $# -gt 0 ];
then
  echo "Usage: scripts/slirp_reset (run from the config/ directory)"
  echo "This script uses scripts in the indexes/ folder"
  exit 1
fi

#
# Threshold for logging slowq queries, ms
SLOW_QUERY=100

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
db.setProfilingLevel(1,$SLOW_QUERY);
use d36f43474916ad310100c9711f21b65bd8231cc6
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel(1,$SLOW_QUERY);
use 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel(1,$SLOW_QUERY);
use ff501cb38db19529bc3eb7fd5759f3844626fdf6
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel(1,$SLOW_QUERY);
END
done

echo " ***** Adding Indexes to sli db"
mongo sli < indexes/sli_indexes.js

echo " ***** Clearing databases off $ISDB"
mongo $ISDB/is <<END
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel(1,$SLOW_QUERY);
END
mongo $ISDB/ingestion_batch_job <<END
db.setProfilingLevel(0);
db.dropDatabase();
db.setProfilingLevel(1,$SLOW_QUERY);
END
echo " ***** Setting up indexes on $ISDB"
mongo $ISDB/is < indexes/is_indexes.js
mongo $ISDB/ingestion_batch_job < indexes/ingestion_batch_job_indexes.js

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
