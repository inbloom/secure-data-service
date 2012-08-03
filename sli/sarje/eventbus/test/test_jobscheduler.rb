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

require 'test/unit'
require 'eventbus' 
require 'time'
require 'systemu'

class TestJobScheduler < Test::Unit::TestCase

    # the directory where this test lives
    TEST_DIR = File.dirname(__FILE__) + "/"

    def setup
        mongo_helper = Eventbus::MongoHelper.new
        # Set up the fixtures in Mongo 
        mongo_helper.removeDatabase
        mongo_helper.setFixture(CONFIG[:mongo_job_collection], TEST_DIR + 'data/test.json')
    end

    def test_job_reader
      job_reader = Eventbus::JobReader.new(CONFIG)
      jobs, event_job_map = job_reader.get_jobs
      assert(1, jobs.size)
      assert(1, event_job_map.size)
    end

    def test_scheduler 
        # number of events and the delay between events being fired 
        nevents = 100 
        delay   = 0.1

        # add mock listener that emits events and jobrunner that counts scheduled jobs
        listener = MockMQListener.new(nevents, delay)
        jobrunner = EventCountingJobRunner.new
        @active_config[:messaging_service] = listener
        @active_config[:jobrunner] = jobrunner
        @active_config[:mongo_poll_interval] = 100
        @scheduler = Eventbus::JobScheduler.new(@active_config)

        # wait until all events have fired 
        start = Time.now 
        while (!listener.done) && ((Time.now - start) < (nevents * 2 * delay))
            sleep(0.1)
        end
        assert jobrunner.total == nevents, "Not all events were successfully dispatched. #{jobrunner.total}"
    end 

    private
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

    def running_jobs
        return [] 
    end 

    def schedule(job)
        @total += 1 
    end 
end 


