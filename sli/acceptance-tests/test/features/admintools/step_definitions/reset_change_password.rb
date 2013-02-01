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
require 'json'
require 'net/imap'
require 'ldapstorage'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require 'date'

Given /^I am a SLI Developer "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

Given /^I am a SLC Operator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

Given /^I am a SLC Admin "([^"]*)" from the "([^"]*)" hosted directory logging in for the first time$/ do |arg1, arg2|
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'], 
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'], 
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
  @ldap.delete_user_attribute(arg1, :emailtoken)
end

When /^I hit the Change Password URL$/ do
  @driver.get(PropLoader.getProps['admintools_server_url'] + "/change_passwords/new")
end

When /^I hit the Admin URL$/ do
  @driver.get(PropLoader.getProps['admintools_server_url'])
end

When /^I visit the link sent to "(.*?)"$/ do |arg1|
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'], 
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'], 
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
  user = @ldap.read_user(arg1)
  resetKey = user[:resetKey].split("@")[0]
  @driver.get(PropLoader.getProps['admintools_server_url'] + "/resetPassword?key=" + resetKey)
end

Then /^I am redirected to the Change Password page$/ do
  assertWithWait("Failed to navigate to the Change Password page")  {@driver.page_source.index("Change Password") != nil}
end

Then /^I am redirected to the Forgot Password page$/ do
  assertWithWait("Failed to navigate to the Forgot Password page")  {@driver.page_source.index("Reset Password") != nil}
end

Then /^I see change password is disabled for production developers$/ do
    assertWithWait("Failed to navigate to the disabled Change Password page") {@driver.page_source.index("Change password operation is disabled for application developers.") != nil}
end

When /^I am forced to change password$/ do
  assertWithWait("Failed to navigate to the Reset Password page")  {@driver.page_source.index("Reset Password") != nil}
end

Then /^I am redirected to the Reset Password page$/ do
  assertWithWait("Failed to navigate to the Reset Password page")  {@driver.page_source.index("Reset Password") != nil}
end

Then /^I see the input boxes to change my password$/ do
  assertWithWait("Failed to find the input boxes to change my password") {@driver.find_element(:id, "new_change_password")}
end

Then /^I see the input box to enter user id$/ do
  assertWithWait("Failed to find the input boxes to change my password") {@driver.find_element(:id, "user_id")}
end

When /^I fill out the input field "([^\"]*)" as "([^\"]*)"$/ do |field, value|
  trimmed = field.sub(" ", "")
  @driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '#{trimmed.downcase}')
    ]").send_keys(value)
end

Then /^I click on "(.*?)"$/ do |arg1|
  @driver.find_element(:xpath, "//input[contains(@id, '#{arg1}')]").click
end

Then /^I click the "(.*?)" link$/ do |arg1| 
  @driver.find_element(:xpath, "//a[contains(@id, '#{arg1}')]").click
end

Then /^I see an error message "(.*?)"$/ do |errorMsg|
  assert(@driver.find_element(:xpath, "//h2[contains(@id, 'errorCountNotifier' and contains(text(), '#{errorMsg}')]").size != 0,
         "Cannot find error message \"#{errorMsg}\"")
end

Then /^I check for message  "(.*?)"$/ do |arg1|
  assertText(arg1)
end

