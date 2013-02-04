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


Then /^Assessment History includes results for:$/ do |table|
  table.hashes.each do |row|
    puts row["Test"]
    panel = getPanel("Assessments", row["Test"])
    assert(panel!=nil, "This Panel Doesn't Exist")    
  end
end

Then /^the Assessment History for "([^"]*)" has the following entries:$/ do |test, table|
  panel = getPanel("Assessments", test)
  #headers
  mapping = {
    "Date" => "administrationDate",
    "Grade" => "gradeLevelAssessedCode",
    "Assessment Name" => "assessmentTitle",
    "Scale score" => "Scale",
    "Other" => "Other",
    "Percentile" => "Percentile",
    "Perf Level" => "perfLevel"
  }   
  
  #For StateTest tests, we need to test fuel gauge visualization
  if (test.include? ("StateTest"))
    mapping["Perf Level"] = ["perfLevel", "fuelGauge"]
    setCutPoints(test)
  end
  
  checkGridEntries(panel, table, mapping)
end


