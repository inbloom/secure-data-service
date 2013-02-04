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


require "selenium-webdriver"
require 'json'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

When /^I hit the Admin Application Authorization Tool$/ do
  #XXX - Once the API is ready, remove the ID
  @driver.get(PropLoader.getProps['admintools_server_url']+"/application_authorizations/")
end

Then /^I am redirected to the Admin Application Authorization Tool$/ do
  assertWithWait("Failed to navigate to the Admintools App Registration page")  {@driver.page_source.index("application_authorizations") != nil}
end

Then /^I see a label in the middle "([^"]*)"/ do |arg1|
  #We're changing how the ID is referenced, so the label for the time-being isn't going to be accurate
  #assert(@driver.page_source.index(arg1) != nil)
end

Then /^I see the list of all available apps on SLI$/ do
  @appsTable = @driver.find_element(:class, "AuthorizedAppsTable")
  assert(@appsTable != nil  )
end

Then /^the authorized apps for my district are colored green$/ do
  approved = @appsTable.find_elements(:xpath, ".//tbody/tr/td[text()='Approved']")
  approved.each do |currentStatus|
    assert(currentStatus.attribute(:id) == "approvedStatus", "App is not the right color, should be green")
  end
end

Then /^the unauthorized are colored red$/ do
  notApproved = @appsTable.find_elements(:xpath, ".//tbody/tr/td[text()='Not Approved']")
  notApproved.each do |currentStatus|
    assert(currentStatus.attribute(:id) == "notApprovedStatus", "App is not the right color, should be red")
  end
end

Then /^are sorted by 'Status'$/ do
  tableHeadings = @appsTable.find_elements(:xpath, ".//thead/tr/th")
  index = 0
  tableHeadings.each do |arg|
    index = tableHeadings.index(arg) + 1 if arg.text == "Status"    
  end
  rows = @appsTable.find_elements(:xpath, ".//tbody/tr")
  inApprovedSection = true
  rows.each do |curRow| 
    td = curRow.find_element(:xpath, "//td[#{index}]")
    assert(inApprovedSection || (!inApprovedSection && td.text != "Approved"), "Encountered an app with a 'Approved' status after one with a 'Not Approved' status")
    if td.text == "Not Approved"
      inApprovedSection = false
    end
  end
end

Then /^I see the Name, Version, Vendor and Status of the apps$/ do
  expectedHeadings = ["Name", "Version", "Vendor", "Status", ""]
  tableHeadings = @appsTable.find_elements(:xpath, ".//tr/th")
  actualHeadings = []
  tableHeadings.each do |heading|
    if (heading.text.index("District") != 0)
      #The first th will contain the district's name
      actualHeadings.push(heading.text)
    end
  end    
  assert(expectedHeadings.sort == actualHeadings.sort, "Headings are different, found #{actualHeadings.inspect} but expected #{expectedHeadings.inspect}")
end

Given /^I am a valid SEA\/LEA user$/ do
end

When /^I hit the Application Authorization Tool$/ do
  pending # express the regexp above with the code you wish you had
end

When /^the login process is initiated$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I pass my valid username and password$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I get message that I am not authorized$/ do
  isForbidden = @driver.find_element(:xpath, '//title[text()="Not Authorized (403)"]')
  assert(isForbidden != nil)
end

Then /^I do not get message that I am not authorized$/ do
  isForbidden = nil
  begin
    isForbidden = @driver.find_element(:xpath, '//title[text()="Not Authorized (403)"]')
  rescue Exception => e
    #expected
    assert(isForbidden == nil)
  else
    assert(isForbidden == nil)
  end
end

Then /^I am not logged into the application$/ do
  step "I hit the Admin Application Authorization Tool"
end


Given /^I am logged into the Application Authorization Tool$/ do
end

Given /^I see an application "([^"]*)" in the table$/ do |arg1|
  @appName = arg1
  apps = @driver.find_elements(:xpath, ".//tbody/tr/td[text()='#{arg1}']/..")
  apps.each do |cur|
    puts("The app is #{cur.inspect} and #{cur.text}")
  end
  assert(apps != nil)
end

