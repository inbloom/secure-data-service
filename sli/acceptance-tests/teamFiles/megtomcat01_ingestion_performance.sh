#!/bin/sh
sh checkoutAndBuild.sh $1
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 1 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 2 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
sh resetEnvironment.sh
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 1 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
cp /opt/megatron/sli/sli/ingestion/ingestion-core/target/classes/SmallSampleDataSet.zip /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip
ruby /opt/megatron/sli/sli/opstools/ingestion_trigger/publish_file_uploaded.rb STOR /opt/ingestion/lz/inbound/Midgar-DAYBREAK/Day1_Run1.zip localhost
while [ `mongo ingestion_batch_job < count.js | awk {'print $1'} | awk 'NR ==3'` -ne 2 ]
    do
        printf "\32."
        sleep 10
done
printf "\n"
mongo ingestion_batch_job < times.js
