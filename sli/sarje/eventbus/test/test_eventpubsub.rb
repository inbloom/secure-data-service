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

require 'test/unit'
require 'eventbus'
require 'time'

class TestEventPubSub < Test::Unit::TestCase
    FIRE_N_EVENTS = 5 
    EVENT_TYPE = "testevent"

    def setup 
        @queue_config = {

        }
    end 

    def test_eventpubsub
        # set up two agents and a subscriber 
        test_publisher_1 = TestAgent.new (FIRE_N_EVENTS, "agent_1", EVENT_TYPE, @queue_config)
        test_publisher_2 = TestAgent.new (FIRE_N_EVENTS, "agent_2", EVENT_TYPE, @queue_config)
        event_subscriber = Eventbus::EventSubscriber.new (EVENT_TYPE)

        # set up the event handler 
        fired_events = {} 
        fired_events.default=(0)
        event_subscriber.handleEvent do |event|
            id = event[:id]
            fired_events[id] = fired_events[id] + 1
        end

        # subscribe to the given list of events 
        event_subscriber.subscribe(@event_subscriptions)

        # fire the events for both agents and wait until they are done 
        threads = []
        threads << test_publisher_1.fire_events 
        threads << test_publisher_2.fire_events 
        threads.each { |aThread| aThread.join }

        # waiting for all events to arrive 
        sleep(5)

        # make sure that all the agents are online and they publish all events 
        all_publishers = event_subscriber.get_publishers
        assert all_publishers.length == 2, "Expected 2 event publishers, but got #{all_publishers.length}."
        event_subscriber.publishers.each do |a_publisher|
            assert a_publisher[:events].sort() == @event_ids.sort, "Publisher #{a_publisher[:pubid]} does not publish all events"
        end 

        # make sure that each event was fired the correct number of times 
        fired_events.each do |k,v| 
            assert v == FIRE_N_EVENTS, "Event #{k} was not fired #{FIRE_N_EVENTS} times."
        end
    end 
end 

class TestAgent 
    def initialize(fire_n_events, id, event_type, mq_config) 
        @fire_n_events = fire_n_events 
        @e_publisher = Eventbus::EventPublisher.new (id, event_type, queue_config)

        # setup the subscription handler on the publisher side 
        @trigger_event_map = {}
        @e_publisher.handleSubscription do | event_subs |
            will_provide = [] 
            event_subs.each do |e_sub|
                @trigger_event_map[e_sub[:details][:trigger]] = e[:id]
                will_provide << e[:id]
            end
            # pass the list of subscriptions back to the EventPublisher 
            will_provide
        end
    end 

    def send_events 
        # fire each event the given number of times 
        Thread.new do 
            @fire_n_events.times do 
                trigger_event_map.each do |trigger, event_id|
                    @e_publisher.fire(event_id)
                end
                sleep(0.1)
            end
        end 
    end 
end 
