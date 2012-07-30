require 'mongo'
require 'json'
require 'thread'

module Eventbus
    class JobScheduler
        def initialize(config)
            # connect to mongo and wrap the lister 
            @mongo_conn          = Mongo::Connection.new(config[:mongo_host], config[:mongo_port])
            @db                  = @mongo_conn.db(config[:mongo_db])
            @coll_scheduled_jobs = config[:mongo_job_collection]
            @listener            = config[:listener]
            @jobrunner           = config[:jobrunner]
            @event_job_map       = {}
            @poll_interval       = config[:poll_interval]

            # start the poller and the event dispatcher 
            @threads = [] 
            @threads << start_poller(10)
            @threads << start_event_dispatcher
        end 

        # blocks until all internal threads terminate
        def join
            @threads.each { |aThread|  aThread.join }
        end

        # ######################################################################
        # Private 
        # ######################################################################
        private

        # Starts the poller that retrieves events from mongo db. 
        def start_poller(poll_interval)
            Thread.new do 
                loop { 
                    # fetch the event types from from mongo and subscribe to them 
                    events = get_evented_jobs
                    @listener.subscribe(events)

                    # sleep for the poll interval
                    sleep(poll_interval)
                }
            end 
        end

        # Listen to events and dispatch them 
        def start_event_dispatcher
            # setup a queue to gather events and run them on haddoop 
            queue = Queue.new

            # listen to events. Note: This will hook into a thread in the listener 
            # the provided block is expected to be non-blocking. 
            @listener.receive do |event| 
                queue << event 
            end 

            # start the thread to process the events 
            Thread.new do
                loop {
                    event = queue.deq

                    # look up the event in the current events table and trigger the job if necessary 
                    if @event_job_map.key?(event[:event_type])
                        schedule_job(@event_job_map[event[:event_type]])
                    end 
                }
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
            all_jobs = @jobrunner.running
            if !all_jobs.find_index(job[:jobname])
                @jobrunner.schedule(job)
            end
        end 

    end # class JobScheduler 
end
