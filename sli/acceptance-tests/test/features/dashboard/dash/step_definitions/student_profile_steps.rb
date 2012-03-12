require 'selenium-webdriver'

When /^I click on student "([^"]*)"$/ do |name|
  clickOnStudent(name)
end

When /^I click on student at index "([^"]*)"$/ do |studentIndex|
  clickOnStudentAtIndex(studentIndex)
end

When /^I view its student profile$/ do
 csiContent = @driver.find_element(:id, "CSIcontent")
 table_cells = csiContent.find_elements(:xpath, "//td")
  
  @info = Hash.new
  sName = csiContent.find_element(:tag_name, "b") 
  
  @info["Name"] = sName.text
  
for i in 0..table_cells.length-1
  if table_cells[i].text.length > 0 
      if i % 2 == 0
        key = table_cells[i].text
      else
        @info[key]= table_cells[i].text
      end
   end
 end
end

Then /^their name shown in profile is "([^"]*)"$/ do |expectedStudentName|
   assert(@info["Name"] == expectedStudentName)
end

Then /^their id shown in proflie is "([^"]*)"$/ do |studentId|
  assert(@info["ID"] == studentId)
end

Then /^their grade is "([^"]*)"$/ do |studentGrade|
 assert(@info["Grade"] == studentGrade)
end

Then /^the lozenges include "([^"]*)"$/ do |lozenge|
  csiContent = @driver.find_element(:id, "CSIcontent")
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
  assert(@info["Teacher"] == teacherName)
end


def clickOnStudent(name)
  studentTable = @driver.find_element(:id, "studentList");
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  
  @driver.find_element(:link, name).click
  
  
  #studentNames = all_tds[studentIndex.to_i].find_elements(:tag_name, "a")
  #studentNames[0].click()
end

def clickOnStudentAtIndex(studentIndex)
  studentTable = @driver.find_element(:id, "studentList");
  all_tds = studentTable.find_elements(:xpath, "//td[@class='name_w_link']")
  studentNames = all_tds[studentIndex.to_i].find_elements(:tag_name, "a")
  studentNames[0].click()
end

