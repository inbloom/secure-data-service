require_relative '../../../utils/sli_utils.rb'
require "selenium-webdriver"

Then /^the count for id "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  studentCell = getStudentCell(arg2)
  label = getStudentAttribute(studentCell,arg1)
  assert(label == arg3, "Count : " + label + ", expected " + arg3)
  
end

Then /^the class for id "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  studentCell = getStudentCell(arg2)
  element = getStudentAttributeTd(studentCell, arg1)
  subElement = element.find_element(:class, arg3)
  assert(subElement != nil, "Expected color" + arg3)
end
