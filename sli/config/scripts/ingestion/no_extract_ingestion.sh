#!/bin/bash
echo clearing ingested data from nxmongo sli db using "remove" operations
mongo nxmongo3.slidev.org/sli mongo_scripts/clear_ingested_data.js
mongo nxmongo3.slidev.org/sli mongo_scripts/sli_indexes.js

lz_folder=/home/ingestion/lz/inbound/IL-STATE-DAYBREAK
job_uid=$1
job_name=$2

echo creating file to kick off no-extract ingestion:  $lz_folder/$job_name-$job_uid.noextract
touch $lz_folder/$job_name-$job_uid.noextract
