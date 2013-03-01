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


When /^I hit the delegation url$/ do
  @driver.get(PropLoader.getProps['admintools_server_url'] + "/admin_delegations")
end

When /^I am redirected to the delegation page for my district$/ do
  assertWithWait("Expected to be on district delegation page") do
    @driver.page_source.index("Delegate District Privileges") != nil
  end
end

When /^"([^"]*)" is unchecked$/ do |feature|
  checkbox = getCheckbox(feature)
  assertWithWait("Expected #{feature} checkbox to be unchecked") do
    !checkbox.attribute("checked")
  end
end

When /^I check the "([^"]*)"$/ do |feature|
  checkbox = getCheckbox(feature)
  checkbox.click
end

# Same thing as checking
When /^I uncheck the "([^"]*)"$/ do |feature|
  checkbox = getCheckbox(feature)
  checkbox.click
end

Then /^"([^"]*)" is checked$/ do |feature|
  checkbox = getCheckbox(feature)
  assertWithWait("Expected #{feature} checkbox to be checked") do
    checkbox.attribute("checked")
  end
end

Then /^I get the message "([^"]*)"$/ do |errorMessage|
  assert(@driver.page_source.index(errorMessage) != nil, "Expected error message: '#{errorMessage}'")
end

Then /^I see a dropdown box listing both districts$/ do
  assert(@driver.find_element(:id, "districtSelection") != nil, "Could not find district dropdown")
end

Then /^I select "([^"]*)" in the district dropdown$/ do |district|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, district)
end

Then /^I see the table for "([^"]*)"$/ do |district|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  option = select.first_selected_option
  assert(option.text == district, "We should have selected #{district}")
  table = @driver.find_element(:id, "AuthorizedAppsTable_" + option.attribute("value"))
  assert(table.displayed?)
end

Then /^I do not see the table for "([^"]*)"$/ do |district|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  option = select.first_selected_option
  unselected = nil
  select.options.each do |o| 
    unselected = o if o.text == district
  end
  assert(option.text != district, "We should have selected #{district}")
  table = @driver.find_element(:id, "AuthorizedAppsTable_" + unselected.attribute("value"))
  assert(!table.displayed?)
end


def getCheckbox(feature)
    if feature == "Application Authorization"
    id = "admin_delegation_appApprovalEnabled"
    elsif feature == "View Security Events"
    id = "admin_delegation_viewSecurityEventsEnabled"
  else
    assert(false, "Could not find the ID for #{feature} checkbox")
  end
  checkbox = @driver.find_element(:id, id)
  return checkbox
end

