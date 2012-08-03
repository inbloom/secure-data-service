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
        @queue_config = {}

        @event_subscriptions = [
            {"id" => "event_1", "details" => { "trigger" => "trigger_1" }},
            {"id" => "event_2", "details" => { "trigger" => "trigger_2" }},
            {"id" => "event_3", "details" => { "trigger" => "trigger_3" }},
            {"id" => "event_4", "details" => { "trigger" => "trigger_4" }},
            {"id" => "event_5", "details" => { "trigger" => "trigger_5" }}
        ]
        @event_ids = @event_subscriptions.map { |e| e['id'] } 
    end 

    def test_eventpubsub
        # set up two agents and a subscriber 
        test_publisher_1 = TestAgent.new(FIRE_N_EVENTS, "agent_1", EVENT_TYPE)
        test_publisher_2 = TestAgent.new(FIRE_N_EVENTS, "agent_2", EVENT_TYPE)
        event_subscriber = Eventbus::EventSubscriber.new(EVENT_TYPE)

        # set up the event handler 
        fired_events = {} 
        fired_events.default=(0)
        event_subscriber.handle_event do |event|
            eid = event['id']
            fired_events[eid] = fired_events[eid] + 1
        end

        # subscribe to the given list of events 
        event_subscriber.subscribe(@event_subscriptions)

        # wait until I have publishers for all events 
        agents_up = false 
        while !agents_up
            all_publishers = event_subscriber.get_publishers
            agents_up = true 
            all_publishers.each do |node_id, events|
                agents_up &&= (Set.new(events).length == @event_subscriptions.length)
            end 
            #puts "AGENTS: #{all_publishers}\nAGENTS_UP: #{agents_up}\n----------------------------------"
            sleep(0.5)
        end

        # fire the events for both agents and wait until they are done 
        threads = []
        threads << test_publisher_1.send_events
        threads << test_publisher_2.send_events
        threads.each { |aThread| aThread.join }

        # waiting for all events to arrive 
        sleep(5)

        # make sure that all the agents are online and they publish all events 
        all_publishers = event_subscriber.get_publishers
        assert all_publishers.length == 2, "Expected 2 event publishers, but got #{all_publishers.length}."
        all_publishers.each do |node_id, events|
            assert events.sort == @event_ids.sort, "Publisher #{node_id} does not publish all events."  
        end 

        # make sure that each event was fired the correct number of times 
        fired_events.each do |k,v| 
            assert v == FIRE_N_EVENTS * 2, "Event #{k} was not fired #{2 * FIRE_N_EVENTS} but #{v} times."
        end
    end 
end 

class TestAgent 
    def initialize(fire_n_events, id, event_type) 
        @fire_n_events = fire_n_events 
        @e_publisher = Eventbus::EventPublisher.new(id, event_type)

        # setup the subscription handler on the publisher side 
        @tem_lock = Mutex.new 
        @trigger_event_map = {}
        @e_publisher.handle_subscriptions do | event_subs |

            will_provide = [] 
            event_subs.each do |e_sub|
                @tem_lock.synchronize { 
                    @trigger_event_map[e_sub['details']['trigger']] = e_sub['id']
                }
                will_provide << e_sub['id']
            end
            # pass the list of subscriptions back to the EventPublisher 
            will_provide
        end
    end 

    def send_events 
        # fire each event the given number of times 
        Thread.new do 
            @fire_n_events.times do 
                @tem_lock.synchronize { 
                    @trigger_event_map.each do |trigger, event_id|
                        msg = {
                            'id' => event_id, 
                            'data' => trigger 
                        }
                        @e_publisher.fire_event(msg)
                    end
                }
                sleep(0.5)
            end
        end 
    end 
end 
