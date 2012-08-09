# start the oplog agent

require 'mongo'
require 'stomp'
require 'json'

puts "starting oplog agent"

client = Stomp::Client.open("stomp://localhost", 61613)
# This needs to be the replica sets - cannot talk to mongos?

db = Mongo::Connection.new("localhost",27017).db("local")
coll = db['oplog.rs']

loop do
  cursor = Mongo::Cursor.new(coll, :timeout => false, :tailable => true)
  while not cursor.closed?
    begin
      if doc = cursor.next_document
        puts "document = #{doc}"

        puts "Record"
        puts  doc["o"]

        message = { "id" => doc["o"]["_id"],
          "collection" =>  doc["ns"],
          "operation" => doc["op"],
          "timestamp" =>   doc["ts"]
        }

        if ['sli.staff', 'sli.assessment'].include?(message["collection"])
          puts "writing to queue #{message}"
          client.publish("/queue/oplog", message.to_json)
        else
          puts "ignoring message from collection #{message['collection']}"
        end
      end
    rescue
      puts "Some error happened - this is not unexpected from a tailable cursor"
      sleep 1
    break
    end
  end
end

class FakeMessageCreator
  def initialize
    @id_counter = 0
  end

  def create
    sleep 1
    message = {
        "id" => @id_counter,
        "collection" => "joe's legendary collection",
        "operation" => "covert"
    }.to_json

    @id_counter = @id_counter + 1
    message
  end
end
