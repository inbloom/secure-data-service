#!/usr/bin/ruby
require 'mongo'

conn = Mongo::Connection.new()
databases = conn.database_names
databases.each do |dbName|
  db = conn.db(dbName)
  coll = db.collection('customRole')

  puts("Updating db #{dbName}")
  result = coll.update({'body.roles.groupTitle' => {'$ne' => 'Student'}}, {'$push' => {'body.roles' => {
      :groupTitle => "Student",
      :isAdminRole => false,
      :names => ['Student'],
      :rights => ['READ_PUBLIC', 'READ_STUDENT_GENERAL'],
      :selfRights => ['READ_STUDENT_OWNED'],
      :customRights => []
  }}
  }, {:multi => true})

  puts "Updated #{result['n']}/#{coll.count}"
end

