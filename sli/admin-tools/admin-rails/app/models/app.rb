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


class App < SessionResource
  self.format = ActiveResource::Formats::JsonFormat
  validates_presence_of [:description, :name, :vendor], :message => "must not be blank"
  validates_format_of :version, :with => /^[A-Za-z0-9\.]{1,25}$/, :message => "must contain only alphanumeric characters and periods and be less than 25 characters long"
  validates_each :administration_url, :image_url do |record, attr, value|
    logger.debug {"Validating #{attr} => #{value}"}
    record.errors.add(attr, "must be a valid url (starting with http:// or https://)") if !value.nil? and (value =~ /^http(s)*:\/\/.+$/).nil?
  end

  validates_format_of [:application_url, :redirect_uri], :with => /^http(s)*:\/\/.+$/, :message => "must be a valid url (starting with http:// or https://)", :if => :not_installed
  validates_presence_of [:application_url, :redirect_uri], :message => "must not be blank", :if => :not_installed

  def not_installed
    not installed
  end

  def pending?
    self.registration.status == "PENDING" ? true : false
  end

  def in_progress?
    if self.allowed_for_all_edorgs
      return false
    end
    progress = true
    puts self.name
    self.authorized_ed_orgs.each { |ed_org| progress = false if !ed_org.to_i != 0 }
    progress
  end

  schema do
    string "client_secret", "redirect_uri", "description", "image_url"
    string "name", "client_id", "application_url", "administration_url"
    string "version", "behavior"
    boolean "is_admin", "license_acceptance", "installed", "allowed_for_all_edorgs"
    time "created", "updated"
    string "authorized_ed_orgs", "vendor"
    string "author_first_name", "author_last_name"

  end


  class Registration < SessionResource
    schema do
      string "status"
    end
  end
end


