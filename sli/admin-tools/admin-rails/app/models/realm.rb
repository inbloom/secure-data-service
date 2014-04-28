class Realm < SessionResource

  self.site = APP_CONFIG['api_base']
  self.collection_name = 'realm'
  validates_length_of :uniqueIdentifier, :name, :minimum => 5, :message => 'must be at least 5 characters long'
  validates_format_of :name, :uniqueIdentifier, :with => /^[a-zA-Z0-9\-_ ]*$/, :message => "can't contain special characters"
  validate :idp_and_redirect_cannot_be_blank
  validate :defaults_on_save

  def defaults_on_save
    self.admin = false
    #Default saml mapping
    self.saml =  { 'field' => [ 
      { 'clientName' => 'roles', 'sliName' => 'roles', 'transform' => '(.+)' },
      { 'clientName' => 'userId', 'sliName' => 'userId', 'transform' => '(.+)' },
      { 'clientName' => 'userType', 'sliName' => 'userType', 'transform' => '(.+)' }, 
      { 'clientName' => 'userName', 'sliName' => 'userName', 'transform' => '(.+)' } 
    ] } if saml.nil?
  end

  def idp_and_redirect_cannot_be_blank
    idp.valid?
  end

  def self.get_realm_to_redirect_to(user_realm)
    all.select {|realm| realm.edOrg && realm.edOrg == user_realm}
  end

  schema do
    string 'uniqueIdentifier', 'name', 'edOrg'
    string 'saml'
    string 'id', 'redirectEndpoint', 'artifactResolutionEndpoint', 'sourceId', 'idp'
  end

  class Idp < SessionResource
    validates_presence_of :id, :message => "can't be blank"
    validates_presence_of :redirectEndpoint, :message => "can't be blank"   
    validates_presence_of :artifactResolutionEndpoint, :unless => proc{|obj| obj.sourceId.blank?}, :message => "can't be blank if IDP Source ID is non-blank"
    validates_presence_of :sourceId, :unless => proc{|obj| obj.artifactResolutionEndpoint.blank?}, :message => "can't be blank if Artifact Resolution Endpoint is non-blank"
    validates_format_of :sourceId, :with => /^[a-fA-F0-9]*$/, :unless => proc{|obj| obj.sourceId.blank?}, :message => 'needs to be a hex-encoded string'
    validates_length_of :sourceId, :is => 40, :unless => proc{|obj| obj.sourceId.blank?}, :message => 'needs to be of length 40'
    schema do
      string 'id', 'redirectEndpoint', 'artifactResolutionEndpoint', 'sourceId'
    end
  end

end
