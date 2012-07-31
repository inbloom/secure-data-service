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
Then /^I go to Teacher Profile$/ do
  # this is temporary until we have a link to teacher profile
  url = getBaseUrl() + "/s/l/teacher/8e2dbe38-273a-477c-95d0-bc8c47701103"
  @driver.get url
  @teacherInfo = viewInfoPanel("teacherProfile")
  verifyPageTitle("SLC - Teacher Profile")
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

Then /^I click on "(.*?)"$/ do |sectionName|
  all_trs = getGrid(@currentTab)
  section = nil
  all_trs.each do |tr|
    if tr.attribute("innerHTML").to_s.include?(sectionName)
      section = tr.find_element(:link_text, sectionName)
      break
    end
  end  
  assert(section!=nil, "Section #{sectionName} was not found")
  section.click
end