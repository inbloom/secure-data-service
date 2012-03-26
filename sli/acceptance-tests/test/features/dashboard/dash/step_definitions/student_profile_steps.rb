require 'selenium-webdriver'

When /^I click on student "([^"]*)"$/ do |name|
  clickOnStudent(name)
end

When /^I click on student at index "([^"]*)"$/ do |studentIndex|
  clickOnStudentAtIndex(studentIndex)
end

When /^I view its student profile$/ do
  wait = Selenium::WebDriver::Wait.new(:timeout => 3) 
  csiContent = wait.until{@driver.find_element(:class, "csi")}
  studentInfo = csiContent.find_element(:class, "studentInfo")
  table_cells = studentInfo.find_elements(:xpath, "//div[@class='field']/span")
  @info = Hash.new
  sName = csiContent.find_element(:class, "studentName") 

  @info["Name"] = sName.text
  
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
   containsName = @info["Name"].include? expectedStudentName
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

def clickOnStudent(name)
  # wait for live case
  wait = Selenium::WebDriver::Wait.new(:timeout => 20) 
  
  studentTable = wait.until{@driver.find_element(:id, "studentList")}
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  
  @driver.find_element(:link, name).click
end

def clickOnStudentAtIndex(studentIndex)
  studentTable = @driver.find_element(:id, "studentList");
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  studentNames = all_tds[studentIndex.to_i].find_elements(:tag_name, "a")
  studentNames[0].click()
end