Given /^in Status it says "([^"]*)"$/ do |arg1|
  statusIndex = 4
  
  rows = @driver.find_elements(:xpath, ".//tbody/tr/td[text()='#{@appName}']/..")
  rows.each do |curRow|
    if curRow.displayed?
      @appRow = curRow
    end
  end
  actualStatus = @appRow.find_element(:xpath, ".//td[#{statusIndex}]").text
  assert(actualStatus == arg1, "Expected status of #{@appName} to be #{arg1} instead it's #{actualStatus.inspect}")
end

Given /^I click on the "([^"]*)" button next to it$/ do |arg1|
  inputs = @appRow.find_elements(:xpath, ".//td/form/input")
  inputs.each do |cur|
    if cur.attribute(:value) == arg1
      cur.click
      break
    end
  end
end

Given /^I am asked 'Do you really want this application to access the district's data'$/ do
      begin
        @driver.switch_to.alert
      rescue
      end
end

Then /^the application is authorized to use data of "([^"]*)"$/ do |arg1|
  row = @driver.find_element(:xpath, ".//tbody/tr/td[text()='#{@appName}']/..")
  assert(row != nil)
end

Then /^is put on the top of the table$/ do
  rows = @driver.find_elements(:xpath, ".//tbody/tr/td/..")
  rows.each do |curRow|
    if curRow.displayed?
      @row = curRow
      break
    end
  end
  assert(@row.find_element(:xpath, ".//td[1]").text == @appName, "The approved application should have moved to the top")
end

Then /^the Status becomes "([^"]*)"$/ do |arg1|
  assertWithWait("Status should have switched to #{arg1}"){  @row.find_element(:xpath, ".//td[4]").text == arg1} 
end

Then /^it is colored "([^"]*)"$/ do |arg1|
  status = @row.find_element(:xpath, ".//td[4]")
  if arg1 == "green"
    assert(status.attribute(:id) == "approvedStatus", "Should be colored green, instead ID is #{status.attribute(:id)}")
  elsif arg1 == "red"
    assert(status.attribute(:id) == "notApprovedStatus", "Should be colored red, instead ID is #{status.attribute(:id)}")
  end
end

Then /^the Approve button next to it is disabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Approve"
      assert(input.attribute(:disabled) == "true", "Approve button should be disabled")
    end
  end
end

Then /^the Deny button next to it is enabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Deny"
      assert(input.attribute(:disabled) != "true", "Deny button should be enabled")
    end
  end
end

Given /^in Status it says Approved$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am asked 'Do you really want deny access to this application of the district's data'$/ do
      begin
        @driver.switch_to.alert
      rescue
      end
end

Then /^the application is denied to use data of "([^"]*)"$/ do |arg1|
  row = @driver.find_element(:xpath, ".//tbody/tr/td[text()='#{@appName}']/..")
  assert(row != nil)
end

Then /^it is put on the bottom of the table$/ do
  @row = @driver.find_element(:xpath, ".//tbody/tr/td[text()='#{@appName}']/..")
end

Then /^the Approve button next to it is enabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Approve"
      assert(input.attribute(:disabled) != "true", "Approve button should be enabled")
    end
  end
end

Then /^the Deny button next to it is disabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Deny"
      assert(input.attribute(:disabled) == "true", "Deny button should be disabled")
    end
  end
end

Given /^I am an authenticated end user \(Educator\) from <district>$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^the Data Browser is denied access for <district>$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I try to access any resource through the DB \(even the home\-link\) page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am a valid District Super Administrator for "([^"]*)"$/ do |arg1|
  #No code needed
end

Given /^I am an authenticated District Super Administrator for "([^"]*)"$/ do |arg1|
  step "I have an open web browser"
  step "I hit the Admin Application Authorization Tool"
  step "I was redirected to the \"Simple\" IDP Login page"
  step "I submit the credentials \"sunsetadmin\" \"sunsetadmin1234\" for the \"Simple\" login page"
  step "I am redirected to the Admin Application Authorization Tool"
end

Given /^I am an authenticated end user "" from "([^"]*)"$/ do |arg1|
    @driver.get(PropLoader.getProps['databrowser_server_url'])
end


