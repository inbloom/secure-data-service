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