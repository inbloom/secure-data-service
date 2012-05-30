require_relative '../../../utils/sli_utils.rb'

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

Then /^I should have a dropdown selector named "([^"]*)"$/ do |elem|
  elem += "Menu"
  @selector = @explicitWait.until{@driver.find_element(:id, elem)}
end

Then /^I should have a selectable view named "([^"]*)"$/ do |view_name|
  options = @selector.find_element(:class, "dropdown-menu").find_elements(:tag_name, "li")
  puts options.length
  arr = []
  options.each do |option|
    link = option.find_element(:tag_name, "a").attribute("text")
    arr << link
  end
  arr.should include view_name
end

Then /^I should only see one view named "([^"]*)"$/ do |view_name|
  @selector = @explicitWait.until{@driver.find_element(:id, "viewSelectMenu")}
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
