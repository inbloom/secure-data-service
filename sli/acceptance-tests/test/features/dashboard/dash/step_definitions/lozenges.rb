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

Then /^I check the student list for grade "([^"]*)" is mapped to "([^"]*)"$/ do |grade, classId|
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  all_trs = studentTable.find_elements(:xpath,".//tr[contains(@class,'ui-widget-content')]")
  searchText = ".//div[contains(@class,'" + classId + "')]"
  i = 0
  all_trs.each do |tr|
    foundGrade = getStudentGrade(tr)
    name = getStudentName(tr)
    if (grade == foundGrade)
      i += 1
      tearDrop = tr.find_element(:xpath, searchText)
      assert(tearDrop != nil, "Expected color" + classId)
    end
  end 
  puts i.to_s + " students have grade: " + grade
end

Then /^the fuel gauge for "([^"]*)" in "([^"]*)" is "([^"]*)"$/ do |studentName, assessment, score|
  studentCell = getStudentCell(studentName)
  td = getStudentAttributeTd(studentCell, assessment)
  
  title = td.attribute("title")
  puts "widget info:" + title
  cutpoints = []
  #fuelGaugeWidget.js
  colorCode = ["#b40610", "#e58829","#dfc836", "#7fc124","#438746"]
  scoreValue = nil
  #Expected text in the form:
  #var cutpoints = new Array(6,15,21,28,33)
  if ( title =~ /cutpoints(.*)new Array\((\d+),(\d+),(\d+),(\d+),(\d+)/ )
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
  
  isCutPointValue = false
  cutpoints.each do |cutpoint|
    if (scoreValue == cutpoint)
      isCutPointValue = true     
    end  
  end 
  
  rects = td.find_elements(:tag_name,"rect")
  # use the 2nd rect
  filledPercentage = rects[1].attribute("width")
  color = rects[1].attribute("fill")
  index = 0
  cutpoints.each do |cutPoint|
    if (cutPoint.to_i < score.to_i)
         index += 1
     else
      break;
    end
  end
  # we need to look at the previous index count to get the color
  colorIndex = 0
  if (index > 0 )
    colorIndex = index -1
  end
  assert(color == colorCode[colorIndex], "Actual Color: " + color + " Expected Color: " + colorCode[colorIndex] + " at index " + index.to_s)
  
  expectedMaxPercentage = (index.to_f/4)*100
  expectedMinPercentage = 0
  if (index > 0)
    expectedMinPercentage = ((index-1).to_f/4)*100
  end
 
  if (!isCutPointValue || index == 0)
    puts "expected percentage range: " + expectedMinPercentage.to_s + " to " + expectedMaxPercentage.to_s + " Actual: " + filledPercentage
    assert((expectedMinPercentage <= filledPercentage.to_f && expectedMaxPercentage >= filledPercentage.to_f), "Actual Fuel Gauge Percentage is not within range")
  else
    found = false
    #ensure it's at at end of border
    for i in (2..cutpoints.length-1)
      xposition = rects[i].attribute("x")
      if (xposition == filledPercentage)
        puts "Score is at a cutpoint, percentage is: " + filledPercentage
        found = true
      end
    end
    assert(found, "Fuel Gauge Percentage seems to be wrong")
  end
end

### This is all LOS code ########
## TODO: move this somewhere

# This will give the tr of the student in los
def getStudentCell (student_name)
  studentTable = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  all_trs = studentTable.find_elements(:xpath,".//tr[contains(@class,'ui-widget-content')]")

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
  return getStudentAttribute(studentTr, "fullName")
end

def getStudentProgramParticipation(studentTr)
  return getStudentAttributes(studentTr, "programParticipation")
end

def getStudentGrade(studentTr)
  return getStudentAttribute(studentTr, "grade")
end
