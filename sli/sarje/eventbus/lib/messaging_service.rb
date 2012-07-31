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

require 'stomp'
require 'json'
require 'thread'

module Eventbus

  class MessagingService
    def initialize(config = {})
      @config = {
          :node_name => Socket.gethostname,
          :start_heartbeat => true,
          :start_node_detector => false
      }.merge(config)

      host = {:login => "", :passcode => "", :host => "localhost", :port => 61613, :ssl => false}
      if config[:subscribe_queue_name].start_with?('/topic/')
        host[:headers] = {'client-id' => config[:node_name]}
      end
      @config[:stomp_config] = {
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
          :parse_timeout => 5,
      }
      @publisher = Publisher.new(@config[:publish_queue_name], @config[:stomp_config])
      if @config[:start_heartbeat]
        start_heartbeat(@config[:node_name])
      end
      if @config[:start_node_detector]
        @heartbeat_queue = Queue.new
        @detected_nodes_lock = Mutex.new
        start_node_detector
      end
    end

    def publish(message)
      @publisher.publish(message)
    end

    def subscribe
      Subscriber.new(@config[:subscribe_queue_name], @config[:stomp_config]) do |message|
        if(message['event_type'] == 'heartbeat')
          @heartbeat_queue << message
        else
          yield message
        end
      end
    end

    def start_node_detector
      Thread.new do
        loop do
          sleep 60
          detected_nodes = Set.new
          begin
            loop do
              heartbeat_message = @heartbeat_queue.pop(true)
              detected_nodes << heartbeat_message['node_name']
            end
          rescue
            # no more oplog in oplog queue
          end
          set_detected_nodes(detected_nodes)
        end
      end
    end

    def get_detected_nodes
      if @config[:start_node_detector]
        detected_nodes = []
        @detected_nodes_lock.synchronize {
          detected_nodes = @detected_nodes
        }
        detected_nodes
      end
    end

    private

    def set_detected_nodes(detected_nodes)
      @detected_nodes_lock.synchronize {
        @detected_nodes = detected_nodes
      }
    end

    def start_heartbeat(node_name)
      Thread.new do
        loop do
          message = {
              'event_type' => 'heartbeat',
              'node_name' => node_name,
              'hostname' => Socket.gethostname,
              'timestamp' => Time.now.to_i.to_s
          }
          publish(message)
          sleep 5
        end
      end
    end
  end

  class Publisher
    def initialize(queue_name, config)
      @queue_name = queue_name
      @client = Stomp::Client.new(config)
    end

    def publish(message)
      @client.publish(@queue_name, message.to_json)
    end
  end

  class Subscriber
    def initialize(queue_name, config)
      Thread.new do
        if queue_name.start_with?('/topic/')
          client = Stomp::Connection.open(config)
          client.subscribe queue_name, {"activemq.subscriptionName" => config[:hosts][0][:headers]['client-id']}
          while true
            message = client.receive
            yield JSON.parse message.body
          end
        else
          client = Stomp::Client.new(config)
          client.subscribe queue_name do |message|
            yield JSON.parse message.body
          end
        end
        client.join
        client.close
      end
    end
  end
end

#agent_incoming = '/queue/agent'
#listener_incoming = '/queue/listener'
#
#agent_config = {
#    :node_name => 'agent',
#    :publish_queue_name => listener_incoming,
#    :subscribe_queue_name => agent_incoming
#}
#agent = Eventbus::MessagingService.new(agent_config) do |message|
#  puts "agent received: #{message}"
#end
#
#listener_config = {
#    :node_name => 'listener',
#    :publish_queue_name => agent_incoming,
#    :subscribe_queue_name => listener_incoming,
#    :start_heartbeat => false
#}
#listener = Eventbus::MessagingService.new(listener_config) do |message|
#  puts "listener received: #{message}"
#end
#
#Thread.new do
#  loop do
#    message = {
#        'event_type' => 'oplog event',
#        'hostname' => Socket.gethostname,
#        'timestamp' => Time.now.to_i.to_s
#    }
#    agent.publish(message)
#    sleep 5
#  end
#end
#
#Thread.new do
#  loop do
#    message = {
#        'event_type' => 'subscription event',
#        'hostname' => Socket.gethostname,
#        'timestamp' => Time.now.to_i.to_s
#    }
#    listener.publish(message)
#    sleep 5
#  end
#end
#sleep