#!/bin/bash
#
# This script starts an ingestion
#
#set -x

if [ $# -eq 0 -o $# -gt 5 ] ;
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

echo "******************************************************************************"
echo "**  Hyrule-NYC - Starting SLIRP ingestion of $FILE_PATH at `date`"
echo "******************************************************************************"

FILE_PATH=$1
FILE=${FILE_PATH##*/}
cp $FILE_PATH $LZ/inbound/Hyrule-NYC
$PUBSCRIPT STOR $LZ/inbound/Hyrule-NYC/$FILE localhost

if [ -n "$2" ] ; then
  echo "******************************************************************************"
  echo "**  Midgar-DAYBREAK - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$2
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH $LZ/inbound/Midgar-DAYBREAK
  $PUBSCRIPT STOR $LZ/inbound/Midgar-DAYBREAK/$FILE localhost
fi

if [ -n "$3" ] ; then
  echo "******************************************************************************"
  echo "**  Tenant_1-State - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$3
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH $LZ/inbound/Tenant_1-State
  $PUBSCRIPT STOR $LZ/inbound/Tenant_1-State/$FILE localhost
fi

if [ -n "$4" ] ; then
  echo "******************************************************************************"
  echo "**  Tenant_2-State - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$4
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH $LZ/inbound/Tenant_2-State
  $PUBSCRIPT STOR $LZ/inbound/Tenant_2-State/$FILE localhost
fi

if [ -n "$5" ] ; then
  echo "******************************************************************************"
  echo "**  Tenant_3-State - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$5
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH $LZ/inbound/Tenant_3-State
  $PUBSCRIPT STOR $LZ/inbound/Tenant_3-State/$FILE localhost
fi
