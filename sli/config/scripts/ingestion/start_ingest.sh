#!/bin/bash
#
# This script starts an ingestion
#
#set -x

if [ $# -eq 0 -o $# -gt 7 ] ;
then
  echo "Usage: start_ingest FILE [FILE2] [FILE3] [FILE4] [FILE5]"
  exit 1
fi

if [ -z "$LZ" ] ; then
  LZ=/ingestion/lz
fi
if [ -z "$PUBSCRIPT" ] ; then
  PUBSCRIPT=/opt/sli/bin/publish_file_uploaded.rb
fi

TENANTS[1]="Hyrule-NYC"
TENANTS[2]="Midgar-DAYBREAK"
TENANTS[3]="Tenant_1-State"
TENANTS[4]="Tenant_2-State"
TENANTS[5]="Tenant_3-State"
TENANTS[6]="Tenant_4-State"
TENANTS[7]="Tenant_5-State"

NUM=1
while [ $# -gt 0 ] ; do
  FILE_PATH=$1
  if [ "$FILE_PATH" != "null" ] ; then
    TENANT=${TENANTS[$NUM]}
    echo "******************************************************************************"
    echo "**  $TENANT - Starting SLIRP ingestion of $FILE_PATH at `date`"
    echo "******************************************************************************"
    FILE=${FILE_PATH##*/}
    cp $FILE_PATH $LZ/inbound/$TENANT
    $PUBSCRIPT STOR $LZ/inbound/$TENANT/$FILE localhost
  fi
  let "NUM=$NUM+1"
  shift
done
