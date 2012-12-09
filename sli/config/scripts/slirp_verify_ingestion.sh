#!/bin/bash
#
# This script gets logs from SLIRP
#
#set -x

if [ $# -ne 2 ];
then
  echo "Usage: scripts/slirp_verify_ingestion FILE DATABASE (run from the config/ directory)"
  exit 1
fi

FILE=$1
DATABASE=$2

echo "Verifying SLIRP ingestion with $FILE"

#
# Get the script
#
unzip -o -q -d /tmp $FILE jsExpected.js

#
# Run it
#
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
