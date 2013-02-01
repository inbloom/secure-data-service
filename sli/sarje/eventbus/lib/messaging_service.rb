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

require 'stomp'
require 'json'
require 'thread'

module Eventbus

  class MessagingService
    def initialize(config = {}, logger = nil)
      @logger = logger
            
      @config = {
        :broker_url => "stomp://localhost:61613"
      }.merge(config)
    end

    def get_publisher(q_name)
      Publisher.new(q_name, @config[:broker_url])
    end

    def get_subscriber(q_name)
      Subscriber.new(q_name, @config[:broker_url])
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
        @client.autoflush = true
        @client.publish(@queue_name, message.to_json)
      rescue Exception => e
        @client = nil
        @logger.error("Problem publishing to queue #{@queue_name}: #{e}") unless @logger.nil?
      end
    end

    def close
      unless @client.nil?
        @client.close
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
        if @client.nil? 
          @client = Stomp::Client.new(@config) if @client.nil?
          @client.autoflush = true
        end 
        @client.subscribe(@queue_name) do |msg|
          yield JSON.parse msg.body
        end
      rescue Exception => e
        @logger.warn("problem occurred with subscribing: #{e}") unless @logger.nil?
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
