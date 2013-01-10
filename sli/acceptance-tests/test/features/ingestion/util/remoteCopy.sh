#!/bin/bash

remoteCopy()
{
PARAM1=$1
PARAM2=$2
ssh ingestion@igingest.slidev.org << EOF
cp $PARAM1 $PARAM2
exit
EOF
}


ORIGIN=$1
DESTINATION=$2

OUT=`remoteCopy $ORIGIN $DESTINATION`

echo $OUT
exit 0
