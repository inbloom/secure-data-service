require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

When /^I select the "([^"]*)" realm$/ do |arg1|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
  @driver.find_element(:id, "go").click
end

Given /^I navigate to databrowser home page$/ do
  @driver.get PropLoader.getProps['databrowser_server_url']
end

Then /^I will be redirected to realm selector web page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

When /^I enter the credentials "([^"]*)" "([^"]*)" for the Simple IDP$/ do |arg1, arg2|
  @driver.find_element(:id, "user_id").send_keys arg1
  @driver.find_element(:id, "password").send_keys arg2
end

When /^I want to imitate the user "([^"]*)" who is a "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:id, "impersonate_user").send_keys arg1
  role_select = @driver.find_element(:id, "selected_roles")
  options = role_select.find_elements(:tag_name=>"option")
  options.each do |e1|
    if (e1.text == arg2)
    e1.click()
    break
    end
  end
end

Then /^I should be redirected to the databrowser web page$/ do
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

Then /^I should see the name "([^"]*)" on the page$/ do |arg1|
  assert(@driver.page_source.include?(arg1), "Failed to find #{arg1} on the page")
end

Then /^I am denied from accessing the databrowser$/ do
  assertWithWait("Was directed to the databrowser when it shouldn't have")  {!@driver.page_source.include?("Listing Home")}
end

When /^I click Login$/ do
  @driver.find_element(:id, "login_button").click
end

When /^I wait for (\d+) second$/ do |arg1|
  sleep(Integer(arg1))
end
