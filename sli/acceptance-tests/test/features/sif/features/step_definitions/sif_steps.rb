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


require 'rubygems'
require 'mongo'
require 'json'
require 'pp'
require 'rest-client'
require 'uuidtools'
require 'fileutils'

require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

SIF_DB_NAME = PropLoader.getProps['sif_database_name']
SIF_DB = PropLoader.getProps['sif_db']
SIF_ZIS_ADDRESS_TRIGGER = PropLoader.getProps['sif_zis_address_trigger']
TENANT_COLLECTION = ["Midgar", "Hyrule", "Security", "Other", "", "TENANT"]

BOOTSTRAPPED_GUIDS = ["2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f", "2012kn-52435872-d66d-11e1-a5ad-406c8f06bd30", "2012lw-d6111b17-d66d-11e1-a5ad-406c8f06bd30"]
MONGO_BIN = ENV['MONGO_HOME'] ? ENV['MONGO_HOME']+"/bin/" : ""

############################################################
# STEPS: BEFORE
############################################################

Before do
  @conn = Mongo::Connection.new(SIF_DB)
  @db = @conn.db(SIF_DB_NAME)

  @postUri = SIF_ZIS_ADDRESS_TRIGGER
  @format = 'application/xml;charset=utf-8'
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end


############################################################
# STEPS: GIVEN
############################################################

# Doesn't remove entities where _id is in BOOTSTRAPPED_GUIDS
Given /^the following collections are clean and bootstrapped in datastore:$/ do |table|
  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db[row["collectionName"]]
    @entity_collection.remove("metaData.tenantId" => {"$in" => TENANT_COLLECTION}, "_id" => {"$nin" => BOOTSTRAPPED_GUIDS})

    puts "There are #{@entity_collection.find("metaData.tenantId" => {"$in" => TENANT_COLLECTION}).count} records in collection " + row["collectionName"] + "."

    if @entity_collection.find("metaData.tenantId" => {"$in" => TENANT_COLLECTION}, "_id" => {"$nin" => BOOTSTRAPPED_GUIDS}).count.to_s != "0"
      @result = "false"
    end
  end
  createIndexesOnDb(@conn, SIF_DB_NAME)
  assert(@result == "true", "Some collections were not cleaned successfully.")
end

Given /^I want to POST a\(n\) "(.*?)" SIF message$/ do |identifier|
  @message = getMessageForIdentifier(identifier)
end

def getMessageForIdentifier(identifier)
  file = File.open(@local_file_store_path + identifier + ".xml", "r")
  message = file.read
  file.close
  uuid = UUIDTools::UUID.random_create
  message = message.sub("***SUB SIF MSG ID***", uuid.to_s)
  return message
end

Given /^the fixture data "(.*?)" has been imported into collection "(.*?)"$/ do |identifier, collection|
  setFixture(collection, "#{identifier}.json", "test/data/sif")
end

def setFixture(collectionName, fixtureFileName, fixtureFilePath="test/data/sif")
  success = system("#{MONGO_BIN}mongoimport -d #{SIF_DB_NAME} -c #{collectionName} -h #{SIF_DB} --file #{fixtureFilePath}/#{fixtureFileName}")
  assert(success, "Exited with code: #{$?.exitstatus}, please confirm that mongo binaries are on your PATH")
end

############################################################
# STEPS: WHEN
############################################################

When /^I POST the message to the ZIS$/ do
  postMessage(@message)
end

def postMessage(message)
  # puts "POSTing message: #{message}"
  headers = {:content_type => @format}
  @res = RestClient.post(@postUri, message, headers){|response, request, result| response }
  # puts(@res.code,@res.body,@res.raw_headers)
  # pp @res
end

When /^I wait for "([^"]*)" seconds$/ do |secs|
  sleep(Integer(secs))
end

############################################################
# STEPS: THEN
############################################################

Then /^I should see following map of entry counts in the corresponding collections:$/ do |table|

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])
    @entity_count = @entity_collection.find("metaData.tenantId" => {"$in" => TENANT_COLLECTION}).count().to_i

    if @entity_count.to_s != row["count"].to_s
      @result = "false"
      red = "\e[31m"
      reset = "\e[0m"
    end

    puts "#{red}There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection. Expected: " + row["count"].to_s+"#{reset}"
  end

  assert(@result == "true", "Some records didn't load successfully.")
end

Then /^I check to find if record is in collection:$/ do |table|
  @result = "true"

  table.hashes.map do |row|
    entities = getEntitiesForParameters(row)
    @entity_count = 0
    @entity_count = entities.size unless entities.nil?

    puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection for record with " + row["searchParameter"] + " = " + row["searchValue"]

    if @entity_count.to_s != row["expectedRecordCount"].to_s
      @result = "false"
    end
  end

  assert(@result == "true", "Some records are not found in collection.")
end

def getEntitiesForParameters(row)
  @entity_collection = @db.collection(row["collectionName"])

  if row["searchType"] == "integer"
      @entities = @entity_collection.find({"$and" => [{row["searchParameter"] => row["searchValue"].to_i}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).to_a
  elsif row["searchType"] == "boolean"
    if row["searchValue"] == "false"
      @entities = @entity_collection.find({"$and" => [{row["searchParameter"] => false}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).to_a
    else
      @entities = @entity_collection.find({"$and" => [{row["searchParameter"] => true}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).to_a
    end
  else
    @entities = @entity_collection.find({"$and" => [{row["searchParameter"] => row["searchValue"]},{"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).to_a
  end

end

Then /^I check that the record contains all of the expected values:$/ do |table|
  table.hashes.map do |row|
    identifier = row["expectedValuesFile"]
    entities = getEntitiesForParameters(row)

    assert(!entities.nil?, "Received nil entities for search parameters")
    assert(entities.size == 1, "Expected one entity, received #{entities.size}")

    entity = entities[0];

    file = File.open(@local_file_store_path + identifier + ".json", "r")
    expectedJson = file.read
    file.close

    expectedMap = JSON.parse(expectedJson)
    expectedMap.each do |key, expected|
      actual = entity[key]
      assert(expected == actual, "Values don't match expected for key: #{key}\nExpected:\t" + expected.to_s + "\nActual:\t" + actual.to_s)
    end

    # must match at this point
    puts "Row matches values in " + identifier + ".json"
  end
end

############################################################
# STEPS: AFTER
############################################################

After do
  @conn.close if @conn != nil
end

############################################################
# END
############################################################
