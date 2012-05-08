require 'selenium-webdriver'

When /^I click on student "([^"]*)"$/ do |name|
  clickOnStudent(name)
end

When /^I click on student at index "([^"]*)"$/ do |studentIndex|
  clickOnStudentAtIndex(studentIndex)
end

When /^I view its student profile$/ do

  csiContent = @explicitWait.until{@driver.find_element(:class, "csi")}
  studentInfo = csiContent.find_element(:class, "studentInfo")
  table_cells = studentInfo.find_elements(:xpath, "//div[@class='field']/span")
  @info = Hash.new
  sName = csiContent.find_element(:xpath, "//div[@class='colMain']/h1") 

  @info["Name"] = sName.text
  puts sName.text
  
for i in 0..table_cells.length-1
  if table_cells[i].text.length > 0 
    puts table_cells[i].text
      if i % 2 == 0
        key = table_cells[i].text
      else
        @info[key]= table_cells[i].text
      end
   end
 end
end

Then /^their name shown in profile is "([^"]*)"$/ do |expectedStudentName|
   containsName = @info["Name"] == expectedStudentName
   assert(containsName, "Actual name is :" + @info["Name"]) 
end

Then /^their id shown in proflie is "([^"]*)"$/ do |studentId|
  assert(@info["ID"] == studentId, "Actual ID is: " + @info["ID"])
end

Then /^their grade is "([^"]*)"$/ do |studentGrade|
 assert(@info["Grade"] == studentGrade, "Actual Grade is: " + @info["Grade"])
end

Then /^the lozenges include "([^"]*)"$/ do |lozenge|
  csiContent = @driver.find_element(:class, "csi")
  labelFound = false
  
  all_lozengeSpans = csiContent.find_elements(:tag_name, "span")
  all_lozengeSpans.each do |lozengeSpan|
    if lozengeSpan.attribute("innerHTML").to_s.include?(lozenge)
      labelFound = true
    end
  end
  
  assert(labelFound == true)
end

Then /^the teacher is "([^"]*)"$/ do |teacherName|
  assert(@info["Teacher"] == teacherName, "Actual teacher is :" + @info["Teacher"]) 
end

When /^the class is "([^"]*)"$/ do |className|
  assert(@info["Class"] == className, "Actual class is :" + @info["Class"]) 
end

When /^the lozenges count is "([^"]*)"$/ do |lozengesCount|
  csiContent = @driver.find_element(:class, "csi")
  labelFound = false
  
  all_lozenges = csiContent.find_elements(:tag_name, "svg")

  assert(lozengesCount.to_i == all_lozenges.length, "Actual lozenges count is:" + all_lozenges.length.to_s)
end

# the order of expected enrollment history is: schoolYear, school, gradeLevel, entryDate, transfer, exitWithdrawDate, exitWithdrawType
Then /^Student Enrollment History includes "([^"]*)"$/ do |expectedEnrollment|
  #TODO check order of enrollment history
  expectedArray = expectedEnrollment.split(';')
  enrollmentTable = @driver.find_element(:xpath, "//div[@class='ui-jqgrid-bdiv']")
  rows = enrollmentTable.find_elements(:tag_name, "tr")
  
  puts "There are " + rows.length.to_s + " entries in Enrollment History"
  assert(rows.length > 1, "Is the enrollment history missing?")
  assert(expectedArray.length == 7, "Missing expected enrollment history element, actual # of elements: " + expectedArray.length.to_s )
  
  enrollmentFound = false
  # ignore row at index zero as it's the header
  for i in (1..rows.length-1)
    found = true
    schoolYear = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'schoolYear')]")
    school = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'nameOfInstitution')]")
    gradeLevel = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'entryGradeLevel')]")
    entryDate = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'entryDate')]")
    transfer = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'transfer')]")
    exitWithdrawDate = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'exitWithdrawDate')]")
    exitWithdrawType = rows[i].find_element(:xpath, "td[contains(@aria-describedby,'exitWithdrawType')]")
    
    enrollmentArray = [ schoolYear, school, gradeLevel, entryDate, transfer, exitWithdrawDate, exitWithdrawType ] 
    
    for j in (0..expectedArray.length-1)
      if (enrollmentArray[j].text != expectedArray[j])
          found = false
      end
    end  
    
    if (found)
      puts "Enrollment Entry Found in row " + i.to_s
      enrollmentFound = true
      break
    end
  end
  assert(enrollmentFound, "Enrollment is not found")
end

def clickOnStudent(name)
  studentTable = @explicitWait.until{@driver.find_element(:id, "studentList")}
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  
  @driver.find_element(:link, name).click
end

def clickOnStudentAtIndex(studentIndex)
  studentTable = @driver.find_element(:id, "studentList");
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  studentNames = all_tds[studentIndex.to_i].find_elements(:tag_name, "a")
  studentNames[0].click()
end

