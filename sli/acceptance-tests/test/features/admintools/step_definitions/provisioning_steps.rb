require "selenium-webdriver"
require "json"

require_relative "../../utils/sli_utils.rb"
require_relative "../../utils/selenium_common.rb"
require "date"

SAMPLE_DATA_SET1_CHOICE = "Sample Data Set 1"
SAMPLE_DATA_SET2_CHOICE = "Sample Data Set 2"
CUSTOM_DATA_SET_CHOICE = "Custom High Level Ed Org"

Given /^there is a production account in ldap for vendor "([^"]*)"$/ do |vendor|
end

When /^I provision$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^a high\-level ed\-org is created in Mongo$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^Landing\-Zone provisioining API is invoked with the high\-level ed\-org information$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I go to the provisioning application$/ do
  url = PropLoader.getProps['admintools_server_url']+"/provision/"
  @driver.get(url)
end

Then /^I can only enter a custom high\-level ed\-org$/ do
  assertWithWait("Sample data choice exists") {@driver.find_element(:id, SAMPLE_DATA_SET1_CHOICE) == nil}
  assertWithWait("Sample data choice exists") {@driver.find_element(:id, SAMPLE_DATA_SET2_CHOICE) == nil}
  assertWithWait("Custom data choice does not exist") {@driver.find_element(:id, CUSTOM_DATA_SET_CHOICE) != nil}
end

When /^I click the Provision button$/ do
  clickButton("provision", "id")
end

Then /^I get the success message$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^there is a sandbox account in ldap for vendor "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I can select between the the high level ed\-org of the sample data sets or enter a custom high\-level ed\-org$/ do
  assertWithWait("Sample data choice does not exist") {@driver.find_element(:id, SAMPLE_DATA_SET1_CHOICE) != nil}
  assertWithWait("Sample data choice does not exist") {@driver.find_element(:id, SAMPLE_DATA_SET2_CHOICE) != nil}
  assertWithWait("Custom data choice does not exist") {@driver.find_element(:id, CUSTOM_DATA_SET_CHOICE) != nil}
end
