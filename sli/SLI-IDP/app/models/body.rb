class Body
  include MongoMapper::Document

  one :name, :required => true
  key :staffUniqueStateId, String, :required => true

  belongs_to :entity, :polymorphic => true

end
