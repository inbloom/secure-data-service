#!/bin/bash

clearLandingZone()
{
ssh ingestion@testing1.slidev.org << EOF
rm -rf lz/inbound/*
exit
EOF
}

OUT=`clearLandingZone`

echo $OUT
exit 0