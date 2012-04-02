class Teacher
  include MongoMapper::Document

  key :type, String, :required => true
  key :tenantId, String, :required => true
  key :body, :as => :entity, :required => true

  connection Mongo::Connection.new('localhost')
  set_database_name 'sli'
  set_collection_name 'teacher'

end
