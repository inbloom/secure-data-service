#!/bin/bash
#
# This script starts an ingestion in SLIRP
#
#set -x

if [ $# -eq 0 -o $# -gt 3 ] ;
then
  echo "Usage: scripts/slirp_startIngest FILE [FILE2] [FILE3] (run from the config/ directory)"
  exit 1
fi

echo "******************************************************************************"
echo "**  Hyrule-NYC - Starting SLIRP ingestion of $FILE_PATH at `date`"
echo "******************************************************************************"

FILE_PATH=$1
FILE=${FILE_PATH##*/}
cp $FILE_PATH /opt/lz/inbound/Hyrule-NYC
/opt/sli/bin/publish_file_uploaded.rb STOR /opt/lz/inbound/Hyrule-NYC/$FILE localhost

if [ -n "$2" ] ; then
  echo "******************************************************************************"
  echo "**  Midgar-DAYBREAK - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$2
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH /opt/lz/inbound/Midgar-DAYBREAK
  /opt/sli/bin/publish_file_uploaded.rb STOR /opt/lz/inbound/Midgar-DAYBREAK/$FILE localhost
fi

if [ -n "$3" ] ; then
  echo "******************************************************************************"
  echo "**  Midgar-SUNSET - Starting SLIRP ingestion of $FILE_PATH at `date`"
  echo "******************************************************************************"
  FILE_PATH=$3
  FILE=${FILE_PATH##*/}
  cp $FILE_PATH /opt/lz/inbound/Midgar-SUNSET
  /opt/sli/bin/publish_file_uploaded.rb STOR /opt/lz/inbound/Midgar-SUNSET/$FILE localhost
fi
