class Name
  include MongoMapper::EmbeddedDocument

  key :firstName, String
  key :lastSurname, String

  embedded_in :body

end
