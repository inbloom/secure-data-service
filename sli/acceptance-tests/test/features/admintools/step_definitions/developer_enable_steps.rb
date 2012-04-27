require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

# When /^I hit the Application Registration Tool URL$/ do
#   @driver.get(PropLoader.getProps['admintools_server_url']+"/apps/")
# end

Then /^I see the list of \(only\) my applications$/ do
  assert("Should be more than one application listed", @driver.find_elements(:xpath, "//tr").count > 0)
end

Then /^I click on a registered "([^"]*)"$/ do |arg1|
  @driver.find_element("Testing App").click
end

Then /^I can see the on\-boarded states\/districts$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I check the <District>$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I click on Save$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the "([^"]*)" is enabled for <District>$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I log out$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I log in as a valid SLI Operator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I see the newly enabled application$/ do
  pending # express the regexp above with the code you wish you had
end