class Metadata
  include MongoMapper::Document

  key :idNamespace, String, :required => true

  belongs_to :meta, :polymorphic => true

  set_collection_name 'metaData'

end
