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

Then /^the grades teardrop color widgets for "([^"]*)" are mapped correctly:$/ do |gradeColumns, table|
  gradeColumnsArray = gradeColumns.split(';')
  gradeMapping = Hash.new 
  table.hashes.each do |row|
    key = row['grade'].rstrip.lstrip
    value = row['teardrop'].rstrip.lstrip
    gradeMapping[key] = value
  end
  all_trs = getStudentGrid()
   
  all_trs.each do |tr|
    gradeColumnsArray.each do |column|
      foundGrade = getAttribute(tr, column)
      tdsWithGrade = getTdBasedOnAttribute(tr, column)
      if (gradeMapping[foundGrade] == nil)
          puts "Grade Mapping doesn't exist for grade: " + grade
      else
          searchText = ".//div[contains(@class,'" + gradeMapping[foundGrade] + "')]"
          tearDrop = tdsWithGrade.find_element(:xpath, searchText)
          assert(tearDrop != nil, "Expected color" + gradeMapping[foundGrade])
      end
    end
  end 
end

Then /^I click on "([^"]*)" header to sort a string column in "([^"]*)" order$/ do |columnName, order|
  sortColumn(columnName, false, isAscendingOrder(order))
end

Then /^I click on "([^"]*)" header to sort an integer column in "([^"]*)" order$/ do |columnName, order|
  sortColumn(columnName, true, isAscendingOrder(order))
end

def isAscendingOrder(order)
  return (order.downcase == "ascending")
end

def sortColumn(columnName, isInt, isAscending)
  hTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-htable")}
  returnedName = getColumnLookupName(columnName)
  searchText = ".//div[contains(@id,'" + returnedName + "')]"
  column = hTable.find_element(:xpath, searchText)

  all_trs = getStudentGrid()
  
  unsorted = []
  all_trs.each do |tr|
    value = getAttribute(tr, returnedName)
    if (isInt)
      value = value.to_i
    end
    unsorted = unsorted + [value]
  end

  #caveat:  there's a Bug on Student Column, it only happens on first load, and if Student is the first column to be sorted
  column.click
  if (!isAscending)
    column.click
    unsorted = unsorted.sort.reverse
  else
    unsorted = unsorted.sort   
  end

  all_trs = getStudentGrid()
  i  = 0
  all_trs.each do |tr|
    value = getAttribute(tr, returnedName)
    assert(value == unsorted[i].to_s, "sort order is wrong")
    i +=1
  end     
end

# This will return all the TRs of the a grid, 1 for each student
def getStudentGrid()
   return getGrid(@driver)
end

# This will give the tr of the student in los
def getStudentCell (student_name)
  all_trs = getStudentGrid()

  studentCell = nil
  all_trs.each do |tr|
    if tr.attribute("innerHTML").to_s.include?(student_name)
      studentCell = tr
    end
  end  

  return studentCell
end

#TODO call studentAttribute inside here
def getStudentName(studentTr)
  return getAttribute(studentTr, getStudentColumnName())
end

def getStudentProgramParticipation(studentTr)
  return getAttributes(studentTr, getStudentProgramParticipationColumnName())
end

#returns an array of grades
def getStudentGrades(studentTr)
  return getAttributes(studentTr, getGradeColumnName())
end


def getColumnLookupName(headerName)
  headerName.downcase!
  if (headerName == "student")
    return getStudentColumnName()
  elsif (headerName == "absence count")
    return getAbsenceCountColumnName()
  else
    assert(true, "unknown header name: " + headerName)
  end
end

def getStudentColumnName()
  return "fullName"
end

def getStudentProgramParticipationColumnName()
  return "programParticipation"
end

def getAbsenceCountColumnName()
  return "absenceCount"
end

