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

require 'eventbus/version'
require 'messaging_service'
require 'jobscheduler'
require 'oplogagent'
require 'hadoop_job_runner'
require "mongo_helper"
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
            "/queue/#{event_type}_events"
        end 

        # fields in all event messages 
        EVENT_ID = "eventId"

        # heartbeat field names 
        HB_NODE_ID   = 'node_id'
        HB_HOSTNAME  = 'hostname'
        HB_TIMESTAMP = 'timestamp'
        HB_EVENTS    = 'events'
    end 

    class EventSubscriber
        include EventPubSubBase 

        def initialize(event_type,logger = nil)
           @logger = logger if logger
           config = {
            :node_name => 'eventsubscriber'
            # :publish_queue_name => Topic_Subscribe,
            # :subscribe_queue_name => Topic_Heartbeat,
            # :start_heartbeat => false
            }
            @messaging = MessagingService.new(config)
            @subscription_channel = @messaging.get_publisher(subscription_address(event_type))
            @events_channel       = @messaging.get_subscriber(events_address(event_type))
            @heartbeat_channel    = @messaging.get_subscriber(HEART_BEAT_ADDRESS)

            # set up the heartbeat listener 
            @current_publishers = {}
            @heartbeat_channel.handle_message do |message|
             # puts "received heartbeat from #{message[HB_NODE_ID]}"
             @logger.info "received heartbeat from #{message[HB_NODE_ID]}" if @logger
                @current_publishers[message[HB_NODE_ID]] = message[HB_EVENTS]
            end 
        end

        # Given a list of event this will subscribe to the 
        def observe_events(events)
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
                :heartbeat_period => 5
            }.merge(config)

            @messaging = MessagingService.new(@config)
            @subscription_channel = @messaging.get_subscriber(subscription_address(event_type))
            @events_channel    = @messaging.get_publisher(events_address(event_type))
            @heartbeat_channel = @messaging.get_publisher(HEART_BEAT_ADDRESS)

            @subscribed_event_ids = []
            @sub_e_ids_lock = Mutex.new

            start_heartbeat(node_id, @config[:heartbeat_period])
        end 

        def handle_subscriptions
            @subscription_channel.handle_message do | event_subs |
                handled_subs = yield event_subs 
                e = if !handled_subs
                        event_subs.map { |x| e[EVENT_ID] }
                    else
                        handled_subs 
                    end
                unique = Set.new e
                @sub_e_ids_lock.synchronize {
                    @subscribed_event_ids = unique.to_a
                }
            end
        end

        def fire_event(event)
            @events_channel.publish(event)
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
                   # puts "publishing heartbeat"
                    @logger.info "publishing heartbeat" if @logger
                    @heartbeat_channel.publish(message)
                    sleep(heartbeat_period)
                end 
            end
        end
    end 
end
