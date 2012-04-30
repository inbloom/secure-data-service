When /^I enter "([^"]*)" into the "([^"]*)" search box$/ do |query, textboxName|
  @explicitWait.until{@driver.find_element(:class,"student-search-form")}
  putTextToField(query, textboxName, "name")
end

When /^I click the search button$/ do
  searchSection = @driver.find_element(:class, "student-search-form")
  searchSection.find_element(:class,"btn").click
end

Then /^"([^"]*)" results are returned$/ do |numResults|
  assert(@driver.current_url.include?("studentSearchPage"), "Current URL doesn't seem to be the expected search results page: " + @driver.current_url)
  grid = getStudentGrid()
  assert(numResults.to_i == grid.length, "Expected number of results: " + numResults + " Actual: " + grid.length.to_s)
end

When /^I enter nothing into either field of student search$/ do
  putTextToField("", "firstName", "name")
  putTextToField("", "lastName", "name")
end

Then /^the search results include:$/ do |table|
  table.hashes.each do |row|
    student = row['Student']
    grade = row['Grade']
    school = row['School']
    
    #caveat:  if the class has duplicate student names
    tr = getStudentCell(student)
    actualStudentName = getStudentName(tr)
    actualStudentCurrentSchool = getStudentAttribute(tr, getCurrentSchoolColumnName())
    actualStudentGradeLevel = getStudentAttribute(tr, getGradeLevelColumnName())
    
    assert(actualStudentName == student, "Expected Student Name: " + student + " Actual Name: " + actualStudentName)
    assert(actualStudentCurrentSchool == school, "Expected School: " + school + " Actual Name: " + actualStudentCurrentSchool)
    assert(actualStudentGradeLevel == grade, "Expected Grade: " + grade + " Actual Name: " + actualStudentGradeLevel)
  end
end

When /^I send the enter key$/ do
  # This only sends the enter key to the last name text box
  @driver.find_element(:name, "lastName").send_keys(:enter)
end

def getGradeLevelColumnName()
  return "gradeLevel"
end

def getCurrentSchoolColumnName()
  return "currentSchoolName"
end