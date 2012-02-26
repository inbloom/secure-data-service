require_relative '../../utils/sli_utils.rb'

Then /^I should have multiple filters available$/ do
  arr = get_all_elements
  arr.size.should be > 0
end

When /^I select filter "([^"]*)"$/ do |filter|
  select_by_id(filter, "studentFilterSelector")
  sleep(5)
end

Then /^I should see a student named "([^"]*)"$/ do |student|
  list = @driver.find_element(:id, "studentList")
  list.should_not be_nil
  list.text.should include student
end

def get_all_elements
  options = @selector.find_elements(:tag_name, "option")
  arr = []
  options.each do |option|
    arr << option.text
  end
  arr
end