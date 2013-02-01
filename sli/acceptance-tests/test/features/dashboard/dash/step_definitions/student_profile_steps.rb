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


require 'selenium-webdriver'

When /^I click on student "([^"]*)"$/ do |name|
  clickOnStudent(name)
  sleep(6)
end

When /^I view its student profile$/ do
  @studentInfo = viewInfoPanel("csi", "studentInfo")
end

Then /^I cannot see csi panel in student profile$/ do
  begin
    #ensure we're on student profile page
    @explicitWait.until{@driver.find_element(:id, "tabs")}
    csiContent = @driver.find_element(:class, "csi")
  rescue
    assert(csiContent == nil, "csi panel was found")
  end
end

Then /^their name shown in profile is "([^"]*)"$/ do |expectedStudentName|
   containsName = @studentInfo["Name"] == expectedStudentName
   assert(containsName, "Actual name is :" + @studentInfo["Name"]) 
end

Then /^their id shown in proflie is "([^"]*)"$/ do |studentId|
  assert(@studentInfo["ID"] == studentId, "Actual ID is: " + @studentInfo["ID"])
end

Then /^their grade is "([^"]*)"$/ do |studentGrade|
 assert(@studentInfo["Grade"] == studentGrade, "Actual Grade is: " + @studentInfo["Grade"])
end

Then /^the lozenges include "([^"]*)"$/ do |lozenge|
  csiContent = @driver.find_element(:class, "csi")
  labelFound = false
  
  all_lozenge = csiContent.find_elements(:css, "span[class*='lozenge-widget']")
  all_lozenge.each do |lozengeElement|
    if lozengeElement.attribute("innerHTML").to_s.include?(lozenge)
      labelFound = true
    end
  end
  
  assert(labelFound == true)
end

Then /^the teacher is "([^"]*)"$/ do |teacherName|
  assert(@studentInfo["Teacher"] == teacherName, "Actual teacher is :" + @studentInfo["Teacher"]) 
end

When /^the class is "([^"]*)"$/ do |className|
  assert(@studentInfo["Class"] == className, "Actual class is :" + @studentInfo["Class"]) 
end

When /^the lozenges count is "([^"]*)"$/ do |lozengesCount|
  csiContent = @driver.find_element(:class, "csi")

  all_lozenges = csiContent.find_elements(:css, "span[class*='lozenge-widget']")

  assert(lozengesCount.to_i == all_lozenges.length, "Actual lozenges count is:" + all_lozenges.length.to_s)
end

def clickOnStudent(name)
  los = @explicitWait.until{@driver.find_element(:class, "ui-jqgrid-bdiv")}
  
  @driver.find_element(:link_text, name).click
end
