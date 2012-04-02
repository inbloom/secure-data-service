class Staff
  include MongoMapper::Document

  key :body, :as => :entity, :required => true
  key :type, String, :required => true
  one :metadata, :as => :meta, :required => true

  connection Mongo::Connection.new('localhost')
  set_database_name 'sli'
  set_collection_name 'staff'


end
