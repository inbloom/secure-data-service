require_relative '../../utils/sli_utils.rb'

Given /^I am authenticated to SLI as "([^"]*)" "([^"]*)"$/ do |user, pass|
  #url = PropLoader.getProps['devdash1']
  #url = url + PropLoader.getProps[@appPrefix]
  
  url = "http://localhost:8080/dashboard"
  @driver.get(url)
  @driver.manage.timeouts.implicit_wait = 30
  @driver.find_element(:name, "j_username").clear
  @driver.find_element(:name, "j_username").send_keys user
  @driver.find_element(:name, "j_password").clear
  @driver.find_element(:name, "j_password").send_keys pass
  @driver.find_element(:name, "submit").click
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

Then /^I should have a dropdown selector named "([^"]*)"$/ do |elem|
  @selector = @driver.find_element(:id, elem)
end

Then /^I should have a selectable view named "([^"]*)"$/ do |view_name|
  options = @selector.find_elements(:tag_name, "option")
  arr = []
  options.each do |option|
    arr << option.text
  end
  arr.should include view_name
end

Then /^I should only see one view$/ do 
  pending
end

Then /^the view should be named "([^"]*)"$/ do |view_name|
  pending
end

When /^I select view "([^"]*)"$/ do |view|
  select_by_id(view, "viewSelector")
  sleep(1)
end

Then /^I should see a table heading "([^"]*)"$/ do |text|
  list = @driver.find_element(:id, "studentList")
  list.should_not be_nil

  list.text.should include text
end

def select_by_id(elem, select)
  Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, select)).
      select_by(:text, elem)
rescue Selenium::WebDriver::Error::NoSuchElementError
  false
end
