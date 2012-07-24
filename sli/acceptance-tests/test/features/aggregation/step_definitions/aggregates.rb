require 'mongo'
require 'json'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'
require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'


Given /^I am using the general data store$/ do
        @local_file_store_path = File.dirname(__FILE__) + "/../../../data/"
end

When /^I run the highest ever aggregation job$/ do
        `hadoop jar $SLI_HOME/POC/aggregation/Math-HighestEver/target/Math-HighestEver-1.0-SNAPSHOT-job.jar`
end

Then /^for the (.*?) with "(.*?)" set to "(.*?)", "(.*?)" is "(.*?)"$/ do |type, query, queryValue, testKey, testValue| 
        coll = @db[type]
        e = coll.find_one({query => queryValue})
        for k in testKey.split(".") do
                e = e[k]
        end
        assert(e == testValue, "#{e} is not equal to #{testValue}")
end

Then /^I see the highest ever test score is "(.*?)"$/ do |score|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)
  assert(aggs[0]["value"] == score, "Aggregate values are #{@res.body}")
end
