require_relative '../../../utils/sli_utils.rb'
require "selenium-webdriver"

Then /^the scale score for assessment "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  studentCell = getStudentCell(arg2)
  label = getStudentAttribute(studentCell,"assessments."+arg1+".Scale score")
  assert(label == arg3, "Score : " + label + ", expected " + arg3)
  
end