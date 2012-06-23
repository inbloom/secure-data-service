#listener.rb

require 'stomp'

client = Stomp::Client.new("", "", "localhost", 61613)

client.subscribe "/queue/awesome" do |message|
  puts "received: #{message.body} on #{message.headers['destination']}"
end
client.join
client.close