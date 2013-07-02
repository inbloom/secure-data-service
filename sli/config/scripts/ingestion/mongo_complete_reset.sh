#!/bin/bash
#
# This script completely resets the Mongo servers
#
set -x

if [ $# -ne 1 ] ; then
  echo "Usage: scripts/ingestion/mongo_complete_reset.sh NUMBER_OF_SHARDS"
  exit 1
fi

NUMBER_OF_SHARDS=$1

TOTAL_INGESTION_SERVERS=3
ISDB="slirpmongo99.slidev.org"

echo "******************************************************************************"
echo "**  Completely resetting Mongo at `date` for $NUMBER_OF_SHARDS shards"
echo "******************************************************************************"

for (( NUM=1; NUM<=$TOTAL_INGESTION_SERVERS; NUM++ )) ; do
  echo " ***** Stopping Tomcat on server #$NUM at `date`"
  ssh slirpingest0$NUM service tomcat stop
  ssh slirpingest0$NUM rm -fr /opt/tomcat/apache-tomcat-7.0.29/webapps/ingest
  ssh slirpingest0$NUM rm -f /opt/logs/gc.out /opt/logs/ingestion.log
done

echo " ***** Stopping Mongos on all ingestion servers at `date`"
for (( NUM=1; NUM<=$TOTAL_INGESTION_SERVERS; NUM++ )) ; do
  echo "** slirpingest0$NUM"
  ssh slirpingest0$NUM 'service mongos stop ; killall mongos'
done

echo "***** Erasing staging server data at `date`"
ssh $ISDB 'service mongod stop ; killall mongod ; rm -fr /var/lib/mongo/* /var/log/mongo/mongod.log ; service mongod start'

echo "***** Erasing mongo shard data at `date`"
for (( NUM=1; NUM<=$NUMBER_OF_SHARDS; NUM++ )) ; do
  echo "** slirpmongo0$NUM at `date`"
  ssh slirpmongo0$NUM 'service mongod stop ; killall mongod ; rm -fr /var/lib/mongo/* /var/log/mongo/mongod.log ; service mongod start'
done

echo "***** Erasing mongo config server data at `date`"
for (( NUM=1; NUM<=3; NUM++ )) ; do
  echo "** slirpmongoc0$NUM at `date`"
  ssh slirpmongoc0$NUM 'service mongod stop ; killall mongod ; rm -fr /var/lib/mongo/* /var/log/mongo/mongod.log ; service mongod start'
done

echo " ***** Starting Mongos on first ingestion server at `date`"
service mongos start

echo " ***** Creating shards at `date`"
for (( NUM=1; NUM<=$NUMBER_OF_SHARDS; NUM++ )) ; do
  echo "** Shard $NUM at `date`"
  mongo << END
  sh.addShard("slirpmongo0$NUM.slidev.org:27017")
END
done

echo " ***** Starting Mongos on all other ingestion servers at `date`"
for (( NUM=2; NUM<=$TOTAL_INGESTION_SERVERS; NUM++ )) ; do
  echo "** slirpingest0$NUM at `date`"
  ssh slirpingest0$NUM service mongos start
done
