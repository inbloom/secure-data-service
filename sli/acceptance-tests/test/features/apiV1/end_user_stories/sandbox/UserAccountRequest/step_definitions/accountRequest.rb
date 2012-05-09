require "selenium-webdriver"
require "mongo"
require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../utils/selenium_common.rb'

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  connection = Mongo::Connection.new
  @db = connection.db("sli")
  @userRegAppUrl = "http://local.slidev.org:3000"
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I go to the production account registration page$/ do
  @driver.get @userRegAppUrl
end

Given /^I go to the sandbox account registration page$/ do
  @driver.get @userRegAppUrl
end

Given /^there is an approved account with login name "([^\"]*)"$/ do |email|
  coll = @db["userAccount"]
  assert(coll.find("username" => email).count != 0, "No record found for #{email}")
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I fill out the field "([^\"]*)" as "([^\"]*)"$/ do |field, value|
  trimmed = field.sub(" ", "")
  @driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '#{trimmed.downcase}')
    ]").send_keys(value)
end

When /^I query the database for EULA acceptance$/ do
  coll = @db["userAccount"]
  @records = coll.find("validated" => true)
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^my password is shown as a series of dots$/ do
  assert(@driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'password')
    ]").attribute("type") == "password")
  assert(@driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'confirmation')
    ]").attribute("type") == "password")
end

Then /^when I click "([^\"]*)"$/ do |button|
  @driver.find_element(:id, "#{button.downcase}").click
end

Then /^my field entries are validated$/ do
  errorMsgs = @driver.find_elements(:xpath, "//td[contains(@id, 'error_explanation')]")
  assert(errorMsgs.size == 0, "Found input error(s) in page")
end

Then /^I am redirected to a page with terms and conditions$/ do
  assert(@driver.current_url.include?("#{@userRegAppUrl}/eulas"))
end

Then /^I receive an error that the account already exists$/ do
  assert(@driver.find_element(:xpath, "//div[contains(
    text(), 'Username already exists')
    ]").size != 0, "Cannot find error message")
end

Then /^I am redirected to the SLC website$/ do
  assert(@driver.current_url.include?("http://www.slcedu.org"))
end

Then /^I am directed to an acknowledgement page.$/ do
  text = "Successful"
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text #{text}")
end