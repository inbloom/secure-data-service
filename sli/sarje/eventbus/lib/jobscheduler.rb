require 'mongo'
require 'json'
require 'thread'

module Eventbus
    class JobScheduler
        def initialize(listener)
            config = {
                "mongo_host"                => "127.0.0.1",
                "mongo_port"                => 27017,
                "mongo_db"                  => "eventbus",
                "mongo_coll_scheduled_jobs" => "jobdefinitions"
            }

            # connect to mongo and wrap the lister 
            @mongo_conn          = Mongo::Connection.new(config["mongo_host"], config["mongo_port"])
            @db                  = mongo_conn.db(config["mongo_db"])
            @coll_scheduled_jobs = config["mongo_coll_schedule_jobs"]
            @listener            = listener 
            @event_job_map       = {}

            # start the poller and the event dispatcher 
            @threads = [] 
            @threads << start_poller 
            @threads << start_event_dispatcher
        end 

        # ######################################################################
        # Private 
        # ######################################################################
        private

        # Starts the poller that retrieves events from mongo db. 
        def start_poller(poll_intervall)
            Thread.new do 
                while true
                    # fetch the event types from from mongo and subscribe to them 
                    @listener.subscribe(get_evented_jobs)

                    # sleep for the poll interval
                    sleep(poll_interval)
                end 
            end 
        end

        # Listen to events and dispatch them 
        def start_event_eventdispatcher
            # setup a queue to gather events and run them on haddoop 
            queue = Queue.new

            # listen to events. Note: This will hook into a thread in the listener 
            # the provided block is expected to be non-blocking. 
            @listener.receive do |event| 
                queue << event 
            end 

            # start the thread to process the events 
            Thread.new do
                while true
                    event = queue.deq

                    # look up the event in the current events table and trigger the job if necessary 
                    if @event_job_map.key?(event[:event_type])
                        schedule_job(@event_job_map[event[:event_type]])
                    end 
                end 
            end
        end

        # Retrieve the event types from mongo that we want to 
        # subscribe to. This will return an array of hashes that identify the 
        # the specific events. 
        def get_evented_jobs 
            coll = @db.collection(@coll_scheduled_jobs)
            new_event_job_map = {}
            result = coll.find.map do |entry|
                new_event_job_map[entry[:event_type]] = entry
                entry 
            end 
            @event_job_map = new_event_job_map
            result 
        end 

        def schedule_job(job)
            puts "Running job on Hadoop: #{job}"
        end 

    end # class JobScheduler 
end
