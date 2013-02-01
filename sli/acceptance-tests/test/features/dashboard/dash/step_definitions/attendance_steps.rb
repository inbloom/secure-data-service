=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

##########################
# Attendance history panel
##########################
When /^the Attendance History in grid "(.*?)" has the following entries:$/ do |gridNumber, table|
  panel = getPanel("Attendance and Discipline", "Attendance History")
  
  #headers
  @attendanceMapping = {
    "Term" => "term",
    "School" => "schoolName",
    "Grade Level" => "gradeLevel",
    "% Present" => "present",
    "Total Absences" => "totalAbsencesCount",
    "Excused" => "excusedAbsenceCount",
    "Unexcused" => "unexcusedAbsenceCount",
    "Tardy" => "tardyCount" 
  }   
  checkGridEntries(panel, table, @attendanceMapping, true, gridNumber)
end

When /^the Attendance column "(.*?)" is of style "(.*?)"$/ do |columnName, styleName|
  panel = getPanel("Attendance and Discipline", "Attendance History")
  gridHeaders = getGridHeaders(panel)
  headerCell = getHeaderCellBasedOnId(gridHeaders, @attendanceMapping[columnName])
  # Check the header has the style class
  headerClass = headerCell.attribute("class")
  assert((headerClass.include? styleName), "Actual class of header: #{headerClass}")
  
  grid = getGrid(panel)
  # For each row, check that the style has been applied
  grid.each do |tr|  
    td = getTdBasedOnAttribute(tr, @attendanceMapping[columnName])
    contentClass = td.attribute("class")
    assert((contentClass.include? styleName), "Actual class of content: #{contentClass}")
  end
end
#####################
# Attendance Calendar
#####################
When /^I see the Attendance Calendar$/ do
  calendar = @currentTab.find_element(:css, "div[class*='attendanceCalendar']")
  @allMonths = calendar.find_elements(:css, "div[class*='ui-datepicker-group']")
  assert(calendar != nil && @allMonths.length == 12, "Attendance Calendar is not found")
end

When /^calendar month number "(.*?)" is "(.*?)" of "(.*?)"$/ do |calendarPosition, month, year|
  calendarPosition= calendarPosition.to_i - 1
  actualMonth = @allMonths[calendarPosition].find_element(:class, "ui-datepicker-month").text
  actualYear = @allMonths[calendarPosition].find_element(:class, "ui-datepicker-year").text
  assert(month == actualMonth, "Expected: #{month} Actual: #{actualMonth}")
  assert(year == actualYear, "Expected #{year} Actual: #{actualYear}")
end

When /^in calendar month number "(.*?)" in day "(.*?)" is "(.*?)" with event "(.*?)"$/ do |calendarPosition, day, attendanceCategory, eventReason|
  verifyAttendanceCategory(calendarPosition, day, attendanceCategory, eventReason)
end

When /^in calendar month number "(.*?)" in day "(.*?)" is "(.*?)" with no events$/ do |calendarPosition, day, attendanceCategory|
  verifyAttendanceCategory(calendarPosition, day, attendanceCategory)
end

When /^the following are the total attendance number shown in the calendar$/ do |table|
  getAttendanceCategoryTotalFromCalendar()
  table.hashes.each do |row|
    expectedCategory = AbsenceCategory.getType(row["Attendance Category"])
    expectedCount = row["Count"].to_i
    assert(@attendanceCount[expectedCategory] == expectedCount, "For #{expectedCategory} Expected #{expectedCount}, Actual #{@attendanceCount[expectedCategory]}")
  end
end

def verifyAttendanceCategory(calendarPosition, day, attendanceCategory, eventReason = nil)
  expectedTitle = AbsenceCategory.getType(attendanceCategory)
  calendarPosition= calendarPosition.to_i - 1
  all_tds = @allMonths[calendarPosition].find_element(:class, "ui-datepicker-calendar").find_elements(:tag_name, "td")
  all_tds.each do |td|
    if (td.text == day)
      title = td.attribute('title')
      tdClass = td.attribute('class')
      assert((tdClass.include? expectedTitle), "Expected: #{expectedTitle} Actual Class: #{tdClass}")
      if (eventReason != nil)
        assert(title == eventReason, "Expected: #{eventReason} Actual Class: #{title}")
      else
        assert(title == "")
      end
      break
    end
  end
end

def getAttendanceCategoryTotalFromCalendar()
  @attendanceCount = Hash.new 
  @attendanceCount[AbsenceCategory::UNEXCUSEDABSENCE] = 0
  @attendanceCount[AbsenceCategory::EXCUSEDABSENCE] = 0
  @attendanceCount[AbsenceCategory::TARDY] = 0
  @allMonths.each do |month|
    tds = month.find_element(:class, "ui-datepicker-calendar").find_elements(:tag_name, "td")
    tds.each do |td|
      tdclass = td.attribute('class')
      if (tdclass.include? AbsenceCategory::UNEXCUSEDABSENCE)
        @attendanceCount[AbsenceCategory::UNEXCUSEDABSENCE] += 1
      elsif (tdclass.include? AbsenceCategory::EXCUSEDABSENCE)
         @attendanceCount[AbsenceCategory::EXCUSEDABSENCE] += 1
      elsif (tdclass.include? AbsenceCategory::TARDY)
        @attendanceCount[AbsenceCategory::TARDY] += 1
      end
    end
  end
end

module AbsenceCategory
  INATTENDANCE = ""
  EXCUSEDABSENCE = "ExcusedAbsence"
  UNEXCUSEDABSENCE = "UnexcusedAbsence"
  TARDY = "Tardy"
  NONSCHOOLDAY = "disableDays"
  
  def AbsenceCategory.getType(name)
    name.downcase!
    if (name == "in attendance")
      return AbsenceCategory::INATTENDANCE
    elsif (name == "excused absence")
      return AbsenceCategory::EXCUSEDABSENCE
    elsif (name == "unexcused absence")
      return AbsenceCategory::UNEXCUSEDABSENCE
    elsif (name == "tardy")
      return AbsenceCategory::TARDY
    elsif (name == "non school day")
      return AbsenceCategory::NONSCHOOLDAY
    end
  end
end

