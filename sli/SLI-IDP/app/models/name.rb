class Name
  include MongoMapper::Document

  key :firstName, String, :required => true
  key :lastSurname, String, :required => true

  belongs_to :body

end
