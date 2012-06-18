require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Then /^The user "([^"]*)" who is a "([^"]*)" can now log in to SLI as a "([^"]*)" from my realm "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  # Login and get a session ID
  idpRealmLogin(arg1, arg1+"1234", arg4)
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("/system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  # Validate the user has proper rights according to the role
  assert(result["authentication"]["authenticated"] == true, "User "+arg1+" did not successfully authenticate to SLI")
  assert(result["authentication"]["authorities"].include?("AGGREGATE_READ"), "User "+arg1+" was not granted AGGREGATE_READ permissions")
  assert(result["authentication"]["authorities"].include?("READ_GENERAL"), "User "+arg1+" was not granted READ_GENERAL permissions") if ["Educator","Leader","IT Administrator"].include?(arg3)
  assert(result["authentication"]["authorities"].include?("WRITE_GENERAL"), "User "+arg1+" was not granted WRITE_GENERAL permissions") if ["IT Administrator"].include?(arg3)
  assert(result["authentication"]["authorities"].include?("READ_RESTRICTED"), "User "+arg1+" was not granted READ_RESTRICTED permissions") if ["Leader","IT Administrator"].include?(arg3)
  assert(result["authentication"]["authorities"].include?("WRITE_RESTRICTED"), "User "+arg1+" was not granted WRITE_RESTRICTED permissions") if ["IT Administrator"].include?(arg3)
end

Then /^The user "([^"]*)" who is a "([^"]*)" can not access SLI as a "([^"]*)" from my realm "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  # Login and get a session ID
  idpRealmLogin(arg1, arg1+"1234", arg4)
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("/system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  # Validate the user has proper rights according to the role
  assert(result["authentication"]["authenticated"] == true, "User "+arg1+" did not successfully authenticate to SLI")
  assert(result["authentication"]["authorities"].size == 0, "User "+arg1+" was granted permissions when they should have none")
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
  assertWithWait("Failed to find forbidden message")  {@driver.page_source.index("Forbidden") != nil}
end

Given /^I am a Super Administrator for "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Then /^I should be redirected to the Complex\-Configurable Role Mapping Page for "([^"]*)"$/ do |arg1|
  assertWithWait("Failed to be redirected to Role mapping page")  {@driver.page_source.index("mapping") != nil}
end

Given /^I have tried to access the Complex\-Configurable Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/realms"
end

Then /^I am redirected to the Complex\-Configurable Role Mapping Page$/ do
  assertWithWait("Failed to be redirected to Role mapping page")  {@driver.page_source.index("mapping") != nil}
end

Given /^I have navigated to my Complex\-Configurable Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/realms"

  assertWithWait("Failed to find the Edit link on the Complex Configurable viewing page")  {@driver.find_element(:link_text, "Edit")}
  @driver.find_element(:link_text, "Edit").click
end

When /^I click on the Reset Mapping button$/ do
  assertWithWait("Failed to find the Reset to Defaults buttonon the Complex Configurable viewing page")  {@driver.find_element(:id, "resetButton")}
  @driver.find_element(:id, "resetButton").click
end

Then /^I got warning message saying 'Are you sure you want to reset the role mappings\?'$/ do
  @driver.switch_to.alert
end

When /^I click 'OK'$/ do
  @driver.switch_to.alert.accept
end

Then /^the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves$/ do
  wait = Selenium::WebDriver::Wait.new(:timeout => 1)
  begin # Catch the exception from the wait... I'd rather get my detailed error messages than generic ones from WebDriver
    wait.until { @driver.execute_script("return document.getElementById(\"mTable\").childNodes.length;") == 4 }
  rescue
  end

  # Seach for two occurances of each of the default roles as elements of <td>s, one being client role other being default role 
  ["Educator","Leader","Aggregate Viewer","IT Administrator"].each do |role|
    results = @driver.find_elements(:xpath, "//td[text()='#{role}']")
    assert(results.size == 2, webdriverDebugMessage(@driver,"Found more than expected occurances of "+role+", expected 2 found "+results.size.to_s))
  end
end

When /^I click on the role "([^"]*)" radio button$/ do |arg1|
  button_id  = "role_Educator" if arg1 == "Educator"
  button_id  = "role_Leader" if arg1 == "Leader"
  button_id  = "role_IT_Administrator" if arg1 == "IT Administrator"
  button_id  = "role_Aggregate_Viewer" if arg1 == "Aggregate Viewer"
  @driver.find_element(:id, button_id).click
end

When /^I enter "([^"]*)" in the text field$/ do |arg1|
  @driver.find_element(:id, "clientRole").clear
  @driver.find_element(:id, "clientRole").send_keys arg1
