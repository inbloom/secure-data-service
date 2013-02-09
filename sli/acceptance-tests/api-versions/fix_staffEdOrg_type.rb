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

db_name = ARGV[0]
puts "Moving 'type' out of 'metaData' for staffEducationOrganizationAssociation in #{db_name}"
conn = Mongo::Connection.new "localhost", "27017"
db = conn.db(db_name)
docs_to_fix = db.collection("staffEducationOrganizationAssociation").find({"type" => {"$exists"=>false}})
docs_to_fix.each do |doc|
  doc["type"] = "staffEducationOrganizationAssociation"
  db.collection("staffEducationOrganizationAssociation").update({"_id" => doc["_id"]}, doc)
end