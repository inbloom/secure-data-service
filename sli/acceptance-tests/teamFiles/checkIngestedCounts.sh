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
echo
/home/jsingh/.rvm/rubies/ruby-1.9.3-p362/bin/ruby ../../../tools/data-tools/mongo_data_profile.rb 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a
/home/jsingh/.rvm/rubies/ruby-1.9.3-p362/bin/ruby ../../../tools/data-tools/mongo_data_profile.rb sli
/home/jsingh/.rvm/rubies/ruby-1.9.3-p362/bin/ruby ../../../tools/data-tools/mongo_data_profile.rb ingestion_batch_job
