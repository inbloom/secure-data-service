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

require 'selenium-webdriver'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^I have navigated to the databrowser page$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

Given /^I was redirected to the realmchooser page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end
    
Given /^I see the realm selector I authenticate to "([^"]*)"$/ do |arg1|
if (@driver.title.index("Choose your realm") != nil)
  step "I selected the realm \"#{arg1}\""
else
  puts "Realm Selector page was skipped."
end

end

Given /^I selected the realm "([^"]*)"$/ do |arg1|
  sleep 1
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
  @driver.find_element(:id, "go").click
end

Then /^I am redirected to the databrowser home page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

When /^I navigate to the dashboard page$/ do
  @driver.get PropLoader.getProps['dashboard_server_address'] + PropLoader.getProps['dashboard_app_prefix']
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end

Then /^I do not see any login pages$/ do
  success = false
  begin
    @driver.find_element(:name, "Login.Submit")
  rescue
    success = true
  end
  assert(success, webdriverDebugMessage(@driver,"User was redirected to a login page when they shouldn't have"))
end

Then /^I am redirected to the dashboard home page$/ do
  #assertWithWait("Failed to be directed to Dashboards's LoS page")  {@driver.page_source.include?("dashboard")}
  assertWithWait("Failed to be directed to Dashboards's LoS page") {@driver.find_element(:xpath, "/html/body/div/div[2]/div").text.include?("dashboard")}  
end

When /^I navigate to the databrowser page$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end

When /^I click on the logout link$/ do
  assertWithWait("Failed to find the Logout link on the page") {@driver.find_element(:link, "Logout")}
  @driver.find_element(:link, "Exit").click
end

Then /^I should see a message that I was logged out$/ do
  assertWithWait("Failed to find message stating that sign off was successful") { @driver.page_source.downcase.index("successfully logged out") != nil }
end

Then /^I should forced to reauthenticate to gain access$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}  
end

When /^I navigate to the dashboard home page$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

Given /^I have navigated to the sample app page$/ do
  @driver.get PropLoader.getProps['sampleApp_server_address']+"sample"
end

Then /^I am redirected to the sample app home page$/ do
    assertWithWait("Failed to navigate to Sample App home page") {@driver.title.index("List of Students") != nil}
end

When /^I navigate to the sample app page$/ do
  @driver.get PropLoader.getProps['sampleApp_server_address']+"sample"
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end

