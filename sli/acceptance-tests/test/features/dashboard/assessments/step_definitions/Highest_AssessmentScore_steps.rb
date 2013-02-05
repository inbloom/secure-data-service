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


require_relative '../../../utils/sli_utils.rb'

Then /^I should see his\/her highest StateTest Writing Scale Score is "([^"]*)"$/ do |scoreResult|
  scores = @driver.find_elements(:id, @studentName+".StateTest Writing.Scale score")
  scores.should_not be_nil
  scores[1].text.should == scoreResult
end

Then /^I should see a field "([^"]*)" for ScaleScore in this table$/ do |fieldName|
  name = @driver.find_element(:id, "listHeader." +@headerName+"."+fieldName)
  name.should_not be_nil
  name.text.should == fieldName
end

Then /^I should see a field "([^"]*)" for PercentileScore in this table$/ do |fieldName|
  name = @driver.find_element(:id, "listHeader." +@headerName+"."+fieldName)
  name.should_not be_nil
  name.text.should == fieldName
end

Then /^I should see a student  "([^"]*)" in student field$/ do |studentName|
  student=@driver.find_element(:id, studentName+".name")
  student.should_not be nil
  student.text.should include studentName
  @studentName = studentName
end

Then /^I should see his\/her highest ScaleScore in SAT Critical Reading is "([^"]*)"$/ do |scoreResult|
  score = @driver.find_element(:id, @studentName+".SAT.Scale score.SAT Reading")
  score.should_not be_nil
  score.text.should == scoreResult
end

Then /^I should see his\/her corresponding %ile Score in SAT Critical Reading is "([^"]*)"$/ do |scoreResult|
  score = @driver.find_element(:id, @studentName+".SAT.Percentile.SAT Reading")
  score.should_not be_nil
  score.text.should == scoreResult
end
