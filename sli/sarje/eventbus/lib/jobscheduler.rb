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

require 'mongo'
require 'json'
require 'thread'

module Eventbus
    class JobReader
      def initialize(config)
        @config = config
        @mongo_conn          = Mongo::Connection.new(config[:mongo_host], config[:mongo_port])
        @db                  = @mongo_conn.db(config[:mongo_db])
        @coll_scheduled_jobs = config[:mongo_job_collection]
      end

      # Retrieve the event types from mongo that we want to
      # subscribe to. This will return an array of hashes that identify the
      # the specific events.
      def get_jobs
        coll = @db.collection(@coll_scheduled_jobs)
        event_job_map = {}
        jobs = coll.find.map do |entry|
          event_job_map[entry["eventId"]] = entry
          entry
        end
        return jobs, event_job_map
      end
    end

    class EventJobMapper
      def initialize
        @event_job_map_lock = Mutex.new
        set_event_job_map({})
      end

      def get_event_job_map
        @event_job_map_lock.synchronize {
          return @event_job_map
        }
      end

      def set_event_job_map(event_job_map)
        @event_job_map_lock.synchronize {
          @event_job_map = event_job_map if event_job_map != nil
        }
      end

      def handle_jobs(event_ids)
        event_job_map = get_event_job_map
        # look up the event in the current events table and trigger the job if necessary
        event_ids.each do |event_id|
          if event_job_map.key?(event_id)
            yield event_job_map[event_id]
          end
        end
      end
    end

    class JobScheduler
        def initialize(config)
            # connect to mongo and wrap the lister
            @event_subscriber    = config[:event_subscriber]
            @jobrunner           = config[:jobrunner]
            @event_job_map       = {}
            @poll_interval       = config[:mongo_poll_interval]

            event_job_mapper = Eventbus::EventJobMapper.new

            @threads = []

            # start the Job Reader and publish Jobs (aka agent's Subscription Events) to messaging service
            @threads << Thread.new do
              job_reader = Eventbus::JobReader.new(config)
              loop do
                jobs, event_job_map = job_reader.get_jobs
                puts "publishing #{jobs}"
                @event_subscriber.observe_events(jobs)
                event_job_mapper.set_event_job_map(event_job_map)
                sleep @poll_interval
              end
            end

            # subscribe to events and enqueues them
            event_ids_queue = Queue.new
            @threads << Thread.new do
              @event_subscriber.handle_event do |event_ids|
                event_ids_queue << event_ids
              end
            end

            # process the Event IDs in the queue and then run the corresponding jobs
            @threads << Thread.new do
              loop do
                event_ids = event_ids_queue.deq
                event_job_mapper.handle_jobs(event_ids) do |job|
                  puts "running job #{job}"
                  @jobrunner.execute_job(job)
                end
              end
            end
        end

        # blocks until all internal threads terminate
        def join
            @threads.each { |aThread|  aThread.join }
        end
    end # class JobScheduler 
end
