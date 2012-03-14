require_relative '../../../utils/sli_utils.rb'
require "selenium-webdriver"

Then /^the count for id "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  id = arg2 << "." << arg1
  element = @driver.find_element(:id, id)
  label = element.text
  assert(label == arg3, "Count : " + label + ", expected " + arg3)

end

Then /^the class for id "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  id = arg2 << "." << arg1
  element = @driver.find_element(:id, id)
  cssClass = element.attribute("class")
  subElement = element.find_element(:class, arg3)
  assert(subElement != nil, "Expected color " + arg3)
end
