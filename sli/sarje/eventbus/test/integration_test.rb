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

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"

require 'test/unit'
require 'mongo'
require 'eventbus'

class TestIntegration < Test::Unit::TestCase

  JOB_COLLECTION = "jobs"
  # the directory where this test lives
  TEST_DIR = File.dirname(__FILE__) + "/"

  CONFIG = {
      :mongo_host           => "127.0.0.1",
      :mongo_port           => 27017,
      :mongo_db             => "eventbus",
      :mongo_job_collection => "jobs",
      :poll_interval        => 5,
      :fixture_file_path    => TEST_DIR + 'data/integration_job.json'
  }
  CONFIG1 = {
      :mongo_host           => "127.0.0.1",
      :mongo_port           => 27017,
      :mongo_db             => "sli",
      :mongo_job_collection => "sarjeTestIntegration",
      :poll_interval        => 5,
      :fixture_file_path    => TEST_DIR + 'data/sarjeTestIntegration.json'
  }

    def setup
        @db = Mongo::Connection.new.db(CONFIG1[:mongo_db])
        mongo_helper = Eventbus::MongoHelper.new(CONFIG)
        # Set up the fixtures in Mongo 
        mongo_helper.removeDatabase
        mongo_helper.setFixture
         end

    def test_integration
       student_coll = @db["student"]
       student_coll.remove({"_id" => "sarje-test-id"})
       count = student_coll.find({"_id" => "sarje-test-id"}).count
       assert_equal(0, count)

       sleep(20)
       mongo_helper = Eventbus::MongoHelper.new(CONFIG1) 
       mongo_helper.removeDatabase
       mongo_helper.setFixture
       
       sleep(20)
       count = student_coll.find({"_id" => "sarje-test-id"}).count
       assert_equal(1, count)

    end
end 


