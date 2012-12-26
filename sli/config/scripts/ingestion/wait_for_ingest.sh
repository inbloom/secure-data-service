#!/bin/bash
#
# This script waits for an ingestion
#
#set -x

COUNT=1
if [ $# -eq 1 ] ; then
  COUNT=$1
elif [ $# -ne 0 ] ; then
  echo "Usage: wait_for_ingest.sh"
  exit 1
fi

if [ -z "$ING_LOG_DIR" ] ; then
  ING_LOG_DIR=/opt/logs
fi

echo "******************************************************************************"
echo "**  Waiting for ingestion to finish at `date`"
echo "******************************************************************************"


echo -n "Waiting"
while [ "`grep -m $COUNT "Clearing cache at job completion" $ING_LOG_DIR/ingestion.log | wc -l`" -ne $COUNT ]; do
  echo -n .
  sleep 60
done
echo

echo "wait_for_ingest.sh finished at `date`"
