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


require 'mongo'
require 'json'

NODES=20

connection = Mongo::Connection.new("nxmongo4.slidev.org", 27017)
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
  if millis/1000 >0
    secondsPerNode=millis/1000/NODES
    printf "\e[32m%-85s\e[0m: \e[35m%d\e[0m secs, \e[35m%s\e[0m secs/node\n",collectionName,millis/1000,secondsPerNode.to_s
    total+=millis
  end
end

puts "Total time: #{total/1000} secs (#{total/60000} mins)"
