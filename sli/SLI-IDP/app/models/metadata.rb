class Metadata
  include MongoMapper::EmbeddedDocument

  key :idNamespace, String

  embedded_in :meta

end
