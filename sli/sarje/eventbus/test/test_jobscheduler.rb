require 'test/unit'
require 'eventbus' 

class MockMQListener 
    def initialize(n_events)
        @receive_block = nil 
        @events = nil 
        @finished = false 
        @event_thread = Thread.new do 
            for i in 1..n_events 
                rb = @receive_block 
                ev = @events 
                if rb && ev
                    rb.call(ev[rand(ev.length)])
                end 
                sleep(0.1)
            end 
            @finished = true 
        end 
    end 

    def subscribe(events)
        @events = events
    end 

    def receive(&block)
        @receiver_func = block 
    end 

    def done
        @finished 
    end 
end

class EventCountingJobRunner 
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

class TestJobScheduler < Test::Unit::TestCase
    JOB_COLLECTION = "jobdefinitions"
    CONFIG = {
      :mongo_host           => "127.0.0.1",
      :mongo_port           => 27017,
      :mongo_db             => "eventbus",
      :mongo_job_collection => "eb_jobs"
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
        listener = MockMQListener.new(100)
        @active_config[:listener] = listener 
        @active_config[:jobrunner] = EventCountingJobRunner.new
        @scheduler = Eventbus::JobScheduler.new(@active_config)

        while !listener.done
            sleep(0.1)
        end
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
