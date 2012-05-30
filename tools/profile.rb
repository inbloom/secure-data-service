require 'mongo'
require 'json'

NODES=50

connection = Mongo::Connection.new("nxmongo3.slidev.org", 27017)
db = connection.db("is")
coll = db.collection("system.profile")

recs = coll.find()

aggregate={}
recs.each do |record|
  if aggregate[record["ns"]].nil?
    aggregate[record["ns"]]=0;
  end
  
  aggregate[record["ns"]]+=record["millis"]
end

total=0
aggregate=Hash[aggregate.sort {|a,b| b[1]<=>a[1]}]
aggregate.each do |collectionName,millis|
  secondsPerNode="N/A"
  if millis/60000 >0
    secondsPerNode=millis/1000/NODES
  end
  printf "\e[32m%-85s\e[0m: \e[35m%d\e[0m secs, \e[35m%s\e[0m secs/node\n",collectionName,millis/1000,secondsPerNode.to_s
  total+=millis
end

puts "Total time: #{total/1000} secs (#{total/60000} mins)"
