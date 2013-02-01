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

require 'eventbus/version'
require 'hadoop_job_runner'
require 'jobscheduler'
require 'messaging_service'
require 'mongo_helper'
require 'oplogagent'
require 'set'

module Eventbus

  module EventPubSubBase
    # the AMQ heartbeat topic
    HEART_BEAT_ADDRESS = "/topic/heartbeat"

    # return the AMQ topic string for subscribing
    def subscription_address(event_type)
      "/topic/#{event_type}_subscribe"
    end

    # return the AMQ queue string for pubsub of events
    def events_address(event_type)
      "/queue/#{event_type}"
    end

    # fields in all event messages
    F_EVENT_ID = "eventId"
    F_QUEUE = "queue"

    # heartbeat field names
    HB_NODE_ID   = 'node_id'
    HB_HOSTNAME  = 'hostname'
    HB_TIMESTAMP = 'timestamp'
    HB_EVENTS    = 'events'
  end

  class EventSubscriber
    include EventPubSubBase

    def initialize(config, event_type, queue_name, logger = nil)
      @event_type = event_type
      @logger = logger if logger
      @messaging = MessagingService.new(config, logger)
      @subscription_channel = @messaging.get_publisher(subscription_address(event_type))
      @events_channel       = @messaging.get_subscriber(events_address(queue_name))
      @heartbeat_channel    = @messaging.get_subscriber(HEART_BEAT_ADDRESS)

      # set up the heartbeat listener
      @current_publishers = {}
      @heartbeat_channel.handle_message do |message|
        @logger.info "received heartbeat from #{message[HB_NODE_ID]}" if @logger
        @current_publishers[message[HB_NODE_ID]] = message[HB_EVENTS]
      end
    end

    # Given a list of event this will subscribe to the
    def observe_events(events)
      events.each do |e| 
        if not e.has_key?(F_QUEUE)
          e[F_QUEUE] = @event_type
        end 
      end
      @subscription_channel.publish(events)
    end

    def handle_event(&block)
      @events_channel.handle_message(&block)
    end

    def get_publishers
      @current_publishers
    end
  end

  class EventPublisher
    include EventPubSubBase

    def initialize(node_id, event_type, config = {},logger = nil)
      @logger = logger if logger
      @config = {
          :heartbeat_period => 5,
          :subscription_request_queue => "/queue/subscription/poll"
      }.merge(config)

      @messaging = MessagingService.new(@config, logger)
      @subscription_channel = @messaging.get_subscriber(subscription_address(event_type))
      @event_channels = {}
      @heartbeat_channel = @messaging.get_publisher(HEART_BEAT_ADDRESS)
      @subscription_request_channel = @messaging.get_publisher(events_address(config[:subscription_request_queue]))

      @subscribed_event_ids = []
      @sub_e_ids_lock = Mutex.new

      start_heartbeat(node_id, @config[:heartbeat_period])
    end

    # handle incoming subscriptions. Requires a block
    # and will yield an arry of subscriptions where each element in 
    # the array is a hash that contains at least an 'eventId' field. 
    def handle_subscriptions
      @subscription_channel.handle_message do | event_subs |
        handled_subs = yield event_subs
        e = handled_subs || event_subs.map { |x| [x[F_EVENT_ID], x[F_QUEUE]] }

        # get the subscribed event ids and the queues involved 
        unique = Set.new e
        selected_events = event_subs.select { |x| unique.include?([x[F_EVENT_ID], x[F_QUEUE]]) }
        new_q = Set.new(selected_events.map { |y| y[F_QUEUE] })
        cur_q = Set.new (@event_channels.keys)

        # add new queues that are not open already and remove unnecessary queues 
        (cur_q.difference new_q).each do |q|
            @event_channels[q].close 
            @event_channels.delete(q)
        end 

        @sub_e_ids_lock.synchronize {
          @subscribed_event_ids = unique.to_a
        }
      end
    end

    def fire_events(events)
      unless events.is_a?(Array)
        events = [events]
      end 
      events.each do |event|
        event.each_pair do |q, value|
          begin
            unless @event_channels.has_key?(q)
              @event_channels[q] = @messaging.get_publisher(events_address(q))
            end
            @event_channels[q].publish(value) 
          rescue Exception => e
            @logger.error("Problem occurred publishing event to queue '#{q}': #{e}")
          end
        end
      end
    end

    def shutdown
      @event_channels.each { |q| q.close }
      @heartbeat_thread.terminate
    end 
    
    def publish_msg_for_subscription
      # Sleep 3 seconds to make sure subscribe socket flush before publish send socket to STOMP.
      # This may be a bug in ruby that socket buffer does not flush when flush is called.
      sleep 3
      @subscription_request_channel.publish({})
      @subscription_request_channel.close
    end

    private
    def start_heartbeat(node_id, heartbeat_period)
      @heartbeat_thread = Thread.new do
        loop do
          events_list = []
          @sub_e_ids_lock.synchronize {
            events_list = @subscribed_event_ids
          }
          message = {
              'node_id'   => node_id,
              'hostname'  => Socket.gethostname,
              'timestamp' => Time.now.to_i.to_s,
              'events'    => events_list
          }
          @logger.info "publishing heartbeat: #{message}" if @logger
          @heartbeat_channel.publish(message)
          sleep(heartbeat_period)
        end
      end
    end
  end
end
