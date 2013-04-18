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
require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'
require_relative '../../../ingestion/features/step_definitions/clean_database.rb'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../odin/step_definitions/data_generation_steps.rb'

Given /^I have an empty delta collection$/ do
      steps %Q{
       Given the following collections are empty in datastore:
          | deltas |
      }
end

When /^I run a small ingestion job$/ do
      steps %Q{
        And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
        When I generate the 10 student data set with optional fields on in the generated directory
        And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
        And I copy generated data to the new OdinSampleDataSet directory
        Given I am using odin data store
        And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
        When zip file is scp to ingestion landing zone
        And a batch job for file "OdinSampleDataSet.zip" is completed in database
      }
end

def getEdfiEntities(type)
  case type
    when "educationOrganization" 
      ["LocalEducationAgency", "School", "StateEducationAgency"]
  end
end

Then /^I see deltas for each (.*?) (.*?) operation$/ do |type, operation|
  File.open("#{@odin_working_path}/generated/manifest.json") {|file|
    manifest = JSON.parse(file.read)
    count = getEdfiEntities(type).map{|t| manifest[t]}.reduce(:+)
    @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
    @db = @conn[@ingestion_db_name]
    @coll = @db['deltas']
    assert_equal(count, @coll.find("c" => type, "u" => {"$exists" => operation == "update"}, "d" => {"$exists" => operation == "delete"}).count)
  }
end