end

When /^I click the add button$/ do
  @driver.find_element(:id, "addButton").click
end

Then /^the custom role "([^"]*)" is mapped to the default role "([^"]*)"$/ do |arg1, arg2|
  wait = Selenium::WebDriver::Wait.new(:timeout => 1)
  begin # Catch the exception from the wait... I'd rather get my detailed error messages than generic ones from WebDriver
    wait.until { @driver.find_element(:xpath, "//tr/td[text()='#{arg1}']") }
  rescue
  end
  
  # Make sure that the new custom role is mapped to the default role we expect
  found_rows = @driver.find_elements(:xpath, "//tr/td[text()='#{arg1}']/..")

  assert(found_rows.size==1, webdriverDebugMessage(@driver, "Found multiple rows with client role of "+arg1))
  assert(found_rows[0].text.include?(arg2), webdriverDebugMessage(@driver, "Row with client role "+arg1+" was not mapped to default role "+arg2))
end

When /^I click on the remove button between role "([^"]*)" and custom role "([^"]*)"$/ do |arg1, arg2|
  wait = Selenium::WebDriver::Wait.new(:timeout => 1)
  begin
    wait.until { @driver.find_element(:xpath, "//tr/td[text()='#{arg2}']") }
  rescue
  end

  # Make sure that the new custom role is mapped to the default role we expect
  @driver.find_element(:xpath, "//tr/td[text()='#{arg2}']/../td/button").click
end

Then /^the custom role "([^"]*)" is no longer mapped to the default role "([^"]*)"$/ do |arg1, arg2|
  wait = Selenium::WebDriver::Wait.new(:timeout => 1)
  begin
    wait.until { @driver.find_elements(:xpath, "//tr/td[text()='#{arg1}']").size == 0 }
  rescue
  end

  # Make sure that the new custom role is mapped to the default role we expect
  found_rows = @driver.find_elements(:xpath, "//tr/td[text()='#{arg1}']/..")
  assert(found_rows.size==0, webdriverDebugMessage(@driver, "Still found a row that with client role of "+arg1))
end

Then /^I get a message that I cannot map the same custom role to multiple SLI Default roles$/ do
  assertWithWait("Could not find an error message complaining about mapping the same role to different SLI roles")  {@driver.find_element(:class, "errorNotification").text.include?("duplicate")}
end

Then /^I get a message that I already have this role mapped to a SLI Default role$/ do
  assertWithWait("Could not find an error message complaining about the role already existing")  {@driver.find_element(:class, "errorNotification").text.include?("duplicate")}
end

Then /^I see a message that tells me that I can put only alphanumeric values as a custom role$/ do
  assertWithWait("Could not find an error message saying roles must be alphanumeric")  {@driver.find_element(:class, "errorNotification").text.include?("alphanumeric")}
end

Then /^the mapping is not added between default role "([^"]*)" and custom role "([^"]*)"$/ do |arg1, arg2|
  step "the custom role \"#{arg2}\" is no longer mapped to the default role \"#{arg1}\""
end

Given /^I see pre\-existing mappings$/ do
  # No code needed. This step-def is intended to reset the system such that others can use it anyways
end

Then /^I no longer see the pre\-existing mappings$/ do
  # No code needed. This step-def is intended to reset the system such that others can use it anyways
end

