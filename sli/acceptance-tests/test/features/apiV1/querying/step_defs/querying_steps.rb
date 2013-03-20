=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end



require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'
require_relative '../../utils/api_utils.rb'
require_relative '../../selectors/step_definitions/selectors.rb'
require_relative '../../../search/step_definitions/search_indexer_steps.rb'
require 'test/unit'
require 'mongo'
require 'stomp'
require 'json'

Before do
  extend Test::Unit::Assertions
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.*)>$/ do |resource_name|
   data = resource_name
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################
When /^I query "([^"]*)" of "([^"]*)" to demonstrate "([^"]*)"$/ do |resource_name, school_id, test_type|
  if school_id == ""
    step "I navigate to GET \"/<#{resource_name}>\""
  else
    if (resource_name == "reportCards")
      step "I navigate to GET \"/<schools/#{school_id}/studentSchoolAssociations/students/#{resource_name}>\""
    else
      step "I navigate to GET \"/<schools/#{school_id}/#{resource_name}>\""
    end
  end
end

Given /^I should see a sorted list sorted by "([^"]*)" in "([^"]*)" order$/ do |sortBy, sortOrder|
  possibleSortOrder = ["ascending", "descending"]
  assert(possibleSortOrder.include?(sortOrder), "#{sortOrder} must be one of #{possibleSortOrder}")
  @sorted_result = @result.collect {|x| x[sortBy]}
  @sorted_result.each_cons(2) do |x, y|
    assert(sortOrder == "descending" ? x >= y : x <= y, "list not sorted: x = #{x}, y = #{y}")
  end
end

# depends on /^I should see a sorted list sorted by "([^"]*)" in "([^"]*)" order$/
And /^I should see a sorted list with "([^"]*)" offset and "([^"]*)" limit sorted by "([^"]*)"$/ do |offset, limit, sortBy|
  expected = @sorted_result[(offset.to_i)..(offset.to_i + limit.to_i - 1)]
  actual = @result.collect {|x| x[sortBy]}
  assert_equal(expected, actual)
end

Then /^in the response body I should not see field "(.*?)"$/ do |field|
  assert(!(@result.has_key? field))
end

Then /^the executed path should not equal the requested "(.*?)"$/ do |requested_path|
    headers = @res.raw_headers
    assert(headers != nil, "Headers are nil")
    assert(headers['x-executedpath'] != nil, "There is no executed path info from the previous request")
    executed_path = headers['x-executedpath'][0]
    assert(executed_path != requested_path, "The executed path should have shown the implied (injected) context but did not")
end

Then /^each entity's response body I should see the following fields only:$/ do |table|
  @result.each do |res|
    check_number_of_fields(res, table)
    check_contains_fields(res, table)
  end
end

When /^I update the "(.*?)" with ID "(.*?)" field "(.*?)" to "(.*?)"$/ do |collection, id, field, value|
  conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  midgar = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"
  mdb = conn.db(midgar)

  coll = mdb[collection]
  entity = coll.find_one({"_id" => id})
  assert(entity, "cant find #{collection} with id #{id}")
  entry = entity
  subfields = field.split(".")
  last = subfields.pop
  subfields.each { |subfield|
    entry = entry[subfield]
  }
  entry[last] = value
  puts "saving entity: #{entity}"
  coll.save(entity)
end

When /^I send an update event to the search indexer for collection "(.*?)" and ID "(.*?)"$/ do |collection, id|
  midgar = "02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a"
  message = [{"ns" => "#{midgar}.#{collection}",
              "o" => { "$set" => { "type" => collection } },
              "o2" => { "_id" => id },
              "op" => "u"
             }]
  client = Stomp::Client.new
  client.publish("search", message.to_json)
end

Then /^I will EVENTUALLY GET "(.*?)" with (\d+) elements$/ do |query, count|
  success = false
  10.times {
    step "I navigate to GET \"#{query}\""
    if count.to_i == @result.size
      success = true
      break
    end
    sleep 1
  }
  assert(success, "expected #{count} elements but got back #{@result.size}")
end

















