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


require_relative '../../../utils/sli_utils.rb'

When /^I go to "([^"]*)"$/ do |student_list|
  @driver.find_element(:link, "Dashboard").click
end

When /^I select <edOrg> "([^"]*)"$/ do |elem|
  select_by_id(elem, "edOrgSelect")
end

When /^I select <school> "([^"]*)"$/ do |elem|
  select_by_id(elem, "schoolSelect")
end

When /^I select <course> "([^"]*)"$/ do |elem|
  select_by_id(elem, "courseSelect")
end

When /^I select <section> "([^"]*)"$/ do |elem|
  select_by_id(elem, "sectionSelect")
  clickOnGo()
end

Then /^I should have a dropdown selector named "([^"]*)"$/ do |elem|
  elem += "Menu"
  @selector = @explicitWait.until{@driver.find_element(:css, "div[class*='#{elem}']")}
end

Then /^I should have a selectable view named "([^"]*)"$/ do |view_name|
  @selector = @explicitWait.until{@driver.find_element(:css, "div[class*='viewSelectMenu']")}
  spans = get_all_elements
  assert(spans.length > 0, "No views found")
  found = false
  spans.each do |span|
    puts span
    if (span.should include view_name)
      found = true
      break
    end
  end
  assert(found, "View was not found")
end

Then /^I should only see one view named "([^"]*)"$/ do |view_name|
  @selector = @explicitWait.until{@driver.find_element(:css, "div[class*='viewSelectMenu']")}
  span = get_all_elements
  assert(span.length == 1, "Found more than 1 view")
  span[0].should include view_name
end

When /^I select view "([^"]*)"$/ do |view|
  @dropDownId = "viewSelectMenu"
  puts "@dropDownId = " + @dropDownId
  selectDropdownOption(@dropDownId, view)
end

Then /^I should see a table heading "([^"]*)"$/ do |text|
  list =  @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-hbox")}

  list.should_not be_nil

  list.text.should include text
end

def select_by_id(elem, select)
  @dropDownId = select + "Menu"
  selectDropdownOption(@dropDownId, elem)
end
