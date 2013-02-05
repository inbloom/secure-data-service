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

require "selenium-webdriver"
require 'digest'
require 'time'
require 'date'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

TEST_USER = "samt_testUser_#{Socket.gethostname}"
EULA_TEXT = "Terms and Conditions"

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
end

After do
  remove_user(TEST_USER)
end

Given /^I have an account of "(.*?)"$/ do |role|
  new_user = {
      "uid" => TEST_USER,
      "groups" => [role],
      "fullName" => "samt testUser",
      "password" => "changeit",
      "email" => "samttestuser@slidev.org",
      "tenant" => "Midgar",
      "edorg" => "IL-SUNSET",
      "homeDir" => "/dev/null"
  }
  remove_user(TEST_USER)
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpPost("/users", new_user.to_json,format, sessionId)
  sleep(1)
  currentTimestamp = Time.now.utc.to_i.to_s
  @key = Digest::MD5.hexdigest(SecureRandom.base64(10))
  token = @key + "@" + currentTimestamp
  update_info = {
            :email    => TEST_USER,
            :resetKey => token
          }
  @ldap.update_user_info(update_info)
  sleep(1)
  end
  
When /^I access the production password reset page$/ do
  samt_reset_password_url = PropLoader.getProps['admintools_server_url']+"/resetPassword/newAccount/"+@key
  @driver.get samt_reset_password_url
end

Then /^I "(.*?)" a checkbox with term\-of\-use$/ do |visible_status|
  
   begin
   @explicitWait.until{@driver.find_element(:tag_name,"body")}
   body = @driver.find_element(:tag_name, "body")
   test_pass=true
   if body.text.include?(EULA_TEXT) && visible_status == "not shown"
     test_pass=false
   end
   assert(test_pass,"expected the eula to #{visible_status}")
     
   rescue
   end
   end
     

Then /^I will have to enter my password$/ do
  @driver.find_element(:id,"new_account_password_new_pass").send_keys "test1234"
  @driver.find_element(:id,"new_account_password_confirmation").send_keys "test1234"
end

When /^I submit the reset password form$/ do
  @driver.find_element(:id,"submitForgotPasswordButton").click
end

Then /^the new password is saved$/ do
    sleep(3)
  user=@ldap.read_user(TEST_USER)
  puts user
  assert(user[:password] == "test1234","user password is not saved")
end




def remove_user(email)
  # remove user created for sandbox
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+email, format, sessionId)

  # remove user created for production
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+email, format, sessionId)
end

