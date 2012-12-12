=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../utils/api_utils.rb'
require_relative '../../utils/sli_utils.rb'




###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  id = "educationOrganizations"                  if human_readable_id == "EDORG URI"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"    if human_readable_id == "South Daybreak Elementary ID"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  
  id = "0636ffd6-ad7d-4401-8de9-40538cf696c8" if human_readable_id == "'Preston Muchow' ID"
  id = "f7094bd8-46fc-4204-9fa2-a383fb71bdf6" if human_readable_id == "'Mayme Borc' ID"
  id = "6bfbdd9a-441a-490a-9f83-716785b61829" if human_readable_id == "'Malcolm Costillo' ID"
  id = "891faebe-bc84-4e0c-b7f3-195637cd981e" if human_readable_id == "'Tomasa Cleaveland' ID"
  id = "ffee781b-22b1-4015-81ff-3289ceb2c113" if human_readable_id == "'Merry Mccanse' ID"
  id = "5dd72fa0-98fe-4017-ab32-0bd33dc03a81" if human_readable_id == "'Samantha Scorzelli' ID"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15_id" if human_readable_id == "'Matt Sollars' ID"
  id = "32932b97-d466-4d3c-9ebe-d58af010a87c" if human_readable_id == "'Dominic Brisendine' ID"
  id = "6f60028a-f57a-4c3d-895f-e34a63abc175" if human_readable_id == "'Lashawn Taite' ID"
  id = "4f81fd4c-c7c5-403e-af91-6a2a91f3ad04" if human_readable_id == "'Oralia Merryweather' ID"
  id = "766519bf-31f2-4140-97ec-295297bc045e" if human_readable_id == "'Dominic Bavinon' ID"
  id = "e13b1a7a-81d6-474c-b751-a6af054dbd6a" if human_readable_id == "'Rudy Bedoya' ID"
  id = "a17bd536-7502-4a4d-9d1f-538792b86795" if human_readable_id == "'Verda Herriman' ID"
  id = "7062c584-e229-4763-bf40-aec36bf112e7" if human_readable_id == "'Alton Maultsby' ID"
  id = "b1312b46-0a6b-4c6d-b73a-8cd7981e260e" if human_readable_id == "'Felipe Cianciolo' ID"
  id = "e0c2e40a-a472-4e78-9736-5ed0cbc16018" if human_readable_id == "'Lyn Consla' ID"
  id = "7ac04245-d931-447c-b8b2-aeef63fa3a4e" if human_readable_id == "'Felipe Wierzbicki' ID"
  id = "5714e819-0323-4281-b8d6-83604d3e95e8" if human_readable_id == "'Gerardo Giaquinto' ID"
  id = "2ec521f4-38e9-4982-8300-8df4eed2fc52" if human_readable_id == "'Holloran Franz' ID"
  id = "f11f341c-709b-4c8e-9b08-da9ff89ec0a9" if human_readable_id == "'Oralia Simmer' ID"
  id = "e62933f0-4226-4895-8fe3-aaffd5556032" if human_readable_id == "'Lettie Hose' ID"
  id = "903ea314-8212-4e9f-92b7-a18a25059804" if human_readable_id == "'Gerardo Saltazor' ID"
  id = "0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba" if human_readable_id == "'Lashawn Aldama' ID"
  id = "37edd9ae-3ac2-4bba-a8d8-be57461cd6de" if human_readable_id == "'Alton Ausiello' ID"
  id = "1d3e77f6-5f07-47c2-8086-b5aa6f4d703e" if human_readable_id == "'Marco Daughenbaugh' ID"
  id = "dbecaa89-29e6-41e1-8099-f80e29baf48e" if human_readable_id == "'Karrie Rudesill' ID"
  id = "414106a9-6156-47e3-a477-4bd4dda7e21a" if human_readable_id == "'Damon Iskra' ID"
  id = "e2d8ba15-953c-4cf7-a593-dbb419014901" if human_readable_id == "'Gerardo Rounsaville' ID"
    
  #return the translated value
  id
end





###############################################################################
# BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
###############################################################################

Before do
  DB_NAME = PropLoader.getProps['api_database_name']
  DB_HOST = PropLoader.getProps['DB_HOST']
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I drop the "([^\"]*)" collection$/ do |collection|
  #steps %Q{
  #  Given the following collections are empty in datastore:
  #    | collectionName |
  #    | #{collection}  |
  #}

  @conn = Mongo::Connection.new(DB_HOST)
  @db = @conn[DB_NAME]

  entity_collection = @db[collection]
  puts "There are #{entity_collection.count} records in collection #{collection}."
  entity_collection.remove()

  assert(entity_collection.count.to_i == 0, "#{collection} is not empty")
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^there should be (\d+) records in the "([^\"]*)" collection$/ do |count, collection|
  #steps %Q{
  #  Then I should see following map of entry counts in the corresponding collections:
  #    | collectionName | count    |
  #    | #{collection}  | #{count} |
  #}

  @conn = Mongo::Connection.new(DB_HOST)
  @db = @conn[DB_NAME]

  @entity_collection = @db[collection]
  assert(@entity_collection.count.to_i == count.to_i, "#{collection} is not empty")
end

Then /^"([^\"]*)" field is "([^\"]*)" for all records$/ do |field, value|
  @entity_collection.find.each do |record|
    assert(record[field].to_s == value.to_s,
           "For #{record["_id"]}, expected #{field} to be #{value}, actual value: #{record[field].to_s}")
  end
end

###############################################################################
# AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER
###############################################################################

After do
  @conn.close if !@conn.nil?
end