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

Given /^I have navigated to the "([^"]*)" login page$/ do |arg1|
  @url = "https://"+PropLoader.getProps['sea_login_url'] if arg1 == "State/District"
  @driver = Selenium::WebDriver.for :ie
  @driver.get @url if @url
  assert(@driver.current_url == @url, "Failed to navigate to "+@url)
end

Given /^I am user "([^"]*)"$/ do |arg1|
#  Step unnecessary, since the username is entered in explicitly in a later step
end

Given /^"([^"]*)" is valid "([^"]*)" user$/ do |arg1, arg2|
#  No code needed, this is done while setting up (configuration of) the ADFS
end

Given /^"([^"]*)" is invalid "([^"]*)" user$/ do |arg1, arg2|
#  No code needed, this is done while setting up (configuration of) the ADFS
end

When /^I enter "([^"]*)" in the username text field$/ do |arg1|
  @driver.find_element(:id, "username").send_keys arg1
end

When /^I enter "([^"]*)" in the password text field$/ do |arg1|
  @driver.find_element(:id, "password").send_keys arg1
end

When /^I click the Go button$/ do
  @driver.find_element(:id, "submit").click
end

Then /^I am now authenticated to SLI$/ do
  @apiUrl = "http://"+PropLoader.getProps['api_server_url']+"/api/"
  @driver.get @apiUrl
  assert(@driver.current_url == @apiUrl, "Failed to navigate to "+@apiUrl)
end

Then /^I am informed that I have entered invalid password$/ do
  # Check for some error message that states something about the password, assume its a negative message,
  #   as we will be making sure the login was unsuccessful with a call to the API that should fail
  assert(@driver.find_element(:name, "error").text.downcase.index("password") != nil, "Could not find message stating the password was wrong")
  @apiUrl = "http://"+PropLoader.getProps['api_server_url']+"/api/"
  @driver.get @apiUrl
  assert(@driver.current_url != @apiUrl, "Successfully to navigate to "+@apiUrl+" in a negative test")
end

Then /^I am informed that "([^"]*)" is an invalid user$/ do |arg1|
  # Check for some error message that states something about the user, assume its a negative message,
  #   as we will be making sure the login was unsuccessful with a call to the API that should fail
  assert(@driver.find_element(:name, "error").text.downcase.index("user") != nil, "Could not find message stating the username was wrong")
  @apiUrl = "http://"+PropLoader.getProps['api_server_url']+"/api/"
  @driver.get @apiUrl
  assert(@driver.current_url != @apiUrl, "Successfully to navigate to "+@apiUrl+" in a negative test")
end
