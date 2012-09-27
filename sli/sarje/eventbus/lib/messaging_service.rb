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
    def initialize(config = {}, logger = nil)
      @logger = logger
      @config = {
          :messaging_host => "localhost",
          :messaging_port => 61613,
          :messaging_login => "",
          :messaging_passcode => "",
          :messaging_ssl => false,
      }.merge(config)

      @host = {
          :login => @config[:messaging_login],
          :passcode => @config[:messaging_passcode],
          :host => @config[:messaging_host],
          :port => @config[:messaging_port],
          :ssl => @config[:messaging_ssl]
      }

      # XXX: hardcoded configuration for ActiveMQ/Stomp
      # we might want to make this configurable eventually 
      @config[:stomp_config] = {
          :hosts => [@host],
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
    end

    def get_publisher(q_name)
      Publisher.new(q_name, @config[:stomp_config])
    end

    def get_subscriber(q_name)
      Subscriber.new(q_name, @config[:stomp_config])
    end
  end

  class Publisher
    def initialize(queue_name, config, logger = nil)
      @queue_name = queue_name
      @config = config
      @logger = logger
    end

    def publish(message)
      begin
        @client = Stomp::Client.new(@config) if @client.nil?
        @client.publish(@queue_name, message.to_json)
      rescue Exception => e
        @client = nil
        @logger.warn("problem publishing to queue #{@queue_name}: #{e}") unless @logger.nil?
      end
    end
  end

  class Subscriber
    def initialize(queue_name, config, logger = nil)
      # NOTE: Topics are considered realtime notifications that will be repeated over
      # time, while as queues are durable and follow the producer/consumer model. 
      # We will not include the client id in the header of the message queue but 
      # instead add it on the applicatio level 
          # @header = {"activemq.subscriptionName" => config[:hosts][0][:headers]['client-id']}
          # @client = Stomp::Connection.open(config)
          # @client.subscribe @queue_name, @header

      @queue_name = queue_name
      @config = config
      @logger = logger
      # @is_topic = @queue_name.start_with?('/topic/')
      #@client = Stomp::Client.new(config)
    end

    def handle_message
      begin
        @client = Stomp::Client.new(@config) if @client.nil?
        @client.subscribe(@queue_name) do |msg|
          yield JSON.parse msg.body
        end
      rescue Exception => e
        @logger.warn("problem occurred with subscribing: #{e}")
        close()
      end
    end

    def close
      unless @client.nil?
        if @client.is_a?(Stomp::Client)
          @client.unsubscribe @queue_name
          @client.close
        else
          @client.unsubscribe @queue_name, @header
          @client.disconnect
        end
        @client = nil
      end
    end
  end
end
