#!/bin/sh
if [ $# -lt 1 ] ; then
  echo "Usage: checkIngestedCounts.sh NAME_OF_DATASET_CONTAINING_MANIFEST.JSON"
  exit 1
fi
rm -f manifest.json
unzip -p /opt/datasets/$1 manifest.json | tee manifest.json
echo
/home/jsingh/.rvm/rubies/ruby-1.9.3-p362/bin/ruby sli-verify.rb Midgar manifest.json
rm -f manifest.json