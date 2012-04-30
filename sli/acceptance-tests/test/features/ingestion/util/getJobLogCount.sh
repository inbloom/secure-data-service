#!/bin/bash

getJobLogCount()
{
ssh ingestion@testing1.slidev.org << EOF
cd lz/inbound
ls -1 job.*.log | wc -l
exit
EOF
}

OUT=`getJobLogCount`

echo $OUT
exit 0