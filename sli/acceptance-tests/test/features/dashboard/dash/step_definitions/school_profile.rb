=begin

Copyright 2012 Shared Learning Collaborative, LLC

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
Then /^I view the School Profile/ do
  @schoolInfo = viewInfoPanel("schoolProfile", "schoolInfo")
  verifyPageTitle("SLC - School Profile")
end

Then /^the school name is "(.*?)"$/ do |expectedSchoolName|
  assert(@schoolInfo["Name"] == expectedSchoolName, "Actual name is :" + @schoolInfo["Name"]) 
end

Then /^the school address is$/ do |expectedAddress|
  assert(@schoolInfo["Address"] == expectedAddress, "Actual Address is: " + @schoolInfo["Address"])
end

Then /^the school phone number is "(.*?)"$/ do |expectedPhone|
  assert(@schoolInfo["Main"] == expectedPhone, "Actual Phone is: " + @schoolInfo["Main"])
end

Then /^the grades served is "(.*?)"$/ do |expectedGradesServed|
  assert(@schoolInfo["Grades offered"] == expectedGradesServed, "Actual Grades Served is: " + @schoolInfo["Grades offered"])
end

Then /^I click on subject "(.*?)"$/ do |subjectName|
  findAndClickOnTreeGrid(subjectName)
end

Then /^I click on course "(.*?)"$/ do |courseName|  
  findAndClickOnTreeGrid(courseName)
end

Then /^I click on section "(.*?)"$/ do |sectionName|
  findAndClickOnTreeGrid(sectionName)
end

Then /^I see the following subjects:$/ do |table|
  subjects = transformToHash(table, "Subject")
  readTreeGrid() 
  verifyElementsInGrid(subjects, @subjectsOnly)
end

Then /^I see the following courses:$/ do |table|
  courses = transformToHash(table, "Course")
  readTreeGrid()
  @coursesOnly = @currentVisibleTreeNodes - @subjectsOnly
  verifyElementsInGrid(courses, @coursesOnly)
end

Then /^I see the following sections:$/ do |table|
  sections = transformToHash(table, "Section")
  readTreeGrid()
  sectionsOnly = @currentVisibleTreeNodes - @subjectsOnly - @coursesOnly
  verifyElementsInGrid(sections, sectionsOnly)
end

def transformToHash(table, columnName)
  htable = Hash.new
  table.hashes.each do |row|
    name = row[columnName]
    htable[name] = false
  end
  return htable
end

def verifyElementsInGrid(expectedhTable, actualArray)
  actualArray.each do |actualRow|
    name = actualRow.text
    expectedhTable[name] = true
  end
  
  expectedhTable.each_key do |key|
    assert(expectedhTable[key], "element was not found in grid: " + key)
  end
end

### TREE GRID 

def findAndClickOnTreeGrid(name)
  readTreeGrid()
  clickOnTreeGrid(name)
end

def readTreeGrid()
  trs = getGrid(@driver)
  @currentVisibleTreeNodes = []
  trs.each do |tr|
    # Look for elements that dont have style set, without style, it means it's visible
    if (tr.attribute("style") == "")
      @currentVisibleTreeNodes << tr
    end
  end
  if (@subjectsOnly == nil)
    @subjectsOnly = @currentVisibleTreeNodes
  end
end

def clickOnTreeGrid(name)
  found = false
  @currentVisibleTreeNodes.each do |node|
    if (node.text == name)
      node.find_element(:tag_name,"span").click
      found = true
      break
    end
  end
  assert(found, "Node name not found: " + name)
end
