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


When /^I view its section profile$/ do

  sectionContent = @explicitWait.until{@driver.find_element(:class, "sectionProfile")}
  sectionInfo = sectionContent.find_element(:class, "sectionInfo")
  table_cells = sectionInfo.find_elements(:tag_name,"tr")
  @info = Hash.new
  sName = sectionContent.find_element(:xpath, "h1") 
  
  @info["Name"] = sName.text
  puts sName.text
  
  for i in 0..table_cells.length-1
   th = table_cells[i].find_element(:tag_name,"th") 
   td = table_cells[i].find_element(:tag_name,"td") 
   puts th.text[0..th.text.length-2]
   puts td.text
   key = th.text[0..th.text.length-2]
   @info[key]= td.text
  end
end

Then /^the section name shown in section profile is "([^"]*)"$/ do |expectedSectionName|
  containsName = @info["Name"] == expectedSectionName
  assert(containsName, "Actual name is :" + @info["Name"]) 
end

And /^the teacher name shown in section profile is "([^"]*)"$/ do |expectedTeacherName|
  assert(@info["Teacher"] == expectedTeacherName, "Actual teacher is :" + @info["Teacher"])   
end

And /^the course name shown in section profile is "([^"]*)"$/ do |expectedCourseName|
  assert(@info["Course"] == expectedCourseName, "Actual course is :" + @info["Course"])
end

And /^the subject name shown in section profile is "([^"]*)"$/ do |expectedSubjectName|
  assert(@info["Subject"] == expectedSubjectName, "Actual subject is :" + @info["Subject"])
end