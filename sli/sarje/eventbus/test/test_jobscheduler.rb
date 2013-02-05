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
require 'eventbus' 
require 'time'
require 'systemu'
require 'logger'

class TestJobScheduler < Test::Unit::TestCase

  JOB_COLLECTION = "jobdefinitions"
  # the directory where this test lives
  TEST_DIR = File.dirname(__FILE__) + "/"

  CONFIG = {
      :mongo_host           => "127.0.0.1",
      :mongo_port           => 27017,
      :mongo_db             => "eventbus",
      :mongo_job_collection => "jobs",
      :poll_interval        => 5,
      :fixture_file_path    => TEST_DIR + 'data/test.json'
  }

    def setup
       @logger = Logger.new(STDOUT)
       @logger.level = Logger::INFO

        mongo_helper = Eventbus::MongoHelper.new(CONFIG)
        # Set up the fixtures in Mongo 
        mongo_helper.removeDatabase
        mongo_helper.setFixture
    end

    def test_job_reader
      job_reader = Eventbus::JobReader.new(CONFIG)
      jobs, event_job_map = job_reader.get_jobs
      assert_equal(3, jobs.size)
      assert_equal(3, event_job_map.size)
    end

  def test_event_job_mapper
    job_reader = Eventbus::JobReader.new(CONFIG)
    event_job_mapper = Eventbus::EventJobMapper.new
    jobs, event_job_map = job_reader.get_jobs
    event_job_mapper.set_event_job_map(event_job_map)
    count = 100

    dispatched_events = 0
    count.times do
      event_job_mapper.handle_job(jobs[rand(jobs.size)]['eventId']) do |job|
        dispatched_events += 1
      end
    end
    assert_equal(count, dispatched_events)
  end

  # TODO: fix this test
    #def test_scheduler
    #
    #
    #    # number of events and the delay between events being fired
    #    nevents = 100
    #    delay   = 0.1
    #
    #    # add mock listener that emits events and jobrunner that counts scheduled jobs
    #    mock_event_subscriber = MockEventSubscriber.new(nevents, delay)
    #    jobrunner = EventCountingJobRunner.new
    #    active_config = CONFIG.clone
    #    active_config[:event_subscriber] = mock_event_subscriber
    #    active_config[:jobrunner] = jobrunner
    #    active_config[:mongo_poll_interval] = 100
    #    Eventbus::JobScheduler.new(active_config)
    #
    #    # wait until all events have fired
    #    start = Time.now
    #    while (!mock_event_subscriber.done) && ((Time.now - start) < (nevents * 2 * delay))
    #        sleep(0.1)
    #    end
    #    assert_equal(nevents, jobrunner.total, "Not all events were successfully dispatched. #{jobrunner.total}")
    #end
end

class MockEventSubscriber
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

    def handle_event(&block)

    end 

    def observe_event(events)
      @events << events
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

    def execute_job(job)
        @total += 1 
    end 
end 


