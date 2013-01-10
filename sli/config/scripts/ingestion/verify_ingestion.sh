#!/bin/bash
#
# This script verifies ingestion counts
#
#set -x

if [ $# -ne 2 ];
then
  echo "Usage: verify_ingestion FILE DATABASE"
  exit 1
fi

FILE=$1
DATABASE=$2

echo "Verifying ingestion with $FILE"

#
# Get the script
#
rm -f /tmp/jsExpected.js
unzip -o -q -d /tmp $FILE jsExpected.js

#
# Run it
#
if [ -f /tmp/jsExpected.js ] ; then
  MISMATCHES=`mongo $DATABASE /tmp/jsExpected.js | grep Mismatch`
  if [ -z "$MISMATCHES" ] ; then
    echo "All entity numbers are correct."
  else
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "ENTITY MISMATCHES !!!!!!!!!!!!!!"
    echo "$MISMATCHES"
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  fi
else
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "NO jsExpected.js in $FILE !!!!!!!!!!!!!!!!!!!!"
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
fi
