require 'mongo'
require 'json'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'
#require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'


#Given /^I am using the general data store$/ do
#        @local_file_store_path = File.dirname(__FILE__) + "/../../../data/"
#end

#When /^I run the highest ever aggregation job$/ do
#        `hadoop jar $SLI_HOME/POC/aggregation/Math-HighestEver/target/Math-HighestEver-1.0-SNAPSHOT-job.jar`
#end

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

Then /^I navigate to a student with many aggregates$/ do
    step "I navigate to GET \"/v1/students/2012xb-1b3369e7-d511-11e1-b0fd-0811960672a8\""
end

When /^I see (\d+) aggregates$/ do |n|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)
  assert(aggs.count == n.to_i, "found #{aggs.count} aggregates, expected #{n}")
end

When /^I navigate to that link and filter by "(.*?)"$/ do |params|
  restHttpGetAbs(@link + "?" + params)
end

When /^each aggregate's (.*?) is "(.*?)"$/ do |name, value|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)
  for agg in aggs do
          assert(agg[name] == value, "incorrect #{name} for aggregate #{agg}, expected #{value}")
  end
end

