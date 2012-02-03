require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'

Then /^The user "([^"]*)" who is a "([^"]*)" can now log in to SLI as a "([^"]*)" from my realm "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  # Login and get a session ID
  realm = "sli" if arg3 == "SLI"
  idpRealmLogin(arg1, arg1+"1234", realm)
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  # Validate the user has proper rights according to the role
  assert(result["authenticated"] == true, "User "+arg1+" did not successfully authenticate to SLI")
  assert(result["authorities"].include?("AGGREGATE_READ"), "User "+user+" was not granted AGGREGATE_READ permissions")
  assert(result["authorities"].include?("READ_GENERAL"), "User "+user+" was not granted READ_GENERAL permissions") if ["Educator","Leader","IT Administrator"].include?(arg3)
  assert(result["authorities"].include?("WRITE_GENERAL"), "User "+user+" was not granted WRITE_GENERAL permissions") if ["IT Administrator"].include?(arg3)
  assert(result["authorities"].include?("READ_RESTRICTED"), "User "+user+" was not granted READ_RESTRICTED permissions") if ["Leader","IT Administrator"].include?(arg3)
  assert(result["authorities"].include?("WRITE_RESTRICTED"), "User "+user+" was not granted WRITE_RESTRICTED permissions") if ["IT Administrator"].include?(arg3)
end

Then /^The user "([^"]*)" who is a "([^"]*)" can not access SLI as a "([^"]*)" from my realm "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  # Login and get a session ID
  realm = "sli" if arg3 == "SLI"
  idpRealmLogin(arg1, arg1+"1234", realm)
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  # Validate the user has proper rights according to the role
  assert(result["authenticated"] == true, "User "+arg1+" did not successfully authenticate to SLI")
  assert(result["authorities"].size == 0, "User "+arg1+" was granted permissions when they should have none: "+result["authorities"])
end

When /^I navigate to the Complex\-Configurable Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/realms"
end

Given /^I am authenticated to "([^"]*)" IDP$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^I am not a Super Administrator$/ do
  #No code needed, this is done as configuration
end

Then /^I should get a message that I am not authorized to access the page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am a Super Administrator for "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Then /^I should be redirected to the Complex\-Configurable Role Mapping Page for "([^"]*)"$/ do |arg1|
  assertWithWait("Failed to be redirected to Role mapping page")  {@driver.title.index(arg1) != nil}
end

Given /^I have tried to access the Complex\-Configurable Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/realms"
end

Then /^I am redirected to the Complex\-Configurable Role Mapping Page$/ do
  assertWithWait("Failed to be redirected to Role mapping page")  {@driver.title.index("Role") != nil}
end

Given /^I have navigated to my Complex\-Configurable Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/realms"
  @driver.find_element(:id, "Edit").click
end

When /^I click on the Reset Mapping button$/ do
  @driver.find_element(:id, "Reset").click
end

Then /^the Leader, Educator, Aggregate Viewer and IT Administrator roles are now mapped to themselves$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^no other mappings exist for this realm$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on the role "([^"]*)" radio button$/ do |arg1|
  @driver.find_element(:id, "radio_"+arg1).click
end

When /^I enter "([^"]*)" in the text field$/ do |arg1|
  @driver.find_element(:id, "textbox").send_keys arg1
end

When /^I click the add button$/ do
  @driver.find_element(:id, "add").click
end

Then /^the custom role "([^"]*)" is mapped to the default role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I click on the remove button between role "([^"]*)" and custom role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the custom role "([^"]*)" is no longer mapped to the default role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I get a message that I cannot map the same custom role to multiple SLI Default roles$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I get a message that I already have this role mapped to a SLI Default role$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I see a message that tells me that I can put only alphanumeric values as a custom role$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the mapping is not added between "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Given /^I see pre\-existing mappings$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I no longer see the pre\-existing mappings$/ do
  pending # express the regexp above with the code you wish you had
end

