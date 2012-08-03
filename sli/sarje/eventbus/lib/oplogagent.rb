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

require 'mongo'
require 'thread'
require 'eventbus'

module Eventbus
  class OpLogReader
    def initialize(config = {})
      @config = {
          :mongo_host => 'localhost',
          :mongo_port => 27017,
          :mongo_db => 'local',
          :mongo_oplog_collection =>'oplog.rs',
          :mongo_connection_retry => 5,
          :mongo_ignore_initial_read => true
      }.merge(config)
    end

    # read_oplogs blocks on cursor tail read
    def handle_oplogs
      loop do
        cursor = get_oplog_mongo_cursor
        while not cursor.closed?
          begin
            if doc = cursor.next_document
              yield doc
            end
          rescue Exception => e
            puts e
            cursor = get_oplog_mongo_cursor
          end
        end
      end
    end

    def get_oplog_mongo_cursor
      begin
        db = Mongo::Connection.new(@config[:mongo_host], @config[:mongo_port]).db(@config[:mongo_db])
        coll = db[@config[:mongo_oplog_collection]]
        cursor = Mongo::Cursor.new(coll, :timeout => false, :tailable => true)
        if(@config[:mongo_ignore_initial_read])
          while cursor.has_next?
            cursor.next_document
          end
        end
      rescue Exception => e
        puts "exception occurred when connecting to mongo for oplog: #{e}"
        puts "reconnection attempt in #{@config[:mongo_connection_retry]} seconds"
        sleep @config[:mongo_connection_retry]
        retry
      end
      return cursor
    end
  end

  class OpLogThrottler
    def initialize(throttle_polling_period = 5)
      @throttle_polling_period = throttle_polling_period
      @oplog_queue = Queue.new
      @subscription_events_lock = Mutex.new
      set_subscription_events([])
    end

    def handle_events
      loop do
        sleep @throttle_polling_period
        messages_to_process = []
        begin
          loop do
            messages_to_process << @oplog_queue.pop(true)
          end
        rescue
          # no more oplog in oplog queue
        end

        if !messages_to_process.empty?
          event_ids = []
          subscription_events = get_subscription_events
          subscription_events.each do |subscription_event|
            event_added = false
            messages_to_process.each do |message_to_process|
              break if event_added
              subscription_event['triggers'].each do |trigger|
                if message_to_process == message_to_process.merge(trigger)
                  event_ids << subscription_event['eventId']
                  event_added = true
                  break
                end
              end
            end
          end
          if(!event_ids.empty?)
            puts "events to send to listener: #{event_ids}"
            yield event_ids
          end
        end
      end
    end

    def push(oplog)
      @oplog_queue.push(oplog)
    end

    def set_subscription_events(subscription_events)
      @subscription_events_lock.synchronize {
        @subscription_events = subscription_events if subscription_events != nil
      }
    end

    def get_subscription_events()
      @subscription_events_lock.synchronize {
        return @subscription_events
      }
    end
  end

  class OpLogAgent
    attr_reader :threads

    def initialize(config = {})
      config = {
          :publish_queue_name => "/queue/listener",
          :subscribe_queue_name => "/topic/agent"
      }.merge(config)

      @threads = []

      @oplog_throttler = Eventbus::OpLogThrottler.new
      @oplog_reader = OpLogReader.new(config)

      @threads << Thread.new do
        @oplog_reader.handle_oplogs do |incoming_oplog_message|
          @oplog_throttler.push(incoming_oplog_message)
        end
      end

      @messaging_service = Eventbus::MessagingService.new(config)

      @messaging_service.subscribe do |incoming_configuration_message|
        if(incoming_configuration_message != nil)
          @oplog_throttler.set_subscription_events(incoming_configuration_message)
        end
      end

      @threads << Thread.new do
        @oplog_throttler.handle_events do |message|
          @messaging_service.publish(message)
        end
      end
    end

    def shutdown
      @threads.each do |thread|
        thread.kill
      end
      @messaging_service.shutdown
    end
  end
end
