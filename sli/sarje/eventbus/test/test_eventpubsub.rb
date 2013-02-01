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

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"

require 'test/unit'
require 'eventbus'
require 'time'
require 'logger'

class TestEventPubSub < Test::Unit::TestCase
    FIRE_N_EVENTS = 5 
    EVENT_TYPE = "testevent"
    EVENT_QUEUE_1 = "eventqueue_1"
    EVENT_QUEUE_2 = "eventqueue_2"

    def setup
        @logger = Logger.new(STDOUT)
        @logger.level = Logger::WARN
 
        @queue_config = {}

        event_subs = [
            {"eventId" => "event_1", "details" => { "trigger" => "trigger_1" }},
            {"eventId" => "event_2", "details" => { "trigger" => "trigger_2" }},
            {"eventId" => "event_3", "details" => { "trigger" => "trigger_3" }},
            {"eventId" => "event_4", "details" => { "trigger" => "trigger_4" }},
            {"eventId" => "event_5", "details" => { "trigger" => "trigger_5" }}
        ]
        @event_subs_1 = event_subs.clone.map { |e| e.clone }
        @event_subs_1.each { |e| e["queue"] = EVENT_QUEUE_1 }

        @event_subs_2 = event_subs.clone.map { |e| e.clone }
        @event_subs_2.each { |e| e["queue"] = EVENT_QUEUE_2 }
        # @event_ids = @event_subscriptions.map { |e| e["eventId"] } 
    end 

    def test_eventpubsub
        # set up two agents and a subscriber 
        test_publisher_1 = TestAgent.new(FIRE_N_EVENTS, "agent_1", EVENT_TYPE, @logger)
        test_publisher_2 = TestAgent.new(FIRE_N_EVENTS, "agent_2", EVENT_TYPE, @logger)

        event_subscriber_1 = TestSubscriber.new(EVENT_TYPE, EVENT_QUEUE_1, @logger, @event_subs_1 + @event_subs_2)
        event_subscriber_2 = TestSubscriber.new(EVENT_TYPE, EVENT_QUEUE_2, @logger, nil)

        sleep 10 

        # # wait until I have publishers for all events 
        # agents_up = false 
        # while !agents_up
        #     all_publishers = event_subscriber.get_publishers
        #     agents_up = true 
        #     all_publishers.each do |node_id, events|
        #         agents_up &&= (Set.new(events).length == @event_subscriptions.length)
        #     end 
        #     #puts "AGENTS: #{all_publishers}\nAGENTS_UP: #{agents_up}\n----------------------------------"
        #     sleep(0.5)
        # end

        # fire the events for both agents and wait until they are done 
        event_subscriber_1.handle_events
        event_subscriber_2.handle_events 

        threads = []
        threads << test_publisher_1.send_events
        threads << test_publisher_2.send_events
        threads.each { |aThread| aThread.join }

        # waiting for all events to arrive 
        sleep(10)


        sent = test_publisher_1.get_sent_messages + test_publisher_2.get_sent_messages
        sent.sort!

        received = event_subscriber_1.get_handled_messages + event_subscriber_2.get_handled_messages
        received.sort!

        assert received == sent, "Received does not equal sent messages !"

        # assert event_subscriber_1.fire_count == (FIRE_N_EVENTS * 2), "Expected #{FIRE_N_EVENTS * 2} but got #{event_subscriber_1.fire_count}."
        # assert event_subscriber_2.fire_count == (FIRE_N_EVENTS * 2), "Expected #{FIRE_N_EVENTS * 2} but got #{event_subscriber_2.fire_count}."

        # # make sure that all the agents are online and they publish all events 
        # all_publishers = event_subscriber.get_publishers
        # assert all_publishers.length == 2, "Expected 2 event publishers, but got #{all_publishers.length}."
        # all_publishers.each do |node_id, events|
        #     assert events.sort == @event_ids.sort, "Publisher #{node_id} does not publish all events."  
        # end 

        # # make sure that each event was fired the correct number of times 
        # fired_events.each do |k,v| 
        #     assert v == FIRE_N_EVENTS * 2, "Event #{k} was not fired #{2 * FIRE_N_EVENTS} but #{v} times."
        # end
    end 
end 

class TestSubscriber 
    def initialize(event_type, event_queue, logger, event_subs)
        @event_subscriber = Eventbus::EventSubscriber.new({}, event_type, event_queue, logger)

        # set up the event handler 
        @fired_events = [] 

        @event_subscriber.handle_event do |event|
            eid = event['eventId']
            @fired_events << [eid, event_queue, event['data']]
        end

        # subscribe to the given list of events 
        @event_subscriber.observe_events(event_subs) if event_subs 
    end

    def print_subscriptions
        @event_subscriber.get_publishers().each do | pub |
            puts "SUBSCRIPTIONS: #{pub}"
        end 
    end 

    def handle_events
    end 

    def get_handled_messages 
        @fired_events
    end 
end 

class TestAgent 
    def initialize(fire_n_events, id, event_type, logger) 
        @fire_n_events = fire_n_events
        @sent_messages = []  
        @e_publisher = Eventbus::EventPublisher.new(id, event_type,{},logger)

        # setup the subscription handler on the publisher side 
        @tem_lock = Mutex.new 
        @trigger_events = []
        @e_publisher.handle_subscriptions do | event_subs |
            will_provide = [] 
            event_subs.each do |e_sub|
                @tem_lock.synchronize { 
                    @trigger_events << [e_sub['eventId'], e_sub['queue'], e_sub['details']]
                }
                will_provide << [e_sub['eventId'], e_sub['queue']]
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
                    @trigger_events.each do |event_id, queue, data|
                        payload = {
                            'eventId' => event_id, 
                            'data' => data 
                        }
                        msg = { queue => payload }
                        @e_publisher.fire_events(msg)
                        @sent_messages << [event_id, queue, data]
                    end
                }
                sleep(0.5)
            end
        end 
    end 

    def get_sent_messages
        @sent_messages 
    end 
end 
