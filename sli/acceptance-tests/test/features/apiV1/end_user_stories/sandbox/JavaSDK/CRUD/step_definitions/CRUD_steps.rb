require_relative '../../../../../../utils/sli_utils.rb'
require_relative '../../../../../../dashboard/dash/step_definitions/selenium_common_dash.rb'

Given /^I am logged in using "([^"]*)" and "([^"]*)" to realm Daybreak Central High$/ do |user, pass|
  @byPassToken = $SESSION_MAP[user+"_IL"]
  assert(@byPassToken!=nil,"cant login user #{user}")
end

Given /^the Java SDK test app  is deployed on test app server$/ do
  @appPrefix = "oauth2-sample/testsdk"
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
  url = url + @appPrefix+"?byPassToken="+URI.escape(@byPassToken)+"&test="+testType
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