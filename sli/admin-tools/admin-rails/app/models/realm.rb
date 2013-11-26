=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


class Realm < SessionResource

  self.site = "#{APP_CONFIG['api_base']}"
  self.collection_name = "realm"
  validates_length_of :uniqueIdentifier, :name, :minimum => 5, :message => "must be at least 5 characters long"
  validates_presence_of :name, :message => "can't be blank"
  validates_format_of :name, :uniqueIdentifier, :with => /^[a-zA-Z0-9\-_ ]*$/, :message => "cannot contain special characters"
  validate :idp_and_redirect_cannot_be_blank
  validate :defaults_on_save

  def defaults_on_save
    self.admin = false
    #Default saml mapping
    self.saml =  { "field" => [ 
      { "clientName" => "roles", "sliName" => "roles", "transform" => "(.+)" },
      { "clientName" => "userId", "sliName" => "userId", "transform" => "(.+)" },
      { "clientName" => "userType", "sliName" => "userType", "transform" => "(.+)" }, 
      { "clientName" => "userName", "sliName" => "userName", "transform" => "(.+)" } 
    ] } if saml.nil?
  end

  def idp_and_redirect_cannot_be_blank
    idp.valid?
  end

  schema do
    string "uniqueIdentifier", "name", "edOrg"
    string "saml"
    string "id", "redirectEndpoint", "artifactResolutionEndpoint", "sourceId", "idp"
  end

  class Idp < SessionResource
    validates_presence_of :id, :message => "can't be blank"
    validates_presence_of :redirectEndpoint, :message => "can't be blank"   
    validates_presence_of :artifactResolutionEndpoint, :unless => proc{|obj| obj.sourceId.blank?}, :message => "can't be blank if sourceId is non-blank"
    validates_presence_of :sourceId, :unless => proc{|obj| obj.artifactResolutionEndpoint.blank?}, :message => "can't be blank if artifactResolutionEndpoint is non-blank"
    validates_format_of :sourceId, :with => /^[a-fA-F0-9]*$/, :unless => proc{|obj| obj.sourceId.blank?}, :message => "needs to be a hex-encoded string"
    validates_length_of :sourceId, :is => 40, :unless => proc{|obj| obj.sourceId.blank?}, :message => "needs to be of length 40"
    schema do
      string "id", "redirectEndpoint", "artifactResolutionEndpoint", "sourceId"
    end
  end

end
