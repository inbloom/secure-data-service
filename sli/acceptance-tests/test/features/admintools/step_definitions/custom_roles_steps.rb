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

require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Transform /rights "(.*?)"/ do |arg1|
  # Default rights for SLI Default roles  
  rights = ["READ_GENERAL", "AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "Educator"
  rights = ["READ_GENERAL", "WRITE_GENERAL", "READ_RESTRICTED", "WRITE_RESTRICTED", "AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "IT Administrator"
  rights = ["READ_GENERAL", "READ_RESTRICTED", "AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "Leader"
  rights = ["AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "Aggregate Viewer"
  # Custom right sets for test roles
  rights = ["READ_GENERAL"] if arg1 == "Read General"
  rights = ["READ_GENERAL", "WRITE_GENERAL"] if arg1 == "Read and Write General"
  rights = [] if arg1 == "none"
  rights
end

Transform /roles "(.*?)"/ do |arg1|
  roles = ["Dummy"] if arg1 == "Dummy"
  roles = [] if arg1 == "none"
  roles
end

When /^I navigate to the Custom Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/custom_roles"
end

Then /^I have navigated to my Custom Role Mapping Page$/ do
  assertWithWait("Failed to be redirected to Role mapping page")  {@driver.page_source.index("Custom") != nil}
end

When /^I got a warning message saying "([^"]*)"$/ do |arg1|
  @driver.switch_to.alert
end

When /^I click 'OK' on the warning message$/ do
  @driver.switch_to.alert.accept
end

When /^I click on the Add Group button$/ do
  btn = @driver.find_element(:id, "addGroupButton")
  btn.click
end

When /^I type the name "([^"]*)" in the Group name textbox$/ do |title|
  input = @driver.find_element(:id, "groupName")
  input.send_keys(title)
end

Then /^a new group is created titled "([^"]*)"$/ do |title|
  @driver.find_element(:xpath, "//td[text()='#{title}']")
end

Then /^the group "([^"]*)" contains the (roles "[^"]*")$/ do |title, roles|
  group = @driver.find_element(:xpath, "//div[text()='#{title}']/../..")
  roles.each do |role|
    group.find_elements(:xpath, "//span[text()='#{role}']")
  end
end

Then /^the group "([^"]*)" contains the (rights "[^"]*")$/ do |title, rights|
  group = @driver.find_element(:xpath, "//div[text()='#{title}']/../..")
  rights.each do |right|
    group.find_elements(:xpath, "//span[text()='#{right}']")
  end
end

When /^I hit the save button$/ do
  @driver.find_element(:id, "rowEditToolSaveButton").click
end

Then /^I am informed that I must have at least one role and right in the group$/ do
  assertWithWait("Could not find an error message complaining about the role and right missing")  { @driver.find_element(:class, "alert-error").text.include?("Validation") }
end

When /^I add the right "([^"]*)" to the group "([^"]*)"$/ do |right, group|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, "addRightSelect"))
  puts("The select is #{@driver.find_element(:id, "addRightSelect").text}")
  select.select_by(:text, right)
  @driver.find_element(:id, "addRightButton").click
end

When /^I add the role "([^"]*)" to the group "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:id, "addRoleInput").send_keys arg1
  @driver.find_element(:xpath, "//button[text()='Add']").click
end

When /^I create a new role <Role> to the group <Group> that allows <User> to access the API$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
    step "I edit the group #{hash["Group"]}"
    step "I add the role #{hash["Role"]} to the group #{hash["Group"]}"
    step "I hit the save button"
    #TODO add stuff to validate the new role is in the group
    step "the user #{hash["User"]} can access the API with rights #{hash["Role"]}"
  end
end

