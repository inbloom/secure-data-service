require 'selenium-webdriver'

Then /^the fuel gauge label for the assessment "([^"]*)" and student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|

  id = arg1 + "." + arg2 + ".label"
  element = @driver.find_element(:id, id)
  label = element.text
  assert(label == arg3, "Fuel gauge label is: " + label + ", expected " + arg3)
end
