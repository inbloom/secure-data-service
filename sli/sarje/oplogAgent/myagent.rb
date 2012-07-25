# start the oplog agent

require 'mongo'
require 'stomp'
require 'json'
require 'time'

puts "Starting agent !"

client = Stomp::Client.new("", "", "localhost", 61613)

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
