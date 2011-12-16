require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

Given /^I go to the Student\-School Association Menu page$/ do
  @driver.find_element(:id, "navStudentSchoolAssociationMenu").click
  #assert(driver.current_url == @url+"/association/", "Failed to navigate to the Student-School Association Page")
end

When /^I select a student$/ do
  pending # express the regexp above with the code you wish you had
end

When /^Add the Student to a School$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the student should appear associated with that School$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I change the students grade to "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^press the update link$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the student appears now in "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I delete the association$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the student does not appear associated with that School$/ do
  pending # express the regexp above with the code you wish you had
end
