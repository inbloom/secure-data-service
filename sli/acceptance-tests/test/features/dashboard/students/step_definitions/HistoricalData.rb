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
require_relative '../../dash/step_definitions/selenium_common_dash.rb'

Given /^I am authenticated to SLI as "([^"]*)" "([^"]*)"$/ do |user, pass|
  url = PropLoader.getProps['api_server_url']
  # url = PropLoader.getProps['dashboard_server_address']
  url = url + PropLoader.getProps[@appPrefix]
  
  @driver.get(url)
  @driver.manage.timeouts.implicit_wait = 5
  
  Selenium::WebDriver::Support::Select.new(@driver.find_element(:name, "realmId")).select_by(:text, "inBloom")
  @driver.find_element(:id, "go").click
  @driver.find_element(:id, "IDToken1").clear
  @driver.find_element(:id, "IDToken1").send_keys user
  @driver.find_element(:id, "IDToken2").clear
  @driver.find_element(:id, "IDToken2").send_keys pass
  @driver.find_element(:name, "Login.Submit").click
end

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
end

When /^I select <viewSelect> "([^"]*)"$/ do |view|
  select_by_id(view, "viewSelect")
end

Then /^I should see a table heading "([^"]*)"$/ do |listHeaderName|
  listHeader = @driver.find_element(:id, "listHeader."+listHeaderName)
  listHeader.should_not be_nil

  listHeader.text.should == listHeaderName
  @headerName = listHeaderName
end

Then /^I should see a field "([^"]*)" in this table$/ do |fieldName|
  field = @driver.find_element(:id, "listHeader."+@headerName+"."+fieldName)
  field.should_not be_nil  
  field.text.should == fieldName
end

Then /^I should see "([^"]*)" in student field$/ do |studentName|
  student = @driver.find_element(:id, studentName+".name")
  student.text.should include studentName
  @studentName = studentName
end

Then /^I should see his\/her "([^"]*)" course grade is "([^"]*)" in this table$/ do |courseTitle, gradeResult|  
  course = @driver.find_element(:id, @studentName+".Course.title."+@headerName.gsub(/\s+/, ""))
  course.should_not be_nil
  course.text.should == courseTitle
  
  grade = @driver.find_element(:id, @studentName+".Course.grade."+@headerName.gsub(/\s+/, ""))
  grade.should_not be_nil
  grade.text.should == gradeResult
end
  
def select_by_id(elem, select)
  Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, select)).select_by(:text, elem)
rescue Selenium::WebDriver::Error::NoSuchElementError
  false
end

When /^I go to the old dashboard page$/ do
  url = getBaseUrl() + "/studentlist"
  @driver.get url
end
