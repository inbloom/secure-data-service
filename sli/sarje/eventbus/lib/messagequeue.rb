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

  class Agent
    def initialize(queue_name)
      @queue_name = queue_name
      @client = Stomp::Client.new(HASH)
    end

    def publish(message)
      @client.publish(@queue_name, message)
    end
  end

  class Listener
    def initialize(queue_name)
      @client = Stomp::Client.new(HASH)
      @client.subscribe queue_name do |message|
        yield JSON.parse message.body
      end
    end

    def merge
      @client.join
      @client.close
    end
  end
end

queue_name = '/queue/oplog'
agent_queue = Queue.new

agent = Eventbus::Agent.new queue_name
Thread.new do
  loop do
    message = agent_queue.pop
    puts "publishing message #{message}"
    agent.publish message
  end
end

Thread.new do
  loop do
    message = {
        'event_type' => 'heartbeat',
        'agent_id' => 'agent 1',
        'hostname' => Socket.gethostname,
        'timestamp' => Time.now.to_i.to_s
    }.to_json
    agent_queue << message
    sleep 5
  end
end

listener_queue = Queue.new
listener = Eventbus::Listener.new queue_name do |message|
  listener_queue << message
end

Thread.new do
  loop do
    message = listener_queue.pop
    if message["event_type"] == "heartbeat"
      puts "received heartbeat from host = #{message['hostname']}, agent id = #{message['agent_id']}"
    else
      puts "received regular message: #{message}"
    end
  end
end

listener.merge
