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


class App < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  validates_presence_of [:description, :application_url, :name, :redirect_uri, :vendor], :message => "must not be blank"

  def self.all_but_admin
    apps = App.all
    apps.delete_if { |app| app.respond_to? :endpoints }
    apps
  end
  
  def pending?
    self.registration.status == "PENDING" ? true : false
  end
  
  def in_progress?
    if self.bootstrap
      return false
    end
    progress = true
    self.authorized_ed_orgs.each { |ed_org| progress = false if !ed_org.to_i != 0 }
    progress
  end
  
  schema do 
    string "client_secret", "redirect_uri", "description", "image_url"
    string "name", "client_id", "application_url", "administration_url"
    string "version", "behavior"
    boolean "is_admin", "license_acceptance", "installed", "bootstrap"
    time "created", "updated"
    string "authorized_ed_orgs", "vendor"

  end
  

  class Registration < SessionResource
    schema do
      string "status"
    end
  end
end


