class Teacher
  include MongoMapper::Document

  has_one :body, :as => :entity
  key :type, String, :required => true

  connection Mongo::Connection.new('localhost')
  set_database_name 'sli'
  set_collection_name 'teacher'

  def last_comma_first
      self.body.name.lastSurname + ", " + self.body.name.firstName + " (Educator)"
  end

  def staffUniqueStateId
    self.body.staffUniqueStateId
  end

end
