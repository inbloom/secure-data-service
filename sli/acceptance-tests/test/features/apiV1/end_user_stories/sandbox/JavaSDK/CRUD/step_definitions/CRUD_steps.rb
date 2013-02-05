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


require_relative '../../../../../../utils/sli_utils.rb'
require_relative '../../../../../../dashboard/dash/step_definitions/selenium_common_dash.rb'
require_relative '../../../../../../dashboard/dash/step_definitions/dashboard_api_integration_steps.rb'

Given /^the Java SDK test app  is deployed on test app server$/ do
  @appPrefix = "sample/"
end

Given /^I navigate to the Sample App$/ do
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + @appPrefix
  puts url
  @driver.get url
  @appPrefix = "sample/testsdk"
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 30)
end

Given /^I navigate to the Sample App REST client$/ do
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + @appPrefix
  puts url
  @driver.get url
  @appPrefix = "sample/testrest"
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 30)
end

When /^I put "([^"]*)"  with Name "([^"]*)"$/ do |arg1, arg2|
  # implemented in SDK test app
end

When /^Sex as "([^"]*)" and$/ do |arg1|
   # implemented in SDK test app
end

When /^BirthDate as "([^"]*)"$/ do |arg1|
   # implemented in SDK test app
end

When /^Address is "([^"]*)"$/ do |arg1|
   # implemented in SDK test app
end

Then /^the student is added$/ do
   # implemented in SDK test app
end

When /^I read all the students$/ do
   # implemented in SDK test app
end

Then /^I should find student "([^"]*)" in the student list$/ do |arg1|
  # implemented in SDK test app
end

When /^I send test request "([^"]*)" to SDK CRUD test url$/ do |testType|
  url = PropLoader.getProps['sampleApp_server_address']
  url = url + @appPrefix + "?test="+testType
  puts url
  @driver.get url 
end

Then /^I should receive response "([^"]*)"$/ do |arg1|
   testResult = @driver.find_element(:id, "testResult")
   puts testResult.text
   assert(testResult.text==arg1,"didnt receive response #{arg1}")
end

When /^I update "([^"]*)"  "([^"]*)" address  to "([^"]*)"$/ do |arg1, arg2, arg3|
   # implemented in SDK test app
end

Then /^the street address is updated$/ do
   # implemented in SDK test app
end

When /^I read "([^"]*)" with name "([^"]*)"$/ do |arg1, arg2|
   # implemented in SDK test app
end

Then /^her address is "([^"]*)"$/ do |arg1|
   # implemented in SDK test app
end

When /^I delete "([^"]*)" with name "([^"]*)"$/ do |arg1, arg2|
   # implemented in SDK test app
end

Then /^the student is deleted$/ do
   # implemented in SDK test app
end

When /^I read collection "([^"]*)"$/ do |arg1|
   # implemented in SDK test app
end

Then /^I student "([^"]*)" is not in the student list$/ do |arg1|
   # implemented in SDK test app
end

When /^I query for "([^"]*)" with "([^"]*)" as "([^"]*)" and sort by "([^"]*)" and sortOrder "([^"]*)"$/ do |arg1, arg2, arg3, arg4, arg5|
   # implemented in SDK test app
end

Then /^I should see a descending list off teachers where first teachers firstName is "([^"]*)"$/ do |arg1|
   # implemented in SDK test app
end
