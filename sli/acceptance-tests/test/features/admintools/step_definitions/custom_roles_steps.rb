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
  rights = ["READ_GENERAL", "WRITE_GENERAL", "WRITE_PUBLIC", "READ_RESTRICTED", "WRITE_RESTRICTED", "AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "IT Administrator"
  rights = ["READ_GENERAL", "READ_RESTRICTED", "AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "Leader"
  rights = ["AGGREGATE_READ", "READ_PUBLIC"] if arg1 == "Aggregate Viewer"
  rights = ["READ_GENERAL"] if arg1 == "New Custom"
  # Custom right sets for test roles
  rights = ["READ_GENERAL", "WRITE_GENERAL", "READ_RESTRICTED", "WRITE_RESTRICTED", "AGGREGATE_READ", "READ_PUBLIC", "WRITE_PUBLIC", "AGGREGATE_WRITE"] if arg1 == "all defaults"
  rights = ["READ_GENERAL"] if arg1 == "Read General"
  rights = ["READ_GENERAL", "WRITE_GENERAL"] if arg1 == "Read and Write General"
  rights = ["READ_GENERAL", "READ_PUBLIC", "READ_AGGREGATE"] if arg1 == "Read General Public and Aggregate"
  rights = [] if arg1 == "none"
  rights
end

Transform /roles "(.*?)"/ do |arg1|
  roles = ["Dummy"] if arg1 == "Dummy"
  roles = ["Educator"] if arg1 == "Educator"
  roles = ["Leader"] if arg1 == "Leader"
  roles = ["Aggregate Viewer"] if arg1 == "Aggregate Viewer"
  roles = ["IT Administrator"] if arg1 == "IT Administrator"
  roles = ["Dummy"] if arg1 == "New Custom"
  roles = [] if arg1 == "none"
  roles
end

When /^I navigate to the Custom Role Mapping Page$/ do
  @driver.get PropLoader.getProps['admintools_server_url']+"/custom_roles"
end

Then /^I am shown the custom roles page for "(.*?)"$/ do |arg1|
    assertWithWait("Failed to be redirected to Role mapping page for realm #{arg1}")  {@driver.page_source.index("Custom Roles for #{arg1}") != nil}
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

When /^I navigate back to the realm listing page$/ do
  @driver.find_element(:link_text, "Cancel").click
end

When /^I type the name "([^"]*)" in the Group name textbox$/ do |title|
  input = @driver.find_element(:id, "groupNameInput")
  input.send_keys(title)
end

Then /^a new group is created titled "([^"]*)"$/ do |title|
  @driver.find_element(:xpath, "//td[text()='#{title}']")
end

Then /^the group "([^"]*)" contains the (roles "[^"]*")$/ do |title, roles|
  group = @driver.find_element(:xpath, "//div[text()='#{title}']/../..")

  assertWithWait("Expected #{roles.size} roles, but saw #{group.find_elements(:class, "role").size} in group #{title}") {group.find_elements(:class, "role").size == roles.size}
  roles.each do |role|
    group.find_elements(:xpath, "//span[text()='#{role}']")
  end
end

Then /^the group "([^"]*)" contains the (rights "[^"]*")$/ do |title, rights|
  group = @driver.find_element(:xpath, "//div[text()='#{title}']/../..")

  assertWithWait("Expected #{rights.size} roles, but saw #{group.find_elements(:class, "right").size} in group #{title}") {group.find_elements(:class, "right").size == rights.size}
  rights.each do |right|
    group.find_elements(:xpath, "//span[text()='#{right}']")
  end
end

When /^I hit the save button$/ do
  saveButtons = @driver.find_elements(:class, "rowEditToolSaveButton")
  saveButtons.each do |save|
    if save.displayed?
      save.click
      break
    end
  end
end

Then /^I am no longer in edit mode$/ do
  # Use the presence of the Edit button as proof we are not in edit mode 
  assertWithWait("Was not returned to viewing mode") {@driver.find_element(:xpath, "//button[text()='Edit']")}
end

Then /^I am informed that I must have at least one role and right in the group$/ do
  @driver.switch_to.alert.accept
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
    step "I am no longer in edit mode"
    step "the user #{hash["User"]} in tenant \"IL\" can access the API with rights #{hash["Group"]}"
  end
end

