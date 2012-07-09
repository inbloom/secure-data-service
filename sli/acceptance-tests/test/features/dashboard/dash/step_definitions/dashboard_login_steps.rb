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


require 'selenium-webdriver'
require_relative '../../../utils/sli_utils.rb'

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG'] 

Given /^I am not authenticated to SLI$/ do
  url = getBaseUrl() + PropLoader.getProps['dashboard_logout_page']
  @driver.get url
end

When /^I navigate to the Dashboard home page$/ do
  url = getBaseUrl()
  @driver.get url
  # There's a redirect to the realm page, so this assert should fail
  # assert(@driver.current_url == url, "Failed to navigate to "+url)
end

Then /^I should be redirected to the Realm page$/ do
  assertWithWait("Failed to navigate to Realm page")  { @driver.title.index("Choose your realm") }
end

#TODO this should support both live and test mode
Given /^I am authenticated to SLI as "([^"]*)" password "([^"]*)"$/ do |username, password|
  localLogin(username, password)
end

Then /^I should be redirected to the Dashboard landing page$/ do
  @expected_url = getBaseUrl() + PropLoader.getProps['dashboard_landing_page'];
  assertWithWait("Failed to navigate to "+@expected_url)  { @driver.current_url == @expected_url }
end

Given /^was redirected to the Realm page$/ do
  # TODO
  # This should block until the Realm page is loaded
  sleep 1
end

Given /^was redirected to the SLI\-IDP login page$/ do
  # TODO
  # This should block until the login page is loaded
  sleep 1
end

Given /^I chose "([^"]*)"$/ do |arg1|
  # TODO
  # Fill in when there is integration with Realm page
end

Given /^I clicked the Go button$/ do
  # TODO
  # Fill in when there is integration with Realm page
end

# TODO this should support both live and test mode
Given /^I enter "([^"]*)" in the username text field and "([^"]*)" in the password text field$/ do |username, password|
  assertMissingField("j_username", "name")
  assertMissingField("j_password", "name")
  putTextToField(username, "j_username", "name")
  putTextToField(password, "j_password", "name")
end

Given /^I clicked the Submit button$/ do
  assertMissingField("submit", "name")
  clickButton("submit", "name")
end

Then /^I am informed that "([^"]*)"$/ do |arg1|
 checkForTextInBody(arg1)
end

Then /^I am redirected to the SLI\-IDP Login page$/ do
  # We don't have true integration yet
  # TODO: 
  @realmPageUrl = getBaseUrl() + PropLoader.getProps['dashboard_login_page']
  assert(@driver.current_url.start_with?(@realmPageUrl), "Failed to navigate to "+@realmPageUrl)
end

When /^I access "([^"]*)"$/ do |path|
  url = getBaseUrl() + path
  @driver.get url
end

Then /^I get an error message "([^"]*)"$/ do |errMsg|
  @explicitWait.until{@driver.page_source.include?(errMsg)}
end

Then /^I get an error code "([^"]*)"$/ do |errCode|
  # TODO: 
  # Is there's no way to get the http status code from selenium?? 
end

Then /^I can see "([^"]*)"$/ do |arg1|
  assertText(arg1)
end

Then /^I add a cookie for linda.kim$/ do
  #TODO fix using long lived session in web-based test
  @driver.manage.add_cookie(:name=> "SLI_DASHBOARD_COOKIE",:value=>"4cf7a5d4-37a1-ca19-8b13-b5f95131ac85")
  if ENV['FORCE_COLOR']
    puts "\e[31mWHY IS THIS TEST USING A LONG LIVED SESSION? THIS IS WRONG\e[0m"
  else
    puts "WHY IS THIS TEST USING A LONG LIVED SESSION? THIS IS WRONG"
  end
end

def checkForTextInBody(expectedText)
  #make sure something is loaded, caveat, it might be still the old page
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  assertText(expectedText)
end
