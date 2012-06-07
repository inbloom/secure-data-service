class Realm < SessionResource

  self.site = "#{APP_CONFIG['api_base']}"
  self.collection_name = "realm"
  validates_length_of :uniqueIdentifier, :minimum => 5, :message => "must be at least 5 characters long"
  validates_presence_of :name, :message => "can't be blank"
  validate :idp_and_redirect_cannot_be_blank
  validate :defaults_on_save

  def defaults_on_save
    if mappings.nil?
      self.mappings = {}
      self.mappings["role"] = [{"sliRoleName" => "IT Administrator", "clientRoleName" => [ "IT Administrator" ] }, {"sliRoleName" => "Educator", "clientRoleName" => [ "Educator" ] }, {"sliRoleName" => "Aggregate Viewer", "clientRoleName" => [ "Aggregate Viewer" ] }, {"sliRoleName" => "Leader", "clientRoleName" => [ "Leader" ] }]
    end
    self.admin = false
    #Default saml mapping
    self.saml =  { "field" => [ { "clientName" => "roles", "sliName" => "roles", "transform" => "(.+)" }, { "clientName" => "userId", "sliName" => "userId", "transform" => "(.+)" }, { "clientName" => "userName", "sliName" => "userName", "transform" => "(.+)" } ] } if saml.nil?
  end
   
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
