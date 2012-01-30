require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'

Given /^I am from realm "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Given /^another realm's admin mapped "([^"]*)" to the "([^"]*)" default SLI role in their realm$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to the SLI Role Mapping Admin Page$/ do
  url = PropLoader.getProps['admintools_server_url']+"/admin/role-mapping"
  @driver.get url
end

When /^I map the default SLI role "([^"]*)" to the custom role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I see that "([^"]*)" is now mapped to the "([^"]*)" role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

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

Given /^a "([^"]*)" has been mapped to the "([^"]*)" default role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I remove the map from the default SLI role "([^"]*)" to the custom role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I see that "([^"]*)" is no longer mapped to the "([^"]*)" role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
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

Then /^I should not see the mapping from the other realm$/ do
  pending # express the regexp above with the code you wish you had
end