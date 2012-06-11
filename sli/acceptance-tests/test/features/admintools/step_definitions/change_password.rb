require "selenium-webdriver"
require 'json'
require 'net/imap'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require 'date'

Given /^I am a SLI Developer "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

Given /^I am a SLC Operator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

When /^I hit the Change Password URL$/ do
  @driver.get(PropLoader.getProps['admintools_server_url'] + "/change_password/new")
end

Then /^I am redirected to the Change Password page$/ do
  assertWithWait("Failed to navigate to the Change Password page")  {@driver.page_source.index("Change Password") != nil}
end

Then /^I see the input boxes to change my password$/ do
  assertWithWait("Failed to find the input boxes to change my password") {@driver.find_element(:id, "new_change_password")}
end

When /^I fill out the input field "([^\"]*)" as "([^\"]*)"$/ do |field, value|
  trimmed = field.sub(" ", "")
  @driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '#{trimmed.downcase}')
    ]").send_keys(value)
end

Then /^I click on "(.*?)"$/ do |arg1|
  @driver.find_element(:xpath, "//input[contains(@id, '#{arg1}')]").click
end

Then /^I see an error message "(.*?)"$/ do |errorMsg|
  assert(@driver.find_element(:xpath, "//h2[contains(@id, 'errorCountNotifier' and contains(text(), '#{errorMsg}')]").size != 0,
         "Cannot find error message \"#{errorMsg}\"")
end

Then /^I check for message  "(.*?)"$/ do |arg1|
  assertText(arg1)
end
