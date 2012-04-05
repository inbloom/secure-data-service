class Body
  include MongoMapper::EmbeddedDocument

  has_one :name
  key :staffUniqueStateId, String

  embedded_in :entity

end
