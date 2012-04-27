#!/bin/bash

remoteProfile()
{
ssh ingestion@igingest.slidev.org << EOF
head /home/ingestion/logs/performanceStatistics.log | /home/ingestion/tools/performanceTool.sh | head -2
grep -e $1 -e $2 /home/ingestion/logs/performanceStatistics.log | /home/ingestion/tools/performanceTool.sh | tail -1
exit
EOF
}

remoteProfileMongo()
{
ssh ingestion@igingest.slidev.org << EOF
/home/ingestion/tools/mongoPerformanceTool.sh
exit
EOF
}

#TODAY=`date "+%Y-%m-%d"`
#YESTERDAY=`date -d yesterday "+%Y-%m-%d"`
#OUT=`remoteProfile $TODAY $YESTERDAY`

OUT=`remoteProfileMongo`

echo "$OUT"
exit 0