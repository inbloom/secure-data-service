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

# testdir = File.dirname(__FILE__)
# $LOAD_PATH << testdir + "/../lib"

require 'mongo'
require 'thread'
require 'eventbus'
require 'messaging_service'

module Eventbus
  class OpLogReader
    def initialize(config = {}, logger = nil)
      @logger = logger if logger
      @config = {
          :mongo_host => 'localhost',
          :mongo_db => 'local',
          :mongo_oplog_collection =>'oplog.rs',
          :mongo_connection_retry => 5,
          :mongo_ignore_initial_read => true,
          :mongo_timestamp_marker => 'oplog.marker'
      }.merge(config)
      @connection = get_connection
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
          rescue Mongo::OperationFailure => ex
            # catch invalid cursor on mongo reconnects
            cursor = mongo_operation_exception_handler(ex, cursor)
          rescue Exception => e
            @logger.error e if @logger
          end
        end
        cursor = get_oplog_mongo_cursor(false)
      end
    end

    def get_oplog_mongo_cursor(initial_empty)
      begin
        db = @connection.db(@config[:mongo_db])
        coll = db[@config[:mongo_oplog_collection]]
        cursor = nil
        if initial_empty
          @logger.info "ignoring initial readings" if @logger
          last_ts = get_last_timestamp()
          # if time stamp is not found in mongo, use the current time
          if !last_ts
            @logger.info "using current timestamp" if @logger
            last_ts = Time.now.to_i
          end
          @logger.info "using timestamp #{last_ts}" if @logger
          cursor = coll.find({'ts' => {'$gte'=> BSON::Timestamp.new(Integer(last_ts), 0) }})
          cursor.add_option(Mongo::Constants::OP_QUERY_TAILABLE)
          cursor.add_option(Mongo::Constants::OP_QUERY_OPLOG_REPLAY)
          cursor.add_option(Mongo::Constants::OP_QUERY_NO_CURSOR_TIMEOUT)
        
          #TODO Do we want to go to end? or just start processing from here?
          while cursor.has_next?
            cursor.next_document
          end
          @logger.debug "reached end of cursor" if @logger
        else
          cursor = Mongo::Cursor.new(coll, :timeout => false, :tailable => true)
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
    
    def save_timestamp(seconds)
      begin
        @logger.info "Saving timestamp " + seconds.to_s if @logger
        coll = get_oplog_marker_collection()
        node_id = get_oplog_marker_id
        doc = coll.find_and_modify({
          :query => {"_id" => node_id},
          :update => {"$set" => {"timeStamp" => seconds, "lastUpdated" => Time.now}},
          :new => true,
          :upsert => true
        })
      rescue Exception => e 
        @logger.error "Cannot save timestamp to mongo: #{e}" if @logger
      end
    end
    
    def get_last_timestamp()
      coll = get_oplog_marker_collection()
      node_id = get_oplog_marker_id
      begin
        doc = coll.find_one({"_id" => node_id})
        if doc 
          return doc["timeStamp"]
        else
          return nil
        end
      rescue Exception => e
        @logger.error "Cannot query collection #{e}" if @logger
      end
    end
    
    def get_oplog_marker_id
      @config[:node_id] + Socket.gethostname + "-" +  @config[:mongo_host] 
    end
    
    def get_oplog_marker_collection()
      coll = nil
      begin
        db = @connection.db(@config[:mongo_db])
        coll = db[@config[:mongo_timestamp_marker]]
      rescue Exception => e
        @logger.debug "exception occurred when connecting to mongo for oplog.marker: #{e}" if @logger
        @logger.debug "reconnection attempt in #{@config[:mongo_connection_retry]} seconds" if @logger
        sleep @config[:mongo_connection_retry]
        retry
      end
      return coll
   end
   
   def mongo_operation_exception_handler(ex, cursor)
     @logger.error ex if @logger
     if !cursor.closed?
       cursor.close
     end 
     return get_oplog_mongo_cursor(true) 
   end
   
  end

  class OpLogThrottler
    def initialize(config = {}, logger = nil)
      @logger = logger
      @throttle_polling_period = config[:collect_events_interval]

      @oplog_queue = Queue.new
      @subscription_events_lock = Mutex.new
      @last_oplog_timestamp = 0
      set_subscription_events([])
    end

    def handle_events(&block)
      loop do
        sleep @throttle_polling_period
        process_messages &block
      end
    end

    def process_messages
      messages_to_process = []
      begin
        loop do
          messages_to_process << @oplog_queue.pop(true)
        end
      rescue
        # no more oplog in oplog queue
      end

      unless messages_to_process.empty?
        # returns an array of hash
        events_to_send = []
        events = {}
        subscription_events = get_subscription_events
        # TODO: this is terribly inefficient when large enough subscription events and messages. Consider optimization.
        subscription_events.each do |subscription_event|
          messages_to_process.each do |message_to_process|
            subscription_event['triggers'].each do |trigger|
              ns = trigger["ns"]
              trigger = trigger.reject{|k,v| k == "ns"}
              if ns && message_to_process["ns"] && !message_to_process["ns"].match(ns)
                next
              end
              if message_to_process == message_to_process.merge(trigger)
                queue_name = subscription_event['queue'] || "oplog"
                # if subscription has publishOplog set, we want to send the oplog to the queue and each message has one oplog entry
                if message_to_process["ts"].seconds > @last_oplog_timestamp
                  @last_oplog_timestamp = message_to_process["ts"].seconds
                end
                if subscription_event['publishOplog']
                  events_to_send << {queue_name => [message_to_process]}
                else
                  unless events[queue_name]
                    events[queue_name] = []
                    events_to_send << {queue_name => events[queue_name]}
                  end
                  events[queue_name] << subscription_event['eventId']
                  break
                end
              end
            end
          end
        end
        unless events_to_send.empty?
          @logger.info "sending #{events_to_send.size} events" if @logger
          yield events_to_send
        end
      end
    end

    def push(oplog)
      @oplog_queue.push(oplog)
    end

    def set_subscription_events(subscription_events)
      if subscription_events
        @subscription_events_lock.synchronize {
          @subscription_events = subscription_events
        }
      end
    end

    def get_subscription_events()
      @subscription_events_lock.synchronize {
        return @subscription_events
      }
    end
    
    def get_last_oplog_timestamp()
      return @last_oplog_timestamp
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
          @oplog_reader.save_timestamp(@oplog_throttler.get_last_oplog_timestamp)
        end
      end
      
      # Publish msg to queue to ensure oplogagent gets subscription on startup
      @event_publisher.publish_msg_for_subscription
    end

    def shutdown
      @threads.each do |thread|
        thread.kill
      end
      @event_publisher.shutdown
    end
  end
end
