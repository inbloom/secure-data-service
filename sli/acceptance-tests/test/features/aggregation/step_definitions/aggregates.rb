require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'


Given /^I am using the general data store$/ do
          @local_file_store_path = File.dirname(__FILE__) + "/../../../data/"
end

When /^I run the aggregation job$/ do
        `hadoop jar $SLI_HOME/POC/aggregation/US2875-HighestEver-ACT/target/US2875-HighestEver-ACT-1.0-SNAPSHOT-job.jar`
end

