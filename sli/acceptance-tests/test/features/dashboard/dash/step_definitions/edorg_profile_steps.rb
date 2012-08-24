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

When /^I look at Ed Org Profile$/ do
  @edorgInfo = viewInfoPanel("edorgProfile")
end

Then /^the ed org name shown is "(.*?)"$/ do |expectedName|
   assert(@edorgInfo["Name"] == expectedName, "Actual name is :" + @edorgInfo["Name"]) 
end

When /^I see the following schools:$/ do |table|
  #headers
  mapping = {
    "School" => "nameOfInstitution"
  }   
  # edorg profiles doesn't have panels
  checkGridEntries(@driver, table, mapping)
end

When /^I click on school "(.*?)"$/ do |edorgName|
  # edorg profile doesn't have tabs
  @currentTab = @driver
  clickOnRow(edorgName)
end