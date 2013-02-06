#!/bin/bash
#
# This script parses a gc log
#
#set -x

if [ $# -ne 1 ];
then
  echo "Usage: parse_gc_log.sh FILE_NAME"
  exit 1
fi

FILE=$1

###################

function SecondsFromLastGrep {
  if [ -n "$2" ] ; then
    LINE=`grep ${TENANTS[$2]} $FILE | grep "$1" | tail -n 1`
  else
    LINE=`grep "$1" $FILE | tail -n 1`
  fi
  echo `SecondsFromLine "$LINE"`
}

###################

sed -e 's/^.* \([0-9\.]*\) secs.*$/\1/' $FILE | grep --text '^[0-9\.]*$' | sort -n -r > /tmp/gc.tmp
TOP5=`head -n 5 /tmp/gc.tmp`
echo "0" > /tmp/gc2.tmp
sed -e 's/^\(.*\)$/\1 +/' /tmp/gc.tmp >> /tmp/gc2.tmp
echo "p" >> /tmp/gc2.tmp
GC_SECONDS=`dc < /tmp/gc2.tmp | sed -e 's/^\(.*\)\..*$/\1/'`
if [ -z "$GC_SECONDS" ] ; then GC_SECONDS=0  ; fi
let "GC_MINUTES = $GC_SECONDS / 60"

START_SECONDS=`grep --text '^[0-9\.]*:.*$' $FILE | head -n 1 | sed -e 's/^\([0-9]*\).*$/\1/'`
FINISH_SECONDS=`grep --text '^[0-9\.]*:.*$' $FILE | tail -n 1 | sed -e 's/^\([0-9]*\).*$/\1/'`
let "TOTAL_SECONDS = $FINISH_SECONDS - $START_SECONDS"
let "TOTAL_MINUTES = $TOTAL_SECONDS / 60"
let "PERCENT = ($GC_SECONDS * 100) / $TOTAL_SECONDS"

echo "Parsing $FILE: $GC_SECONDS seconds ($GC_MINUTES minutes), $PERCENT% of total time (${TOTAL_SECONDS}s = ${TOTAL_MINUTES}m)"
echo "Largest 5:" $TOP5
