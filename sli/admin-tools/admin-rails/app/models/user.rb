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

class User < SessionResource
  self.collection_name = "users"
  include ActiveModel::Validations

  validates_presence_of :email
  validates :fullName, :presence => true, :length => { :maximum => 128 }
  validates :tenant, :length => { :maximum => 160 }, :format => { :with => /^[-_a-zA-Z0-9]+[-_.@a-zA-Z0-9]*$/, :message => "should not contains spaces or special characters" }, :unless => "tenant.blank?"
  validates :edorg, :length => { :maximum => 255 }, :unless => "edorg.blank?"

  schema do
    string  "uid"
    string  "email"
    string  "tenant"
    string  "edorg"
    time "createTime", "modifyTime"
    string  "groups"
    string  "fullName"
    string  "sn"
    string  "givenName"
    string  "primary_role"
    string  "optional_role_1"
    string  "optional_role_2"
    string   "homeDir"
  end

  def get_groups
    self.groups.sort!.join(", ")
  end

  def get_create_time
    Time.parse(self.createTime + "UTC")
  end

end
