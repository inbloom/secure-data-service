require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^I navigated to the Data Browser Home URL$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

Given /^I was redirected to the Realm page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

When /^I choose realm "([^"]*)" in the drop\-down list$/ do |arg1|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
end

Given /^I click on the realm page Go button$/ do
  assertWithWait("Could not find the Go button")  { @driver.find_element(:id, "go") }
  @driver.find_element(:id, "go").click
end

Then /^I should be redirected to the Data Browser home page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

When /^I click on the "([^"]*)" link$/ do |arg1|
  assertWithWait("Failed to find '"+arg1+"' Link on page")  {@driver.find_element(:link_text, arg1)}
  @driver.find_element(:link_text, arg1).click
end

Given /^the API timeout is set to "(\d+)" seconds$/ do |arg1|
  @timeout = arg1.to_i
end

When /^I wait less than the timeout$/ do
  sleep(@timeout-15)
end

When /^I wait for longer than the timeout$/ do
  sleep(@timeout+15)
end
