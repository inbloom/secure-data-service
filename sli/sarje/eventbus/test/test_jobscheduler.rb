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

require 'test/unit'
require 'eventbus' 
require 'time'

class TestJobScheduler < Test::Unit::TestCase
    JOB_COLLECTION = "jobdefinitions"
    CONFIG = {
      :mongo_host           => "127.0.0.1",
      :mongo_port           => 27017,
      :mongo_db             => "eventbus",
      :mongo_job_collection => "jobs",
      :poll_interval        => 5
    }

    # the directory where this test lives
    TEST_DIR = File.dirname(__FILE__) + "/"

    def setup
        @active_config = CONFIG.clone 

        # Set up the fixtures in Mongo 
        removeDatabase
        setFixture(CONFIG[:mongo_job_collection], TEST_DIR + 'data/eb_jobs.json')
    end

    def test_scheduler 
        # number of events and the delay between events being fired 
        nevents = 100 
        delay   = 0.1

        # add mock listener that emits events and jobrunner that counts scheduled jobs
        listener = MockMQListener.new(nevents, delay)
        jobrunner = EventCountingJobRunner.new
        @active_config[:listener] = listener
        @active_config[:jobrunner] = jobrunner
        @active_config[:poll_interval] = 100    
        @scheduler = Eventbus::JobScheduler.new(@active_config)

        # wait until all events have fired 
        start = Time.now 
        while (!listener.done) && ((Time.now - start) < (nevents * 2 * delay))
            sleep(0.1)
        end
        assert jobrunner.total == nevents, "Not all events were successfully dispatched. #{jobrunner.total}"
    end 

    private 

    # remove the entire database 
    def removeDatabase
        connection_str = "#{@active_config[:mongo_host]}:#{@active_config[:mongo_port]}/#{@active_config[:mongo_db]}"
        cmd = "mongo --eval \"db.dropDatabase()\" #{connection_str} "
        sh cmd 
    end 

    # Load a fixture file into the mongodb 
    def setFixture(collectionName, fixtureFilePath, dropExistingCollection=true)
        dropOption = (dropExistingCollection) ? "--drop":""
        cmd = "mongoimport #{dropOption} -d #{@active_config[:mongo_db]} -c #{collectionName} -h #{@active_config[:mongo_host]} --file #{fixtureFilePath}"
        sh cmd do |success, exit_code|
            assert success, "Failure loading fixture data #{fixtureFilePath}: #{exit_code.exitstatus}"
        end
    end
end

class MockMQListener 
    attr_reader :done
    def initialize(n_events, delay)
        @receive_block = nil 
        @events = nil 
        @done = false 
        @event_thread = Thread.new do 
            sleep(2)
            for i in 1..n_events 
                rb = @receive_block 
                ev = @events 
                if rb && ev
                    rb.call(ev[rand(ev.length)])
                end 
                sleep(delay)
            end 
            @done = true 
        end 
    end 

    def subscribe(events)
        @events = events
    end 

    def receive(&block)
        @receive_block = block 
    end 
end

class EventCountingJobRunner 
    attr_reader :total 

    def initialize
        @total = 0 
    end 

    def running
        return [] 
    end 

    def schedule(job)
        @total += 1 
    end 
end 


