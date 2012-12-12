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

  id = "students"                                   if human_readable_id == "STUDENT URI"
  
  id = "0636ffd6-ad7d-4401-8de9-40538cf696c8" if human_readable_id == "'Preston Muchow' ID"
    
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