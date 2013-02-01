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
###############################
#    Generic profile
###############################
# Given a panelName and the panelInfoName, it reads the table for a generic info section
def viewInfoPanel(panelName, panelInfoName = nil)
  panel = @explicitWait.until{@driver.find_element(:class, panelName)}
  name = panel.find_element(:css, "h1") 
  hTable = Hash.new
  hTable["Name"] = name.text
  puts name.text
  
  if (panelInfoName != nil)
    infoPanel = panel.find_element(:class, panelInfoName)
    table_cells = infoPanel.find_elements(:tag_name, "tr")
  
    for i in 0..table_cells.length-1
     th = table_cells[i].find_element(:tag_name, "th") 
     td = table_cells[i].find_element(:tag_name, "td") 
     key = th.text[0..th.text.length-2]
     puts key
     puts td.text
     hTable[key]= td.text
    end
  end
  return hTable
end

###############################
#    Ed-org profile
###############################
When /^I look at Ed Org Profile$/ do
  @edorgInfo = viewInfoPanel("edorgProfile")
  @currentTab = getTab("Schools")
end

Then /^the ed org name shown is "(.*?)"$/ do |expectedName|
   assert(@edorgInfo["Name"] == expectedName, "Actual name is :" + @edorgInfo["Name"]) 
end

When /^I see the following schools:$/ do |table|
  #headers
  mapping = {
    "School" => "nameOfInstitution"
  }   
  #assumes currentTab has 1 grid
  checkGridEntries(@currentTab, table, mapping)
end

When /^I click on school "(.*?)"$/ do |edorgName|
  clickOnRow(edorgName)
end

###############################
#    Section Profile
###############################
When /^I view its section profile$/ do
  @sectionInfo = viewInfoPanel("sectionProfile", "sectionInfo")
  verifyPageTitle("inBloom - Section Profile")
end

Then /^the section name shown in section profile is "([^"]*)"$/ do |expectedSectionName|
  containsName = @sectionInfo["Name"] == expectedSectionName
  assert(containsName, "Actual name is :" + @sectionInfo["Name"]) 
end

And /^the teacher name shown in section profile is "([^"]*)"$/ do |expectedTeacherName|
  assert(@sectionInfo["Teacher"] == expectedTeacherName, "Actual teacher is :" + @sectionInfo["Teacher"])   
end

And /^the course name shown in section profile is "([^"]*)"$/ do |expectedCourseName|
  assert(@sectionInfo["Course"] == expectedCourseName, "Actual course is :" + @sectionInfo["Course"])
end

And /^the subject name shown in section profile is "([^"]*)"$/ do |expectedSubjectName|
  assert(@sectionInfo["Subject"] == expectedSubjectName, "Actual subject is :" + @sectionInfo["Subject"])
end

###############################
#    Teacher Profile
###############################

When /^I look at Teacher Profile$/ do
  @teacherInfo = viewInfoPanel("teacherProfile")
  verifyPageTitle("inBloom - Teacher Profile")
  #Assume first tab is Sections
  @currentTab = getTab("Sections")
end

Then /^the teacher name shown is "(.*?)"$/ do |expectedTeacherName|
  assert(@teacherInfo["Name"] == expectedTeacherName, "Actual name is :" + @teacherInfo["Name"]) 
end

Then /^I see the following current sections:$/ do |table|
  panel = getPanel("Sections", "Current Sections")
  #headers
  mapping = {
    "Sections" => "uniqueSectionCode"
  }   
  checkGridEntries(panel, table, mapping)
end

Then /^I click to see section "(.*?)"$/ do |sectionName|
  clickOnRow(sectionName)
end

###############################
#    List of Teachers
###############################

When /^I see the following teachers:$/ do |table|
  panel = getPanel("Teachers")
  #headers
  mapping = {
    "Teachers" => "fullName"
  }   
  checkGridEntries(panel, table, mapping)
end

When /^I click on teacher "(.*?)"$/ do |teacherName|
  clickOnRow(teacherName)
end



