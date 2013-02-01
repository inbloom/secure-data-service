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


class UserAccountRegistration

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :email, :firstName, :lastName, :password, :vendor

  validates_presence_of :firstName, :lastName, :email, :password, :password_confirmation
  validates :email, :email => true
  validates :password, :confirmation => true #password_confirmation attr
  validates_presence_of :vendor unless APP_CONFIG["is_sandbox"]

  def initialize(attributes = {})
    attributes.each do |name, value|
      send("#{name}=", value)
    end
  end

  def persisted?
    false
  end

  def register
    if @vendor.nil? or @vendor == ""
      @vendor = "None"
    end

    ApplicationHelper.add_user(self)
  end

  private
  def is_sandbox?
    APP_CONFIG["is_sandbox"]
  end

end
