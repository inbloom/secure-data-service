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

When /^the view configuration file has a AssessmentFamilyHierarchy like "([^"]*)"$/ do |arg1|
  # configuration has been setup to use correct assessmentFamilyHierarchy
end

Then /^I should see his\/her most recent StateTest Writing Perf\. level is "([^"]*)"$/ do |perfLevel|
  level = @driver.find_element(:id, @studentName+".StateTest Writing for Grade 8.Mastery level")
  level.should_not be nil
  level.text.should == perfLevel
end

Then /^I should see his\/her Perf\.level for READ 2.0 for most recent window is "([^"]*)"$/ do |perfLevel|
  level = @driver.find_element(:id, @studentName+".READ2_NEXT.Mastery level")
  level.should_not be nil
  level.text.should == perfLevel
end
