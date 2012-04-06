require 'rubygems'
require 'uuidtools'

class Teacher
  include MongoMapper::Document

  key :_id, BSON::ObjectId
  key :type, String

  has_one :body, :as => :entity
  has_one :metadata, :as => :meta

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
