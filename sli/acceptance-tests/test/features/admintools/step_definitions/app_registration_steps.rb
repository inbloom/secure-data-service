require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'


Given /^I am a valid SLI Administrator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

When /^I hit the Application Registration Tool URL$/ do
  @driver.get(PropLoader.getProps['admintools_server_url']+"/apps/")
end

When /^I get redirected to the IDP login page$/ do
  assertWithWait("Failed to navigate to the IDP Login page")  {@driver.find_element(:id, "IDToken1")}
end

When /^I authenticate with username "([^"]*)" and password "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:id, "IDToken1").send_keys arg1
  @driver.find_element(:id, "IDToken2").send_keys arg2
  @driver.find_element(:name, "Login.Submit").click
end

Then /^I am redirected to the Application Registration Tool page$/ do
  assertWithWait("Failed to navigate to the Admintools App Registration page")  {@driver.page_source.index("Application Registeration") != nil}
end

Then /^I see all of the applications that are registered to SLI$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^those apps are sorted by the Last Update column$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am a valid IT Administrator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

Then /^I receive a message that I am not authorized$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I have clicked to the button New$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^a new row was created in the table$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I entered the name "([^"]*)" into the field titled "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I have ented data into the other requried fields except for the shared secret and the app id which are read\-only$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the button Save$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the new application is created$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the application is listed in the table on the top$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^a client ID is created for the new application that can be used to access SLI$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the row of application named "([^"]*)" in the table$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^the row expands$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I see the details of "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^all the fields are read only$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I clicked on the button ‘Edit’$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the row of the app "([^"]*)" expanded$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^every field except the shared secret and the app ID became editable$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I have edited the field named "([^"]*)" to say "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I clicked Save$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the info for "([^"]*)" was updated$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I the field named "([^"]*)" still says "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I have clicked on the button 'X' for the application named "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I got warning message saying 'You are trying to remove this application from SLI\. By doing so, you will prevent any active user to access it\. Do you want to continue\?'$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click 'Yes'$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the application named "([^"]*)" is removed from the SLI$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^the previously generated client ID can no longer be used to access SLI$/ do
  pending # express the regexp above with the code you wish you had
end
