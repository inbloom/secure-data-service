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