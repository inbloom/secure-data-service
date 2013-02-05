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

require 'json'
require 'thread'

module Eventbus

  class MongoHelper
    #TODO: move this out to a property
    MONGO_HOME = "/usr/local"
    MONGO_EXEC = "mongo"
    MONGOIMPORT_EXEC = "mongoimport"

    def initialize(config)
      @active_config = config
    end

    # remove the entire database
    def removeDatabase
      connection_str = "#{@active_config[:mongo_host]}:#{@active_config[:mongo_port]}/#{@active_config[:mongo_db]}"
      status, stdout, stderr = systemu "#{MONGO_EXEC} --eval \"db.dropDatabase()\" #{connection_str}"
    end

    # Load a fixture file into the mongodb
    def setFixture(collectionName = @active_config[:mongo_job_collection], fixtureFilePath = @active_config[:fixture_file_path], dropExistingCollection = true)
      dropOption = (dropExistingCollection) ? "--drop":""
      command = "#{MONGOIMPORT_EXEC} #{dropOption} -d #{@active_config[:mongo_db]} -c #{collectionName} -h #{@active_config[:mongo_host]} --file #{fixtureFilePath}"
      #puts "COMMAND: #{command}"
      status, stdout, stderr = systemu command

      #sh cmd do |success, exit_code|
      #    assert success, "Failure loading fixture data #{fixtureFilePath}: #{exit_code.exitstatus}"
      #end
    end
  end
end
