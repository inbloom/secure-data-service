# start the oplog agent

require 'mongo'
require 'stomp'
require 'json'

puts "starting oplog agent"

client = Stomp::Client.new("", "", "localhost", 61613)

# This needs to be the replica sets - cannot talk to mongos?

db = Mongo::Connection.new("nxmongo10.slidev.org",27017).db("local")
coll = db.collection('oplog.rs')
loop do
  cursor = Mongo::Cursor.new(coll, :timeout => false, :tailable => true)
  while not cursor.closed?
    begin
      if doc = cursor.next_document

        puts "Record"
        puts  doc["o"]

        message = { "id" => doc["o"]["_id"],
          "collection" =>  doc["ns"] ,
          "operation" => doc["op"] ,
          "timestamp" =>   doc["ts"]
        }.to_json

        puts "sending to queue #{message}"
        client.publish("/queue/oplog", message)

      end
    rescue
      puts "Some error happened - this is not unexpected from a tailable cursor"
    break
    end
  end
end
