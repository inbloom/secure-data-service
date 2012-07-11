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
  @sectionInfo = viewInfoPanel("sectionProfile", "sectionInfo")
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