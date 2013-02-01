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

connection = Mongo::Connection.new("nxmongo8.slidev.org", 27017)
db = connection.db("sli")
coll = db.collection("system.profile")

cursor = coll.find({"ns" => { "$nin" => ["sli.system.profile", "sli.system.namespaces", "sli.system.js"]}})

arr=[]
totals={}
cursor.each do |doc|
  arr<<doc["ns"] unless arr.include?(doc["ns"])
  
  totals[doc["ns"]].nil? ? totals[doc["ns"]]=doc["millis"] : totals[doc["ns"]]+=doc["millis"]   
      
end

puts arr.inspect
puts totals.inspect
