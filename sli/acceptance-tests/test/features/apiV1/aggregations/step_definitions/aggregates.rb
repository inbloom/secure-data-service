require 'mongo'
require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/common_stepdefs.rb'


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

Then /^I see the embedded highest ever test score is "(.*?)"$/ do |score|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)["calculatedValues"]
  assert(aggs[0]["value"] == score, "Calculated values are #{@res.body}")
end

Then /^I navigate to a student with many calculated values$/ do
    step "I navigate to GET \"/v1/students/2012xb-1b3369e7-d511-11e1-b0fd-0811960672a8_id\""
end

When /^I see (\d+) calculated values$/ do |n|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)
  assert(aggs.count == n.to_i, "found #{aggs.count} calculated values, expected #{n}")
end

When /^I navigate to that link and filter by "(.*?)"$/ do |params|
  restHttpGetAbs(@link + "?" + params)
end

When /^each calculated value's (.*?) is "(.*?)"$/ do |name, value|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)
  for agg in aggs do
          assert(agg[name] == value, "incorrect #{name} for calculated value #{agg}, expected #{value}")
  end
end


Then /^I see the proficiency count for (.*?) is (\d+)$/ do |level, count|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)
  assert(aggs[0]["value"][level].to_s() == count, "Aggregate values are #{@res.body}")
end

Then /^I see the embedded proficiency count for (.*?) is (\d+)$/ do |level, count|
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  aggs = JSON.parse(@res.body)["aggregates"]
  puts aggs[0]["value"]
  assert(aggs[0]["value"][level].to_s() == count, "Aggregate values are #{@res.body}")
end
