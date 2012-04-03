class Metadata
  include MongoMapper::EmbeddedDocument

  key :idNamespace, String, :required => true

  belongs_to :meta, :polymorphic => true

  set_collection_name 'metaData'

end
