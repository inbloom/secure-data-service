Then /^the lozenge for student "([^"]*)" include "([^"]*)"$/ do |student_name, lozenge_label|

  # first, find the table cell for that student. 
  studentCell = getStudentCell(student_name)
  assert(!studentCell.nil?, "Student '" + student_name + "' was not found in student list table.")
  
  lozenges = getStudentProgramParticipation(studentCell)

  # Then, make sure the lozenge label is as expected
  labelFound = false
  lozenges.each do |lozenge|
    if lozenge.to_s.include?(lozenge_label)
      labelFound = true
    end
  end
  
#   Apparently Selenium doesn't handle svg elements. 
#     lozengeLabels = lozengeSpan.find_elements(:xpath, "./svg//tspan");
#     lozengeLabels.each do |lozengeLabel|
#       puts lozengeLabel.attribute("innerHTML").to_s
#     end

  assert(labelFound, "Lozenge '" + lozenge_label + "' was not found for the student " + student_name);

end


Then /^there is no lozenges for student "([^"]*)"$/ do |student_name|

  # first, find the table cell for that student. 
  studentCell = getStudentCell(student_name)
  assert(!studentCell.nil?, "Student '" + student_name + "' was not found in student list table.")

  # Then, make sure there is no lozenges array is empty
  lozenges = getStudentProgramParticipation(studentCell)
  assert(lozenges.length == 0, "Student " + student_name + " has lozenges")
end

# This will give the tr of the student in los
def getStudentCell (student_name)
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  all_tds = studentTable.find_elements(:xpath,".//tr[contains(@class,'ui-widget-content')]")

  studentCell = nil
  all_tds.each do |td|
    if td.attribute("innerHTML").to_s.include?(student_name)
      studentCell = td
    end
  end  

  return studentCell
end

def getStudentAttributeTd(studentTr,attribute)
  assert(!studentTr.nil?, "Student Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  td = studentTr.find_element(:xpath, searchText)
  return td
end

def getStudentAttribute(studentTr, attribute)
  assert(!studentTr.nil?, "Student Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  value = studentTr.find_element(:xpath, searchText)
  return value.text
end

def getStudentAttributes(studentTr, attribute)
  assert(!studentTr.nil?, "Student Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  values = []
  i = 0
  elements = studentTr.find_elements(:xpath, searchText)
  elements.each do |element|
    if (element.text.length > 0)
      values[i] = element.text
      i += 1
    end
  end
  return values
end
def getStudentName(studentTr)
  return getStudentAttribute(studentTr, "fullName")
end

def getStudentProgramParticipation(studentTr)
  return getStudentAttributes(studentTr, "programParticipation")
end

