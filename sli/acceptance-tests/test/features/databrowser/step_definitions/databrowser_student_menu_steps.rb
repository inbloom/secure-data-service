require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

Given /^I go to Student Menu page$/ do
  @driver.find_element(:id, "navStudentMenu").click
  #assert(driver.current_url == @url+"/students/", "Failed to navigate to the Student Menu Page")
end

Then /^there should be Total Students "([^"]*)"$/ do |arg1|
  numStudents = @driver.find_element(:id, "numStudents").text
  assert(numSchools == arg1, "NumStudents found ("+numStudents+") differs from expected value of "+arg1+".")
end

Then /^I click List All Students button$/ do
  @driver.find_element(:id, "listStudents").click
end

When /^I enter the first name "([^"]*)"$/ do |arg1|
  @driver.find_element(:xpath, "//input[@id='firstname']").send_keys arg1
end

When /^I enter the last name "([^"]*)"$/ do |arg1|
  @driver.find_element(:xpath, "//input[@id='lastname']").send_keys arg1
end

When /^click the Add Student link$/ do
  @driver.find_element(:xpath, "//td[@class='action']/a[@onclick='add()']").click
end

When /^I update student "([^"]*)" with "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:xpath, "//td[@class='firstname']/input[@value='"+arg1+"']").send_keys arg2
  @driver.find_element(:xpath, "//input[@value='"+arg1+"']/../following-sibling::td/a[contains(@onclick, 'update_click')]").click
end

When /^I click the Delete Student link for "([^"]*)"$/ do |arg1|
  @driver.find_element(:xpath, "//input[@value='"+arg1+"']/../following-sibling::td/a[contains(@onclick, 'delete_click')]").click
end

Then /^"([^"]*)" should appear on the list student page$/ do |arg1|
  assert(@driver.page_source.include?(arg1), "Stduent "+arg1+" not found on page")
end

Then /^"([^"]*)" should not appear on the list student page$/ do |arg1|
    assert(@driver.page_source.index(arg1) == nil, "Student "+arg1+" was found on page in a negative test")
end

After do |scenario|
  @driver.quit if @driver
end