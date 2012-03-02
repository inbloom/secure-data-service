require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../dashboard/step_definitions/selenium_common.rb'

Given /^I am authenticated to SLI as "([^"]*)" "([^"]*)"$/ do |user, pass|
  url = PropLoader.getProps['dashboard_server_address']
  url = url + PropLoader.getProps[@appPrefix]
  
  #url = "http://localhost:8080/dashboard"
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

When /^I select <viewSelector> "([^"]*)"$/ do |view|
  select_by_id(view, "viewSelector")
  sleep(5)
end

When /^the view configuration file set "([^"]*)" is "([^"]*)"$/ do |arg1, arg2|
  #do nothing, view configuration has been setup
end

Then /^I should see a table heading "([^"]*)"$/ do |text|
  assert(tableHeaderContains(text))
end

def select_by_id(elem, select)
  Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, select)).
      select_by(:text, elem)
rescue Selenium::WebDriver::Error::NoSuchElementError
  false
end

