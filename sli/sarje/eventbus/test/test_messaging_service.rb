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

require 'eventbus'
require 'test/unit'

class TestMessagingService < Test::Unit::TestCase
  def setup
  end

  def clear_queue(queue_name, node_name = nil)
    host = {
        :login => "",
        :passcode => "",
        :host => "localhost",
        :port => 61613,
        :ssl => false
    }
    if !node_name.nil?
      host[:headers] = {"client_id" => node_name}
    end

    stomp_config = {
        :hosts => [host],
        :initial_reconnect_delay => 0.01,
        :max_reconnect_delay => 30.0,
        :use_exponential_back_off => true,
        :back_off_multiplier => 2,
        :max_reconnect_attempts => 0,
        :randomize => false,
        :backup => false,
        :timeout => -1,
        :connect_headers => {},
        :parse_timeout => 5
    }

    subscriber = Eventbus::Subscriber.new queue_name, stomp_config do |message|
      puts "cleared a message"
    end

    sleep 3 #wait for subscriber to get everything

    subscriber.kill_subscription_listener
  end

  # Note: you must have MQ service running locally for these tests to work


  def test_messaging
    puts "starting test_messaging"
    agent_incoming = '/topic/agent'
    listener_incoming = '/queue/listener'

    clear_queue(agent_incoming, 'agent')
    clear_queue(listener_incoming)

    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :start_heartbeat => false
    }
    agent_received_messages = []
    agent = Eventbus::MessagingService.new(agent_config)
    agent.subscribe do |message|
      if message['test'] == 'test_messaging'
        agent_received_messages << message
      else
        puts "received some bogus message"
      end
    end

    listener_config = {
        :node_name => 'listener',
        :publish_queue_name => agent_incoming,
        :subscribe_queue_name => listener_incoming,
        :start_heartbeat => false
    }
    listener_received_messages = []
    listener = Eventbus::MessagingService.new(listener_config)
    listener.subscribe do |message|
      if message['test'] == 'test_messaging'
        listener_received_messages << message
      else
        puts "received some bogus message"
      end
    end

    # wait for the subscription to start
    sleep 3

    oplog_event_message_count = 5
    subscription_event_message_count = 10
    oplog_event_message_count.times do
      message = {
          'event_type' => 'oplog event',
          'test' => 'test_messaging',
          'hostname' => Socket.gethostname,
          'timestamp' => Time.now.to_i.to_s
      }
      agent.publish(message)
    end

    subscription_event_message_count.times do
      message = {
          'event_type' => 'subscription event',
          'test' => 'test_messaging',
          'hostname' => Socket.gethostname,
          'timestamp' => Time.now.to_i.to_s
      }
      listener.publish(message)
    end

    # wait for threads to finish receiving messages
    sleep 3

    assert_equal(oplog_event_message_count, listener_received_messages.size)
    assert_equal(subscription_event_message_count, agent_received_messages.size)

    agent.shutdown
    listener.shutdown
    puts "finished test_messaging"
  end

  # test that everyone who subscribes to a topic will get the messages from the publisher
  def test_topic
    puts "starting test_topic"
    agent_incoming = '/topic/agent'
    listener_incoming = '/queue/listener'

    clear_queue(agent_incoming, 'agent')
    clear_queue(agent_incoming, 'agent2')
    clear_queue(listener_incoming)

    # create the 2 agents that will be receiving topic posts from listener
    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming
    }
    agent_received_messages = []
    agent = Eventbus::MessagingService.new(agent_config)
    agent.subscribe do |message|
      if message['test'] == 'test_topic'
        agent_received_messages << message
      else
        puts "agent received some bogus message"
      end
    end

    agent2_config = {
        :node_name => 'agent2',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming
    }
    agent2_received_messages = []
    agent2 = Eventbus::MessagingService.new(agent2_config)
    agent2.subscribe do |message|
      if message['test'] == 'test_topic'
        agent2_received_messages << message
      else
        puts "agent2 received some bogus message"
      end
    end

    # create the listener
    listener_config = {
        :node_name => 'listener',
        :publish_queue_name => agent_incoming,
        :subscribe_queue_name => listener_incoming
    }
    listener_received_messages = []
    listener = Eventbus::MessagingService.new(listener_config)
    listener.subscribe do |message|
      listener_received_messages << message
    end

    # wait for the agents and listeners to load completely
    sleep 3

    assert_equal(0, agent_received_messages.size)
    assert_equal(0, agent2_received_messages.size)

    subscription_event_message_count = 10
    subscription_event_message_count.times do
      message = {
          'event_type' => 'subscription event',
          'test' => 'test_topic',
          'hostname' => Socket.gethostname,
          'timestamp' => Time.now.to_i.to_s
      }
      listener.publish(message)
    end

    # should complete in 3 seconds or upgrade your machine
    sleep 3

    # check that both agents get all the topic posts
    assert_equal(subscription_event_message_count, agent_received_messages.size)
    assert_equal(subscription_event_message_count, agent2_received_messages.size)

    agent.shutdown
    agent2.shutdown
    listener.shutdown
    puts "finished test_topic"
  end

  def test_queue
    puts "starting test_queue"
    agent_incoming = '/queue/agent'
    listener_incoming = '/queue/listener'

    clear_queue(agent_incoming)
    clear_queue(listener_incoming)

    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :start_heartbeat => false
    }
    agent_received_messages = []
    agent = Eventbus::MessagingService.new(agent_config)
    agent.subscribe do |message|
      if message['test'] == 'test_queue'
        agent_received_messages << message
      else
        puts "received some bogus message"
      end
    end

    agent2_config = {
        :node_name => 'agent2',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :start_heartbeat => false
    }
    agent2_received_messages = []
    agent2 = Eventbus::MessagingService.new(agent2_config)
    agent2.subscribe do |message|
      if message['test'] == 'test_queue'
        agent2_received_messages << message
      else
        puts "received some bogus message"
      end
      sleep 1 # fake processing
    end

    listener_config = {
        :node_name => 'listener',
        :publish_queue_name => agent_incoming,
        :subscribe_queue_name => listener_incoming,
        :start_heartbeat => false
    }
    listener_received_messages = []
    listener = Eventbus::MessagingService.new(listener_config)
    listener.subscribe do |message|
      listener_received_messages << message
      sleep 1 # fake processing
    end

    # wait for the agents and listeners to load and start subscribing to queues
    sleep 3

    subscription_event_message_count = 10
    subscription_event_message_count.times do
      message = {
          'event_type' => 'subscription event',
          'test' => 'test_queue',
          'hostname' => Socket.gethostname,
          'timestamp' => Time.now.to_i.to_s
      }
      listener.publish(message)
    end

    # should complete in 3 seconds or upgrade your machine
    sleep (subscription_event_message_count * 2)

    agent.shutdown
    agent2.shutdown
    listener.shutdown

    # both agents should be receiving messages because we are faking processing
    assert(agent_received_messages.size > 0)
    assert(agent2_received_messages.size > 0)
    assert_equal(subscription_event_message_count, agent_received_messages.size + agent2_received_messages.size)
    puts "finished test_queue"
  end

  def test_heartbeat
    puts "starting test_heartbeat"
    agent_incoming = '/topic/agent'
    listener_incoming = '/queue/listener'

    listener_config = {
        :node_name => 'listener',
        :publish_queue_name => agent_incoming,
        :subscribe_queue_name => listener_incoming,
        :start_heartbeat => false,
        :start_node_detector => true,
        :heartbeat_detector_period => 2
    }
    listener_received_messages = []
    listener = Eventbus::MessagingService.new(listener_config)
    listener.subscribe do |message|
      listener_received_messages << message
    end

    # test that no heartbeat is received by listener before agent starts
    2.times do
      sleep 2
      assert_equal(0, listener.get_detected_nodes.to_a.size)
    end

    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :heartbeat_period => 1
    }
    agent_received_messages = []
    agent = Eventbus::MessagingService.new(agent_config)
    agent.subscribe do |message|
      agent_received_messages << message
    end

    agent2_config = {
        :node_name => 'agent2',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :heartbeat_period => 1
    }
    agent2_received_messages = []
    agent2 = Eventbus::MessagingService.new(agent2_config)
    agent2.subscribe do |message|
      agent2_received_messages << message
    end

    # check that both agents are detected by the listener after 5 seconds
    2.times do
      sleep 5
      assert_equal(2, listener.get_detected_nodes.size)
    end

    agent.shutdown
    agent2.shutdown
    listener.shutdown
    puts "finished test_heartbeat"
  end
end