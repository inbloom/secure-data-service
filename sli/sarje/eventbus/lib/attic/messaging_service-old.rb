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

  class MessagingServiceOld
    def initialize(config = {})
      @config = {
          :node_name => Socket.gethostname,
          :start_heartbeat => true,
          :start_node_detector => false,
          :messaging_host => "localhost",
          :messaging_port => 61613,
          :messaging_login => "",
          :messaging_passcode => "",
          :messaging_ssl => false,
          :heartbeat_period => 5,
          :heartbeat_detector_period => 10
      }.merge(config)

      host = {
          :login => @config[:messaging_login],
          :passcode => @config[:messaging_passcode],
          :host => @config[:messaging_host],
          :port => @config[:messaging_port],
          :ssl => @config[:messaging_ssl]
      }

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
          :parse_timeout => 5
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
      @subscriber ||= Subscriber.new(@config[:subscribe_queue_name], @config[:stomp_config]) do |message|
        if(@config[:start_node_detector] && message.instance_of?(Hash) && message['event_type'] == 'heartbeat')
          puts "#{@config[:node_name]} received a heartbeat from #{message['node_name']}"
          @heartbeat_queue << message
        else
          puts "#{@config[:node_name]} received a message: #{message}"
          yield message
        end
      end
    end

    def start_node_detector
      puts "starting node detector for node <#{@config[:node_name]}>"
      @node_detector_thread ||= Thread.new do
        loop do
          sleep @config[:heartbeat_detector_period]
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

    def shutdown
      if !@subscriber.nil?
        @subscriber.kill_subscription_listener
      end
      if !@node_detector_thread.nil?
        @node_detector_thread.kill
      end
      if !@heartbeat_thread.nil?
        @heartbeat_thread.kill
      end
    end

    private

    def set_detected_nodes(detected_nodes)
      @detected_nodes_lock.synchronize {
        @detected_nodes = detected_nodes
      }
    end

    def start_heartbeat(node_name)
      puts "starting heartbeat for node #{node_name}"
      @heartbeat_thread ||= Thread.new do
        loop do
          message = {
              'event_type' => 'heartbeat',
              'node_name' => node_name,
              'hostname' => Socket.gethostname,
              'timestamp' => Time.now.to_i.to_s
          }
          publish(message)
          sleep @config[:heartbeat_period]
        end
      end
    end
  end

  class Publisher
    def initialize(queue_name, config)
      @queue_name = queue_name
      @config = config
    end

    def publish(message)
      client = Stomp::Client.new(@config)
      client.publish(@queue_name, message.to_json)
    end
  end

  class Subscriber
    def initialize(queue_name, config)
      @queue_name = queue_name
      @thread ||= Thread.new do
        @client = nil
        if @queue_name.start_with?('/topic/')
          @header = {"activemq.subscriptionName" => config[:hosts][0][:headers]['client-id']}
          @client = Stomp::Connection.open(config)
          @client.subscribe @queue_name, @header
          puts "subscribing to topic #{@queue_name}"
          while true
            message = @client.receive
            yield JSON.parse message.body
          end
        else
          puts "subscribing to queue #{@queue_name}"
          @client = Stomp::Client.new(config)
          @client.subscribe @queue_name do |message|
            yield JSON.parse message.body
          end
        end
        @client.join
        @client.close
      end
    end

    def kill_subscription_listener
      if @client.is_a?(Stomp::Client)
        @client.unsubscribe @queue_name
        @client.close
      else
        @client.unsubscribe @queue_name, @header
        @client.disconnect
      end
    end
  end
end
