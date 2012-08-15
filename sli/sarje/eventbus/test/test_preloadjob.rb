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

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
TEST_DATA_DIR  = File.join(testdir,"..", "..", "..","acceptance-tests", "test", "data")

require 'test/unit'
require 'eventbus'
require 'time'
require 'json'
require 'json'

class TestEventPubSub < Test::Unit::TestCase
  # fixture tenant entry 
  TENANT_FIXTURE_FILE = File.join(TEST_DATA_DIR, "defaultIngestionTenant_fixture.json")

  TEST_SOURCE_DB = "" 
  TEST_TARGET_DB = "" 

  def setup
    # load modified tenant fixture data into target database 
    mongodb = 

    # load storied dataset into target database 
    # check integrity of the target database 
    # load storied database into source database 
    # load modified tenant fixture into the source database 

  end 

  def test_preload 
        # generate tenant_id and load it into the dabase 
  end 

  def integrity_check
  end 

  # Load a fixture file into the mongodb
  def setFixture(collectionName = @active_config[:mongo_job_collection], fixtureFilePath = @active_config[:fixture_file_path], dropExistingCollection = true)
    dropOption = (dropExistingCollection) ? "--drop":""
    command = "#{MONGOIMPORT_EXEC} #{dropOption} -d #{@active_config[:mongo_db]} -c #{collectionName} -h #{@active_config[:mongo_host]} --file #{fixtureFilePath}"
    status, stdout, stderr = systemu command

    #sh cmd do |success, exit_code|
    #    assert success, "Failure loading fixture data #{fixtureFilePath}: #{exit_code.exitstatus}"
    #end
  end

end 
