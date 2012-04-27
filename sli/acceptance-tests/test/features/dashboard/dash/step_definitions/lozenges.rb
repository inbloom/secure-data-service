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
      foundGrade = getStudentAttribute(tr, column)
      tdsWithGrade = getStudentAttributeTd(tr, column)
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

Then /^the fuel gauge for "([^"]*)" in "([^"]*)" is "([^"]*)"$/ do |studentName, assessment, score|
  studentCell = getStudentCell(studentName)
  td = getStudentAttributeTd(studentCell, assessment)
  
  title = td.attribute("title")
  puts "widget info:" + title
  cutpoints = []
  #dashboardUtil.js
  colorCode = ["#eeeeee","#b40610", "#e58829","#dfc836", "#7fc124","#438746"]
  scoreValue = nil
  #Expected text in the form:
  #var cutpoints = new Array(6,15,21,28,33)
  if ( title =~ /cutPoints(.*)new Array\((\d+), (\d+), (\d+), (\d+), (\d+)/ )
    cutpoints[0] = $2
    cutpoints[1] = $3
    cutpoints[2] = $4
    cutpoints[3] = $5
    cutpoints[4] = $6
  end
  #Expected text in the form:
  # FuelGaugeWidget ('ISATWriting5', 1, cutpoints)
  if ( title =~ /FuelGaugeWidget \((.*), (\d+),/ )
    scoreValue = $2
  end
  
  assert(score == scoreValue, "Expected: " + score + " but found: " + scoreValue)
  
  rects = td.find_elements(:tag_name,"rect")
  # use the 2nd rect
  filledPercentage = rects[1].attribute("width")
  color = rects[1].attribute("fill")
  index = 0
  
  cutpoints.each do |cutPoint|
    if (cutPoint.to_i <= score.to_i)
         index += 1
     else
      break;
    end
  end
  # we need to look at the previous index count to get the color
  colorIndex = 0
  if (index > 0 )
    colorIndex = index 
  end
  assert(color == colorCode[colorIndex], "Actual Color: " + color + " Expected Color: " + colorCode[colorIndex] + " at index " + index.to_s)
  
  expectedMaxPercentage = (index.to_f/4)*100
  expectedMinPercentage = 0
  if (index > 0)
    expectedMinPercentage = ((index-1).to_f/4)*100
  end
 
  puts "expected percentage range: " + expectedMinPercentage.to_s + " to " + expectedMaxPercentage.to_s + " Actual: " + filledPercentage
  assert((expectedMinPercentage <= filledPercentage.to_f && expectedMaxPercentage >= filledPercentage.to_f), "Actual Fuel Gauge Percentage is not within range")
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
    value = getStudentAttribute(tr, returnedName)
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
    value = getStudentAttribute(tr, returnedName)
    assert(value == unsorted[i].to_s, "sort order is wrong")
    i +=1
  end     
end

### This is all LOS code ########
## TODO: move this somewhere
# This will return all the TRs of the student grid, 1 for each student
def getStudentGrid()
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  all_trs = studentTable.find_elements(:xpath,".//tr[contains(@class,'ui-widget-content')]")
  return all_trs
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

def getStudentAttributeTd(studentTr,attribute)
  assert(!studentTr.nil?, "Student Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  td = studentTr.find_element(:xpath, searchText)
  return td
end

def getStudentAttributeTds(studentTr,attribute)
  assert(!studentTr.nil?, "Student Row is empty")
  searchText = "td[contains(@aria-describedby,'" + attribute + "')]"
  tds = studentTr.find_elements(:xpath, searchText)
  return tds
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
#TODO call studentAttribute inside here
def getStudentName(studentTr)
  return getStudentAttribute(studentTr, getStudentColumnName())
end

def getStudentProgramParticipation(studentTr)
  return getStudentAttributes(studentTr, getStudentProgramParticipationColumnName())
end

#returns an array of grades
def getStudentGrades(studentTr)
  return getStudentAttributes(studentTr, getGradeColumnName())
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

