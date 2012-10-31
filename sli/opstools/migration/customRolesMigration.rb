#!/usr/bin/ruby
require 'mongo'

conn = Mongo::Connection.new()
databases = conn.database_names
databases.each do |dbName|
  puts("Connecting to db #{dbName}")
  db = conn.db(dbName)
  coll = db.collection('customRole')

  customRoleDocs = coll.find({})
  customRoleDocs.each do |doc|
    puts("\n---The doc is #{doc.inspect}")
    roles = doc["body"]["roles"]
    roles.each do |role|
      if role.has_key?("isAdminRole")
        next
      end
      if role["rights"].include?("ADMIN_APPS")
        role["isAdminRole"] = true
        role["rights"].delete("ADMIN_APPS")
      else
        role["isAdminRole"] = false
      end
      puts("The role is #{role.inspect}")
    end
    coll.remove({"_id" => doc["_id"]})
    puts("The doc we're about to insert is #{doc.inspect}")
    coll.insert(doc)
  end
end
