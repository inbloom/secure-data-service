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
require_relative '../../../ingestion/features/step_definitions/clean_database.rb'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../odin/step_definitions/data_generation_steps.rb'

After do
  @conn.close if @conn
end

Given /^I have an empty delta collection$/ do
    @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
    @db = @conn[@ingestion_db_name]
    @coll = @db.collection("deltas")
    @coll.remove()
end

Given /^I have an empty bulk extract files collection$/ do
    @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
    @db = @conn["sli"]
    @coll = @db.collection("bulkExtractFiles")
    @coll.remove()
end

When /^I run a small ingestion job$/ do
  ingest_odin("10students")
end

def getEdfiEntities(type)
  case type
    when "educationOrganization" 
      ["EducationOrganization"]
  end
end

Then /^I see deltas for each (.*?) (.*?) operation$/ do |type, operation|
  File.open("#{@odin_working_path}/generated/manifest.json") {|file|
    manifest = JSON.parse(file.read)
    manifest.default = 0
    count = getEdfiEntities(type).map{|t| manifest[t]}.reduce(:+)
    @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
    @db = @conn[@ingestion_db_name]
    @coll = @db['deltas']
    disable_NOTABLESCAN()
    criteria = {"c" => type, "d" => {"$exists" => operation == "delete"}}
    assert_equal(count, @coll.find(criteria).count)
    enable_NOTABLESCAN()
  }
end

Then /^deltas collection should have "(.*?)" records$/ do |count|
    count = count.to_i
    @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
    @db = @conn[@ingestion_db_name]
    @coll = @db['deltas']
    assert(@coll.count() == count, "expecting #{count}, but has #{@coll.count()} delta records")
end

When /^I run a delete ingestion job$/ do
  ingest_odin("deletes/school_10students")
end
