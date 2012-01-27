require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'

Given /^I am from realm "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Given /^another realm's admin mapped "([^"]*)" to the "([^"]*)" default SLI role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I navigate to the SLI Role Mapping Admin Page$/ do
  url = PropLoader.getProps['admintools_server_url']+"/admin/role-mapping"
  @driver.get url
end

When /^I map the default SLI role "([^"]*)" to the custom role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I see that "([^"]*)" is now mapped to the "([^"]*)" role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^a "([^"]*)" can now log in to SLI as a "([^"]*)" from my realm "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end

Given /^a "([^"]*)" has been mapped to the "([^"]*)" default role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^I remove the map from the default SLI role "([^"]*)" to the custom role "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I see that "([^"]*)" is no longer mapped to the "([^"]*)" role$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^a "([^"]*)" can not access SLI as a "([^"]*)" from my realm "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end

Then /^I should not see the mapping from the other realm$/ do
  pending # express the regexp above with the code you wish you had
end