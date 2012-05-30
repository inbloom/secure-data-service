require_relative '../../../utils/sli_utils.rb'

Then /^I should have multiple filters available$/ do
  arr = get_all_elements
  arr.size.should be > 0
end

When /^I select filter "([^"]*)"$/ do |filter|
  select_by_id(filter, "filterSelect")
end

Then /^I should see a student named "([^"]*)"$/ do |student|
  list = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  
  list.should_not be_nil
  list.text.should include student
end

def get_all_elements
  options = @selector.find_element(:class, "dropdown-menu").find_elements(:tag_name, "li")
  arr = []
  options.each do |option|
    link = option.find_element(:tag_name, "a").attribute("text")
    arr << link
  end
  arr
end