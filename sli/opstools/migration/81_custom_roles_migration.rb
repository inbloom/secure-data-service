require 'mongo'

conn = Mongo::Connection.new()
databases = conn.database_names
databases.each do |dbName|
  db = conn.db(dbName)
  coll = db.collection('customRole')

  puts("Updating db #{dbName}")
  result = coll.update({'body.roles.groupTitle' => {'$ne' => 'Parent'}}, {'$push' => {'body.roles' => {
      :groupTitle => "Parent",
      :isAdminRole => false,
      :names => ['Parent'],
      :rights => ['READ_PUBLIC', 'READ_STUDENT_GENERAL'],
      :selfRights => ['READ_STUDENT_OWNED', 'READ_STUDENT_RESTRICTED'],
      :customRights => []
  }}
  }, {:multi => true})

  puts "Updated #{result['n']}/#{coll.count} with Parent role"

end