Then /^the user "([^"]*)" can access the API with (rights "[^"]*")$/ do |arg1, arg2|
  # Login and get a session ID
  idpRealmLogin(arg1, arg1+"1234", "IL")
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("/system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  # Validate the user has expected rights
  assert(result["authentication"]["authenticated"] == true, "User "+arg1+" did not successfully authenticate to SLI")
  puts("#{arg2}")
  assert(result["authentication"]["authorities"].size == arg2.size, "User "+arg1+" was granted #{result["authentication"]["authorities"].size} permissions but expected #{arg2.size}")
  arg2.each do |right|
    assert(result["authentication"]["authorities"].include?(right), "User "+arg1+" was not granted #{right} permissions")
  end
end

When /^I remove the right "([^"]*)" from the group "([^"]*)"$/ do |arg1, arg2|
  step "I edit the group \"#{arg2}\""
  # Find the thing you want to delete
  @driver.find_element(:xpath, "//div[text()='#{arg1}']/../button").click
end

When /^I remove the role <Role> from the group <Group> that denies <User> access to the API$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
    step "I edit the group #{hash["Group"]}"
    step "I remove the role #{hash["Role"]} from the group #{hash["Group"]}"
    step "I hit the save button"
    #TODO add stuff to validate the role has been removed from the group
    step "the user #{hash["User"]} can no longer access the API with rights #{hash["Role"]}"
  end
end

Then /^That user can no longer access the API$/ do
  # Dummy step, validation is done in WHEN step, this step just used to make the Gherkin read happy
end

Then /^That user can now access the API$/ do
  # Dummy step, validation is done in WHEN step, this step just used to make the Gherkin read happy
end

Then /^I no longer see that mapping in the table$/ do
  # Dummy step, validation is done in WHEN step, this step just used to make the Gherkin read happy
end

When /^I remove the role "([^"]*)" from the group "([^"]*)"$/ do |arg1, arg2|
  step "I edit the group \"#{arg2}\""
  # Find the thing you want to delete
  @driver.find_element(:xpath, "//div[text()='#{arg1}']/../button").click
end

When /^I edit the group "([^"]*)"$/ do |arg1|
  @driver.find_element(:xpath, "//div[text()='#{arg1}']").click
  @driver.find_element(:id, "rowEditToolEditButton").click
end

When /^I remove the group "([^"]*)"$/ do |arg1|
  @driver.find_element(:xpath, "//div[text()='#{arg1}']").click
  @driver.find_element(:id, "rowEditToolDeleteButton").click
  @driver.switch_to.alert.accept
end

Then /^the group "([^"]*)" no longer appears on the page$/ do |arg1|
  lower_timeout_for_same_page_validation
  groups = @driver.find_elements(:xpath, "//div[text()='#{arg1}']")
  assert(groups.size == 0, "Found group named #{arg1} on page")
  reset_timeouts_to_default
end

When /^I edit the rights for the group <Group> to include the duplicate right <Right>$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
    step "I edit the group #{hash["Group"]}"
    # Check that the duplicate right is not an available choice in the dropdown
    group = @driver.find_elements(:xpath, "//div[text()='#{hash["Group"]}']/../..")
    select = Selenium::WebDriver::Support::Select.new(group.find_element(:tag_name, "select"))
    select.options.each do |option|
      assert(option.text != hash["Right"], "Duplicate Right detected! Right: #{hash["Right"]}")
    end
    # Hit cancel to return to known state
    step "I click the cancel button"
  end
end

Then /^I cannot find the right in the dropdown$/ do
  # Dummy step, validation is done in WHEN step, this step just used to make the Gherkin read happy
end

When /^I edit the roles for the group <Group> to include the duplicate role <Role>$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
    step "I edit the group #{hash["Group"]}"
    step "I add the role #{hash["Role"]} to the group #{hash["Group"]}"
    @driver.switch_to.alert.accept
    step "I click the cancel button"
  end
end

Then /^I am informed that "([^"]*)"$/ do |arg1|
  # Dummy step, validation is done in WHEN step, this step just used to make the Gherkin read happy
end

When /^I click the cancel button$/ do
  btn = @driver.find_element(:id, "rowEditToolCancelButton")
  btn.click
end

When /^I click on the Reset Mapping button$/ do
  @driver.find_element(:id, "resetToDefaultsButton").click
end

Then /^the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves$/ do
  # Seach for two occurances of each of the default roles as elements of <td>s, one being client role other being default role 
  ["Educator","Leader","Aggregate Viewer","IT Administrator"].each do |role|
    results = @driver.find_elements(:xpath, "//td/div[text()='#{role}']")
    moreResults = @driver.find_elements(:xpath, "//td/div/span[text()='#{role}']")
    assert(results.size + moreResults.size == 2, webdriverDebugMessage(@driver,"Found unexpected occurences of role "+role+", expected 2 found "+results.size.to_s))
  end
end

Then /^I see the mapping in the table$/ do
  # Dummy step, validation is done in WHEN step, this step just used to make the Gherkin read happy
end

Then /^the save button is disabled$/ do
  assert(!@driver.find_element(:id, "rowEditToolSaveButton").enabled?, "Save button should be disabled")
end
