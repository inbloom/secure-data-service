#!/bin/bash

findJobLog()
{
ssh ingestion@testing1.slidev.org << EOF
cd lz/inbound
ls
exit
EOF
}

OUT=`findJobLog`

echo $OUT
exit 0