#!/usr/bin/ruby
require 'mongo'

conn = Mongo::Connection.new()
databases = conn.database_names
databases.each do |dbName|
  db = conn.db(dbName)
  coll = db.collection('customRole')

  puts("Updating db #{dbName}")
  coll.update({}, {'$push' => {'body.roles' => {
      :groupTitle => "Student",
      :isAdminRole => false,
      :names => ['Student'],
      :rights => ['READ_PUBLIC', 'READ_NAME'],
      :selfRights => ['READ_GENERIC'],
      :customRights => []
  }}
  }, {:multi => true})
end

