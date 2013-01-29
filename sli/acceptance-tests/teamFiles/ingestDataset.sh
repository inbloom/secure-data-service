#!/bin/sh
if [ -z "$1" ]; then
    echo "Defaulted to SmallSampleDataSet.zip"
    dataset="SmallSampleDataSet.zip"
else
    dataset=$1
fi
if [ -z "$2" ]; then
    echo "Defaulted to first run of zip file."
    run=1
else
    run=$2
fi
echo "Using $dataset, renamed to dataset.zip for ease of scripting"
cp /opt/datasets/$dataset /opt/datasets/dataset.zip
cp /opt/datasets/dataset.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/dataset.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne $run ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js