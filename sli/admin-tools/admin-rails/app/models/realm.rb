=begin

Copyright 2012 Shared Learning Collaborative, LLC

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
