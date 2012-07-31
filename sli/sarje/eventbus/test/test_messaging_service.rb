testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"

require 'eventbus'
require 'test/unit'

class TestMessagingService < Test::Unit::TestCase
  def setup
  end

  # Note: you must have MQ service running locally for these tests to work


  def test_messaging
    agent_incoming = '/topic/agent'
    listener_incoming = '/queue/listener'

    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :start_heartbeat => false
    }
    agent_received_messages = []
    agent = Eventbus::MessagingService.new(agent_config)
    agent.subscribe do |message|
      agent_received_messages << message
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
    end

    sleep 3
    oplog_event_message_count = 5
    subscription_event_message_count = 10
    Thread.new do
      counter = 0
      oplog_event_message_count.times do
        counter = counter + 1
        message = {
            'event_type' => 'oplog event',
            'hostname' => Socket.gethostname,
            'timestamp' => Time.now.to_i.to_s
        }
        agent.publish(message)
      end
    end

    Thread.new do
      subscription_event_message_count.times do
        message = {
            'event_type' => 'subscription event',
            'hostname' => Socket.gethostname,
            'timestamp' => Time.now.to_i.to_s
        }
        listener.publish(message)
      end
    end

    # should complete in 3 seconds or upgrade your machine
    sleep 3

    assert_equal(oplog_event_message_count, listener_received_messages.size)
    assert_equal(subscription_event_message_count, agent_received_messages.size)
  end

  # test that everyone who subscribes to a topic will get the messages from the publisher
  def test_topic
    agent_incoming = '/topic/agent'
    listener_incoming = '/queue/listener'

    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :start_heartbeat => false
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
        :start_heartbeat => false
    }
    agent2_received_messages = []
    agent2 = Eventbus::MessagingService.new(agent2_config)
    agent2.subscribe do |message|
      agent2_received_messages << message
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
    end

    sleep 3

    subscription_event_message_count = 10
    Thread.new do
      subscription_event_message_count.times do
        message = {
            'event_type' => 'subscription event',
            'hostname' => Socket.gethostname,
            'timestamp' => Time.now.to_i.to_s
        }
        listener.publish(message)
      end
    end

    # should complete in 3 seconds or upgrade your machine
    sleep 3

    assert_equal(subscription_event_message_count, agent_received_messages.size)
    assert_equal(subscription_event_message_count, agent2_received_messages.size)
  end

  def test_queue
    agent_incoming = '/queue/agent'
    listener_incoming = '/queue/listener'

    agent_config = {
        :node_name => 'agent',
        :publish_queue_name => listener_incoming,
        :subscribe_queue_name => agent_incoming,
        :start_heartbeat => false
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
        :start_heartbeat => false
    }
    agent2_received_messages = []
    agent2 = Eventbus::MessagingService.new(agent2_config)
    agent2.subscribe do |message|
      agent2_received_messages << message
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
    end

    sleep 3

    subscription_event_message_count = 10
    Thread.new do
      subscription_event_message_count.times do
        message = {
            'event_type' => 'subscription event',
            'hostname' => Socket.gethostname,
            'timestamp' => Time.now.to_i.to_s
        }
        listener.publish(message)
      end
    end

    # should complete in 3 seconds or upgrade your machine
    sleep 3

    assert_equal(subscription_event_message_count, agent_received_messages.size + agent2_received_messages.size)
  end
end