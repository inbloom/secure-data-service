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


require 'rubygems'
require 'mongo'
require 'fileutils'

require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

MONGO_PERFORMANCE_DB_NAME = "mongo_performance"
MONGO_PERFORMANCE_DB = PropLoader.getProps['ingestion_db']
MONGO_PERFORMANCE_DB_PORT = PropLoader.getProps['ingestion_db_port']

############################################################
# STEPS: BEFORE
############################################################

Before do
  @conn = Mongo::Connection.new(MONGO_PERFORMANCE_DB, MONGO_PERFORMANCE_DB_PORT)
end

############################################################
# STEPS: GIVEN
############################################################

Given /^I have a clean database$/ do
  @conn.drop_database(MONGO_PERFORMANCE_DB_NAME)
end

Given /^the minimal indexes on collection "([^"]*)"$/ do |collection_name|
  @db   = @conn[MONGO_PERFORMANCE_DB_NAME]
  @collection = @db[collection_name]
  
  doc = {"name" => "seed", "type" => "seed", "count" => 0, "info" => {"x" => 0, "y" => 0}}
  @collection.insert(doc)
  
  @collection.create_index("count")
end

############################################################
# STEPS: WHEN
############################################################

Given /^I insert "([^"]*)" records into collection "([^"]*)"$/ do |record_count, collection_name|
  @db   = @conn[MONGO_PERFORMANCE_DB_NAME]
  
  count = 0
  max_count = record_count.to_i
  @collection = @db[collection_name]
  time_start = Time.now
  
  while count < max_count  do
   doc = {"name" => "test document", "type" => "test document", "count" => count.to_s, "info" => {"x" => count.to_s, "y" => count.to_s}}
   @collection.insert(doc)
   count +=1;
  end
  
  time_finish = Time.now
  msecs = time_diff_milli time_start, time_finish
  puts "TIME TO INSERT " + record_count + " RECORDS = " + msecs.to_s + " ms"
  puts "AVERAGE TIME TO INSERT = " + (msecs / record_count.to_i).to_s + " ms"
end

Given /^I select "([^"]*)" records from the collection "([^"]*)"$/ do |record_count, collection_name|
  @db   = @conn[MONGO_PERFORMANCE_DB_NAME]
  
  count = 0
  max_count = record_count.to_i
  @collection = @db[collection_name]
  time_start = Time.now
  
  while count < max_count  do
   #puts "i = " + count.to_s
   @document = @collection.find("count" => count.to_s).to_a
   
   #if (@document.count() > 0)
   #  puts "found"
   #else 
   #  puts "not found"
   #end
   
   count +=1;
  end
  
  time_finish = Time.now
  msecs = time_diff_milli time_start, time_finish
  puts "TIME TO SELECT / FIND" + record_count + " RECORDS = " + msecs.to_s + " ms"
  puts "AVERAGE TIME TO SELECT / FIND = " + (msecs / record_count.to_i).to_s + " ms"
end

Given /^I update "([^"]*)" records in the collection "([^"]*)"$/ do |record_count, collection_name|
  @db   = @conn[MONGO_PERFORMANCE_DB_NAME]
  
  count = 0
  max_count = record_count.to_i
  @collection = @db[collection_name]
  time_start = Time.now
  
  while count < max_count  do
   @document = @collection.find("count" => count.to_s).to_a
   
   doc = {"name" => "test document", "type" => "test document", "count" => count.to_s, "info" => {"x" => (count+100).to_s, "y" => (count+100).to_s}}
   @collection.update({"_id" => @document[0]["_id"]}, doc)
   
   count +=1;
  end
  
  time_finish = Time.now
  msecs = time_diff_milli time_start, time_finish
  puts "TIME TO SELECT + UPDATE " + record_count + " RECORDS = " + msecs.to_s + " ms"
  puts "AVERAGE TIME TO SELECT + UPDATE = " + (msecs / record_count.to_i).to_s + " ms"
end

############################################################
# STEPS: THEN
############################################################

Given /^I should see results of database performance$/ do
  puts "---------- Performance Results ----------"
end

############################################################
# FUNCTIONS
############################################################

def time_diff_milli(start, finish)
   (finish - start) * 1000.0
end

############################################################
# END
############################################################
