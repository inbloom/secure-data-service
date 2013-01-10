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

  validates_presence_of :fullName, :email

  #validates :email,:email=>true


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
    Time.parse(self.createTime)
  end

end
