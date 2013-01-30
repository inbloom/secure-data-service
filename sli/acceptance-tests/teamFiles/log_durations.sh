#!/bin/sh
start_date=`mongo ingestion_batch_job < times.js | awk {'print $3'} | awk 'NR==5'`
start_date=${start_date:9: 19}
end_date=`mongo ingestion_batch_job < times.js | awk {'print $3'} | awk 'NR==6'`
end_date=${end_date:9: 19}
let diff1=( `date +%s -d $end_date`-`date +%s -d $start_date` )
status=`mongo ingestion_batch_job < times.js | awk {'print $3'} | awk 'NR==7'`
echo "Day 1 run took $diff1 seconds, starting at $start_date and finishing on $end_date GMT with status $status"
echo "Day 1 run took $diff1 seconds, starting at $start_date and finishing on $end_date GMT with status $status" >> megtomcat01_logs/auto_perf_results.log
start_date=`mongo ingestion_batch_job < times.js | awk {'print $3'} | awk 'NR==11'`
start_date=${start_date:9: 19}
end_date=`mongo ingestion_batch_job < times.js | awk {'print $3'} | awk 'NR==12'`
end_date=${end_date:9: 19}
status=`mongo ingestion_batch_job < times.js | awk {'print $3'} | awk 'NR==7'`
let diff2=( `date +%s -d $end_date`-`date +%s -d $start_date` )
echo "Day N run took $diff2 seconds, starting at $start_date and finishing on $end_date GMT with status $status"
echo "Day N run took $diff2 seconds, starting at $start_date and finishing on $end_date GMT with status $status" >> megtomcat01_logs/auto_perf_results.log