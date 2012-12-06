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
    puts("\n---The original doc is #{doc.inspect}")
    roles = doc["body"]["roles"]
    roles.each do |role|
      if role["groupTitle"] == "IT Administrator"
        role["isAdminRole"] = true
        role["rights"].delete("ADMIN_APPS") #This line probably won't do anything but whatever
      else
        role["isAdminRole"] = false
      end
      puts("The updated role is #{role.inspect}")
    end
    coll.remove({"_id" => doc["_id"]})
    puts("The doc we're about to insert is #{doc.inspect}")
    coll.insert(doc)
  end
end
