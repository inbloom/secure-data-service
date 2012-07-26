require 'stomp'
require 'json'
require 'thread'

module Eventbus

  HASH = {
      :hosts => [
          {:login => "", :passcode => "", :host => "localhost", :port => 61613, :ssl => false}
      ],
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

  class Publisher
    def initialize(queue_name)
      @queue_name = queue_name
      @client = Stomp::Client.new(HASH)
    end

    def publish(message)
      @client.publish(@queue_name, message)
    end
  end

  class Subscriber
    def initialize(queue_name)
      Thread.new do
        @client = Stomp::Client.new(HASH)
        @client.subscribe queue_name do |message|
          yield JSON.parse message.body
        end
        @client.join
        @client.close
      end
    end
  end
end

queue_name = '/queue/oplog'

subscriber_queue = Queue.new
subscriber = Eventbus::Subscriber.new queue_name do |message|
  subscriber_queue << message
end

Thread.new do
  loop do
    message = subscriber_queue.pop
    if message["event_type"] == "heartbeat"
      puts "received heartbeat #{message}"
    else
      puts "received regular message: #{message}"
    end
  end
end

sleep 2
publisher_queue = Queue.new
publisher = Eventbus::Publisher.new queue_name
Thread.new do
  loop do
    message = publisher_queue.pop
    puts "publishing message #{message}"
    publisher.publish message
  end
end

Thread.new do
  counter = 0
  loop do
    counter = counter + 1
    message = {
        'event_type' => 'heartbeat',
        'event_id' => counter,
        'agent_id' => 'agent 1',
        'hostname' => Socket.gethostname,
        'timestamp' => Time.now.to_i.to_s
    }.to_json
    publisher_queue << message
    sleep 5
  end
end

sleep
