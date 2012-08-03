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

require "eventbus/version"
require "messaging_service"
require "jobscheduler"
require "oplogagent"
require "hadoop_job_runner"

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
    end 

    class EventSubscriber
        include EventPubSubBase 

        def initialize(event_type)
            config = {
            :node_name => 'eventsubscriber',
            :publish_queue_name => Topic_Subscribe,
            :subscribe_queue_name => Topic_Heartbeat,
            :start_heartbeat => false
            }
            @messaging = MessagingService.new(config)
            @subscribe_channel = @messaging.get_publisher(subscription_address)
            @events_channel    = @messaging.get_subscriber(events_address)
            @heartbeat_channel = @messaging.get_subscriber(HEART_BEAT_ADDRESS)

            # set up the heartbeat listener 
            @current_publishers = {}
            @heartbeat_channel.handle_events do |message|
                @current_publishers[message["id"]] = message["events"]
            end 
        end

        # Given a list of event this will subscribe to the 
        def subscribe(events)
            @subscribe_channel.publish(events)
        end 

        def set_event_handler(&block)
            @events_channel.handle_events(&block)
        end 

        def get_publishers
            @current_publishers
        end
    end
end
