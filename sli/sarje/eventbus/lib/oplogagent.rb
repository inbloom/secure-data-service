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

# testdir = File.dirname(__FILE__)
# $LOAD_PATH << testdir + "/../lib"

require 'mongo'
require 'thread'
require 'eventbus'

module Eventbus
  class OpLogReader
    def initialize(config = {}, logger = nil)
      @logger = logger if logger
      @config = {
          :mongo_host => 'localhost',
          :mongo_db => 'local',
          :mongo_oplog_collection =>'oplog.rs',
          :mongo_connection_retry => 5,
          :mongo_ignore_initial_read => true
      }.merge(config)
    end

    # read_oplogs blocks on cursor tail read
    def handle_oplogs
      cursor = get_oplog_mongo_cursor(@config[:mongo_ignore_initial_read])
      loop do
        while not cursor.closed?
          begin
            doc = cursor.next 
            if doc
              yield doc
            else
              sleep(1)
            end
          rescue Exception => e
            @logger.error e if @logger
          end
        end
        cursor = get_oplog_mongo_cursor(false)
      end
    end

    def get_oplog_mongo_cursor(initial_empty)
      begin
        connection = get_connection
        db = connection.db(@config[:mongo_db])
        coll = db[@config[:mongo_oplog_collection]]
        cursor = Mongo::Cursor.new(coll, :timeout => false, :tailable => true)
        if initial_empty
          @logger.info "ignoring initial readings" if @logger
          while cursor.has_next?
            cursor.next_document
          end
        end
      rescue Exception => e
        @logger.debug "exception occurred when connecting to mongo for oplog: #{e}" if @logger
        @logger.debug "reconnection attempt in #{@config[:mongo_connection_retry]} seconds" if @logger
        sleep @config[:mongo_connection_retry]
        retry
      end
      return cursor
    end

    def get_connection
      hosts = @config[:mongo_host].split(",").map { |x| x.strip }
      if hosts.size == 1
          host_port = hosts[0].split(":").map { |x| x.strip }
          Mongo::Connection.new(host_port[0], host_port[1])
      else
          Mongo::ReplSetConnection.new(hosts)
      end
    end
  end

  class OpLogThrottler
    def initialize(config = {}, logger = nil)
      @logger = logger
      @throttle_polling_period = config[:collect_events_interval]

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
          # returns an array of hash
          events_to_send = []
          events = Hash.new
          subscription_events = get_subscription_events
          # TODO: this is terribly inefficient when large enough subscription events and messages. Consider optimization.
          subscription_events.each do |subscription_event|
            event_added = false
            messages_to_process.each do |message_to_process|
              break if event_added
              subscription_event['triggers'].each do |trigger|
                if message_to_process == message_to_process.merge(trigger)
                  queue_name = "oplog"
                  queue_name = subscription_event['queue'] if subscription_event['queue'] != nil
                  publish_oplog = subscription_event['publishOplog'] ? true : false
                  # if subscription has publishOplog set, we want to send the oplog to the queue and each message has one oplog entry
                  if (publish_oplog)
                    new_event = Hash[queue_name, [message_to_process]]
                    events_to_send << new_event
                  else
                    # if queue_name is unknown, create it and add it to events_to_send
                    if (!events.has_key?(queue_name))
                      events[queue_name] = [] 
                      events_to_send << {queue_name => events[queue_name]}
                    end
                    events[queue_name] << subscription_event['eventId']
                    event_added = true
                  end
                  break
                end
              end
            end
          end
          if (!events_to_send.empty?)
            @logger.info "sending #{events_to_send.size} events" if @logger
            #events_to_send.each do |evt|
            #  evt.each_pair do |key, value|
            #    @logger.info "events to send to listener #{key}: #{value}" unless @logger.nil?
            #  end
            #end
            yield events_to_send
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

    def initialize(config = {}, logger = nil)
      @logger = logger if logger
      @event_publisher = config[:event_publisher]
      @threads = []

      @oplog_throttler = Eventbus::OpLogThrottler.new(config, logger)
      @oplog_reader = OpLogReader.new(config, logger)

      @threads << Thread.new do
        @oplog_reader.handle_oplogs do |incoming_oplog_message|
          @oplog_throttler.push(incoming_oplog_message)
        end
      end

      @event_publisher.handle_subscriptions do | subscriptions |
        @logger.info "received subscription #{subscriptions}" if @logger
        if subscriptions
          @oplog_throttler.set_subscription_events(subscriptions)
        end
      end

      @threads << Thread.new do
        @oplog_throttler.handle_events do |events|
          @event_publisher.fire_events(events)
        end
      end
    end

    def shutdown
      @threads.each do |thread|
        thread.kill
      end
      @event_publisher.shutdown
    end
  end
end
