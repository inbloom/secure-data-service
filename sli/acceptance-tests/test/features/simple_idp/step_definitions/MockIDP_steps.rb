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

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require_relative '../../dashboard/dash/step_definitions/selenium_common_dash.rb'


Given /^I navigate to databrowser home page$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

When /^I enter the credentials "([^"]*)" "([^"]*)" for the Simple IDP$/ do |arg1, arg2|
  @driver.find_element(:id, "user_id").send_keys arg1
  @driver.find_element(:id, "password").send_keys arg2
end

When /^I want to manually imitate the user "([^"]*)" who is a "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:id, "manualUserBtn").click
  @driver.find_element(:id, "impersonate_user").send_keys arg1
  role_select = @driver.find_element(:id, "selected_roles")
  options = role_select.find_elements(:tag_name=>"option")
  options.each do |e1|
    if (e1.text == arg2)
    e1.click()
    break
    end
  end
  @driver.find_element(:id, "manualUserLoginButton").click
end

Then /^I want to select "(.*?)" from the "(.*?)" in automatic mode$/ do |arg1, arg2|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, "datasets"))
  select.select_by(:value, arg2)
  assertWithWait("Failed to locate list of users dropdown") {@driver.find_element(:id, arg2)}

  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, arg2))
  select.select_by(:value, arg1)
  @driver.find_element(:id, "sampleUserLoginButton").click
end

And /^I should see that I "(.*?)" am logged in$/ do |arg1|
assertWithWait("Failed to find the developers username on the simple idp login page")  {@driver.page_source.include?(arg1)}  
end

When /^I logout of the databrowser$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']+"/entities/system/session/logout"
  @driver.manage.delete_all_cookies
end

Then /^I should see the logout message$/ do
  assertWithWait("Failed to receive successfully logged out message")  {@driver.page_source.include?("You are logged out of SLI")}
end

When /^I click on the simple-idp logout link$/ do
  @driver.find_element(:id, "logoutLink").click
end

Then /^I should be redirected to an API error page that says invalid user$/ do
  assertWithWait("Failed to receive invalid user error message")  {@driver.page_source.include?("Invalid user")}
end

When /^I want to impersonate a custom role of "([^"]*)"$/ do |arg1|
    @driver.find_element(:id, "customRoles").send_keys arg1
end

Then /^I should be redirected to the impersonation page$/ do
  assertWithWait("Failed to be directed to Impersonation's page")  {@driver.find_element(:id, "selected_roles")}
end

Then /^I should be redirected to the databrowser web page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

Then /^I should see the name "([^"]*)" on the page$/ do |arg1|
  assertWithWait("Failed to find #{arg1} on the page") {@driver.page_source.include?(arg1)}
end

Then /^I am denied from accessing the databrowser$/ do
  assertWithWait("Was directed to the databrowser when it shouldn't have")  {!@driver.page_source.include?("Listing Home")}
end

Then /^I get message that I am not authorized to use the Databrowser$/ do
  assertWithWait("Should have received message that databrowser could not be accessed") { @driver.page_source.index("You are not authorized to use this app." ) != nil}
end

When /^I click Login$/ do
  @driver.find_element(:id, "login_button").click
end

When /^I wait for (\d+) second$/ do |arg1|
  sleep(Integer(arg1))
end

When /^I navigate to databrowsers "(.*?)" page$/ do |arg1|
  @driver.get PropLoader.getProps['databrowser_server_url']+arg1
end

Then /^I should see my roles as "(.*?)"$/ do |arg1|
  assertWithWait("Failed to find #{arg1} on the page") {@driver.page_source.include?(arg1)}
end

Then /^I should see my rights include "(.*?)"$/ do |arg1|
  assertWithWait("Failed to find #{arg1} on the page") {@driver.page_source.include?(arg1)}
end

When /^I navigate to the Sample App I should see the name "(.*?)" on the page$/ do |arg1|
  @driver.get PropLoader.getProps['sampleApp_server_address']+"sample"
  assertWithWait("Failed to find #{arg1} on the page") {@driver.page_source.include?(arg1)}
end

When /^I navigate to the Sample App it should crash and burn$/ do 
  @driver.get PropLoader.getProps['sampleApp_server_address']+"sample"
  assertWithWait("Failed to crash and/or burn") {@driver.page_source.include?('java.lang.IllegalArgumentException: invalid start or end')}
end  
