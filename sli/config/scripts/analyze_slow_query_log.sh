#!/bin/bash
#
# This script analyzes a slow query log
#
#set -x

if [ $# -ne 1 ];
then
  echo "Usage: scripts/analyze_slow_query_log.sh FILE (run from the config/ directory)"
  exit 1
fi

FILE=$1

#
# Get and sort 
#
grep millis $FILE | sed -e 's/^.* \([0-9]*\),.*$/\1/' | sort -n -r > /tmp/a
COUNT=`wc -l /tmp/a | sed -e 's/^\([0-9]*\) .*$/\1/'`
if [ $COUNT = 0 ] ; then
  echo "No slow queries"
else
  TOP5=`head -n 5 /tmp/a | tr '\n' ','`

  #
  # Add times
  #
  echo "0" > /tmp/b
  grep millis $FILE | sed -e 's/^.* \([0-9]*\),.*$/\1 +/' | sort -n -r >> /tmp/b
  echo "p" >> /tmp/b
  TOTAL=`dc < /tmp/b`
  let "SECONDS = $TOTAL / 1000"
  let "MINUTES = $SECONDS / 60"
  let "AVERAGE = $TOTAL / $COUNT"
  
  echo "$COUNT slow queries, total: $TOTAL ms = $SECONDS sec = $MINUTES min, $AVERAGE ms average, top 5: $TOP5"
fi
