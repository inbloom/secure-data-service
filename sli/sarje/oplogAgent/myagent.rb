# start the oplog agent

require 'mongo'
require 'stomp'
require 'json'
require 'time'

puts "Starting agent !"

hash = {
    :hosts => [
        {:login => "", :passcode => "", :host => "localhost", :port => 61613, :ssl => false}
    ],
    # These are the default parameters, don't need to be set
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

client = Stomp::Client.new(hash)

# This needs to be the replica sets - cannot talk to mongos?

loop do
  message = { 
    "eventid" => "99",
    "timestamp" => Time.now.to_i.to_s
  }.to_json

  puts "sending to queue #{message}"
  client.publish("/queue/oplog", message)
  sleep(5)
end
