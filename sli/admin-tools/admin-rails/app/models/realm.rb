class Realm < SessionResource

  self.site = "#{APP_CONFIG['api_base']}"
  self.collection_name = "realm"
  validates_length_of :uniqueIdentifier, :minimum => 5, :message => "must be at least 5 characters long"
  validates_presence_of :name, :message => "can't be blank"
  validate :idp_and_redirect_cannot_be_blank
   
  def idp_and_redirect_cannot_be_blank
    idp.valid?
  end
  
  schema do
    string "uniqueIdentifier", "name", "edOrg"
    string "saml", "mappings"
    string "id", "redirectEndpoint", "idp"
  end

  class Idp < SessionResource
    validates_presence_of :id, :message => "can't be blank"
    validates_presence_of :redirectEndpoint, :message => "can't be blank"
      schema do
        string "id", "redirectEndpoint"
      end
    end
end
