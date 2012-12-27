#!/bin/bash
#
# This script starts an ingestion
#
#set -x

if [ $# -eq 0 -o $# -gt 3 ] ;
then
  echo "Usage: start_ingest FILE [FILE2] [FILE3]"
  exit 1
fi

if [ -z "$LZ" ] ; then
  LZ=/opt/lz
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
  echo "**  Midgar-SUNSET - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$3
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH $LZ/inbound/Midgar-SUNSET
  $PUBSCRIPT STOR $LZ/inbound/Midgar-SUNSET/$FILE localhost
fi