Then /^the user "([^"]*)" in tenant "([^"]*)" can access the API with (rights "[^"]*")$/ do |user, tenant, rights|
  # Wait two seconds for the API to catch up
  sleep 2
  
  # Login and get a session ID
  idpRealmLogin(user, user+"1234", tenant)
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("/system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  # Validate the user has expected rights
  assert(result["authentication"]["authenticated"] == true, "User "+user+" did not successfully authenticate to SLI")
  assert(result["authentication"]["authorities"].size == rights.size, "User "+user+" was granted #{result["authentication"]["authorities"].size} permissions but expected #{rights.size}")
  rights.each do |right|
    assert(result["authentication"]["authorities"].include?(right), "User "+user+" was not granted #{right} permissions")
  end
end

When /^I remove the right "([^"]*)" from the group "([^"]*)"$/ do |arg1, arg2|
  group = @driver.find_element(:xpath, "//div[text()='#{arg2}']/../..")
  group.find_element(:id, "DELETE_" + arg1).click
end

When /^I remove the role <Role> from the group <Group> that denies <User> access to the API$/ do |table|
  # table is a Cucumber::Ast::Table
  table.hashes.each do |hash|
    step "I edit the group #{hash["Group"]}"
    step "I remove the role #{hash["Role"]} from the group #{hash["Group"]}"
    step "the group #{hash["Group"]} contains the roles #{hash["Group"]}"
    step "I hit the save button"
    step "I am no longer in edit mode"
    step "the user #{hash["User"]} in tenant \"IL\" can access the API with rights \"none\""
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
  group = @driver.find_element(:xpath, "//div[text()='#{arg2}']/../..")
  group.find_element(:id, "DELETE_" + arg1).click
end

When /^I edit the group "([^"]*)"$/ do |arg1|
  row = @driver.find_element(:xpath, "//div[text()='#{arg1}']/../..")
  row.find_element(:class, "rowEditToolEditButton").click
end

When /^I remove the group "([^"]*)"$/ do |arg1|
  row = @driver.find_element(:xpath, "//div[text()='#{arg1}']/../..")
  row.find_element(:class, "rowEditToolDeleteButton").click

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
    select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, "addRightSelect"))
    # select = Selenium::WebDriver::Support::Select.new(group.find_element(:tag_name, "select"))
    select.options.each do |option|
      assert(option.text != hash["Right"], "Duplicate Right detected! Right: #{hash["Right"]}")
    end
    # Hit cancel to return to known state
    puts("Current right is #{hash['Right']} and group is #{hash['Group']}")
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
  row = @driver.find_element(:xpath, "//div[@id='addRoleUi']/../..")
  button = row.find_element(:class, "rowEditToolCancelButton")
  button.click
end

When /^I click on the Reset Mapping button$/ do
  @driver.find_element(:id, "resetToDefaultsButton").click
end

Then /^the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves$/ do
  # Why oh why does Jenkins hate me so, sleep to appease the CI Gods
  sleep 2

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
  assert(!@driver.find_element(:class, "rowEditToolSaveButton").enabled?, "Save button should be disabled")
end

Then /^the IT Administrator role is the only admin role$/ do
  ["Educator","Leader","Aggregate Viewer","IT Administrator"].each do |role|
    row = @driver.find_element(:xpath, "//tr/td/div[text()='#{role}']/../..")
    checked = row.find_element(:class, "isAdmin").attribute("checked")
    assert(((role != "IT Administrator" and !checked) or (role == "IT Administrator" and checked)), "Role is #{role} and admin status is #{checked}")
  end
end

When /^I check the admin role box$/ do
  checkboxes = @driver.find_elements(:class, "isAdmin")
  checkboxes.each do |checkbox|
      checkbox.click
      puts("Clicking the check")
  end
end

Then /^the group "(.*?)" has the admin role box checked$/ do |title|
  group = @driver.find_element(:xpath, "//div[text()='#{title}']/../..")
  checkbox = group.find_element(:class, "isAdmin")
  puts("The group is #{group.text} and checked is #{checkbox.attribute("checked").inspect}")
  assert(checkbox.attribute("checked") == "true", "The admin checkbox for group #{title} is not checked")
end

Then /^the user "(.*?)" in tenant "(.*?)" can access the API with adminUser "(.*?)"$/ do |user, tenant, adminChecked|
  # Wait two seconds for the API to catch up
  sleep 2

  # Login and get a session ID
  idpRealmLogin(user, user+"1234", tenant)
  assert(@sessionId != nil, "Session returned was nil")
  
  # Make a call to Session debug and look that we are authenticated
  restHttpGet("/system/session/debug", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  puts("The result is #{result.inspect}")
  puts("\nand the thing is #{result["authentication"]["principal"]["adminUser"].class}")
  adminChecked = adminChecked == "true" ? true : false
  assert(result["authentication"]["authenticated"] == true, "User "+user+" did not successfully authenticate to SLI")
  assert(result["authentication"]["principal"]["adminUser"] == adminChecked, "User's admin status is not #{adminChecked}")
end

