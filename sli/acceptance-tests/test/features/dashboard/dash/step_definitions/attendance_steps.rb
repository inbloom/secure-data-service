=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require_relative '../../../utils/sli_utils.rb'
require "selenium-webdriver"

Then /^the count for id "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  studentCell = getStudentCell(arg2)
  label = getAttribute(studentCell,arg1)
  assert(label == arg3, "Count : " + label + ", expected " + arg3)  
end

Then /^the class for id "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  studentCell = getStudentCell(arg2)
  element = getTdBasedOnAttribute(studentCell, arg1)
  subElement = element.find_element(:class, arg3)
  assert(subElement != nil, "Expected color" + arg3)
end

# Attendance history panel
When /^the Attendance History in grid "(.*?)" has the following entries:$/ do |gridNumber, table|
  panel = getPanel("Attendance and Discipline", "Attendance History")
  
  #headers
  mapping = {
    "Term" => "term",
    "School" => "schoolName",
    "Grade Level" => "gradeLevel",
    "% Present" => "present",
    "Total Absences" => "totalAbsencesCount",
    "Excused" => "excusedAbsenceCount",
    "Unexcused" => "unexcusedAbsenceCount",
    "Tardy" => "tardyCount" 
  }   
  checkGridEntries(panel, table, mapping, true, gridNumber)
end