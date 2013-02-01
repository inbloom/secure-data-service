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



class ForgotPassword

  include ActiveModel::Validations
  include ActiveModel::Conversion
  extend ActiveModel::Naming

  attr_accessor :token, :new_pass, :confirmation

  validates_presence_of :token, :new_pass, :confirmation
  validates :new_pass, :confirmation => true #password_confirmation attr
  validate :confirm_new

  def initialize(attributes = {})
    attributes.each do |name, value|
      send("#{name}=", value)
    end
  end

  def persisted?
    false
  end

  def confirm_new
    if self.new_pass != self.confirmation
      errors[:new_pass] << "New passwords do not match."
    end
  end

  def set_password
    user = APP_LDAP_CLIENT.read_user_resetkey(token)
    if !!user && valid? == true
      begin
        emailToken = user[:emailtoken]
        if emailToken.nil?
          currentTimestamp = DateTime.current.utc.to_i.to_s
          emailToken = Digest::MD5.hexdigest(SecureRandom.base64(10)+currentTimestamp+user[:email]+user[:first]+user[:last])
        end
        update_info = {
            :email => "#{user[:email]}",
            :password => "#{new_pass}",
            :resetKey => "",
            :emailtoken => "#{emailToken}"
        }
        response =  APP_LDAP_CLIENT.update_user_info(update_info)
        emailAddress = user[:emailAddress]
        fullName = user[:first] + " " + user[:last]
        yield(emailAddress, fullName)
        return true
      rescue InvalidPasswordException => e
        Rails.logger.error e.message
        Rails.logger.error e.backtrace.join("\n")
        APP_CONFIG['password_policy'].each { |msg|  errors.add(:new_pass, msg) }
      rescue Exception => e
        Rails.logger.error e.message
        Rails.logger.error e.backtrace.join("\n")
        errors.add(:base, "Unable to change password, please try again.")
      end
    end
    return false
  end

end
