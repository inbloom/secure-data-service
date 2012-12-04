#!/bin/bash
#
# This script waits for an ingestion in SLIRP
#
#set -x

COUNT=1
if [ $# -eq 1 ] ; then
  COUNT=$1
elif [ $# -ne 0 ] ; then
  echo "Usage: scripts/slirp_wait_for_ingest.sh (run from the config/ directory)"
  exit 1
fi

echo "******************************************************************************"
echo "**  Waiting for SLIRP ingestion to finish at `date`"
echo "******************************************************************************"


echo -n "Waiting"
while [ "`grep -m $COUNT "Clearing cache at job completion" /opt/logs/ingestion.log | wc -l`" -ne $COUNT ]; do
  echo -n .
  sleep 60
done
echo

echo "slirp_wait_for_ingest.sh finished at `date`"
