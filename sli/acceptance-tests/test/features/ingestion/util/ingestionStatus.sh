#!/bin/bash

externalDirectoryListing()
{
sftp ingestion@testing1.slidev.org << EOF
cd lz/inbound/
ls -tr
exit
EOF
}

externalFileView()
{
PARAM=$1
ssh ingestion@testing1.slidev.org << EOF
cd lz/inbound/
cat $PARAM
exit
EOF
}


JOB_STRING=$1
VERIFICATION_RECORDS=$2

OUT=`externalDirectoryListing | grep $JOB_STRING | tail -1`
OUT=`externalFileView $OUT`

echo $OUT
exit 0
