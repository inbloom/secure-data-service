require "selenium-webdriver"
require "mongo"
require 'approval'
require 'active_support/inflector'
require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../utils/selenium_common.rb'
require_relative '../../AccountApproval/step_definitions/approval_steps.rb'

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
  @userRegAppUrl = PropLoader.getProps['user_registration_app_url']
  @validationBaseSuffix = "/user_account_validation"

  @emailConf = {
      :host => 'mon.slidev.org',
      :port => 3000,
  }
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  url = @validationLink                                         if human_readable_id == "VALID VERIFICATION LINK"
  url = @userRegAppUrl + @validationBaseSuffix + "/invalid123"  if human_readable_id == "INVALID VERIFICATION LINK"
  #return the translated value
  url
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I go to the production account registration page$/ do
  @driver.get @userRegAppUrl
  @prod = true
end

Given /^I go to the sandbox account registration page$/ do
  @driver.get @userRegAppUrl
  @prod = false
end

Given /^there is an approved account with login name "([^\"]*)"$/ do |email|
  @prod ? env = "Production" : env = "Sandbox"
  coll = @db["userAccount"]
  records = coll.find("body.userName" => email, "body.environment" => env).to_a
  assert(records.size != 0, "No record found for #{email}")
  assert(records.size == 1, "More than one records found for #{email}")
  records.each do |record|
    assert(record["body"]["validated"] == true, "#{email} is not validated")
  end
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I fill out the field "([^\"]*)" as "([^\"]*)"$/ do |field, value|
  if field "Email"
    @emailAddress = value
  end
  trimmed = field.sub(" ", "")
  @driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '#{trimmed.downcase}')
    ]").send_keys(value)
end

When /^I query the database for EULA acceptance$/ do
  coll = @db["userAccount"]
  @validatedRecords = coll.find("body.validated" => true).to_a
end

When /^I visit "([^\"]*)"$/ do |link|
  @driver.get link
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
  @driver.find_element(:xpath, "//input[contains(@id, '#{button.downcase}')]").click
end

Then /^my field entries are validated$/ do
  errorMsgs = @driver.find_elements(:xpath, "//td[contains(@id, 'error_explanation')]")
  assert(errorMsgs.size == 0, "Found input error(s) in page")
end

Then /^I am redirected to a page with terms and conditions$/ do
  assert(@driver.current_url.include?("#{@userRegAppUrl}/eula"))
  assertText("Terms of use")
end

Then /^I receive an error that the account already exists$/ do
  assert(@driver.find_element(:xpath, "//div[contains(
    text(), 'Username already exists')
    ]").size != 0, "Cannot find error message")
end

Then /^I am redirected to the hosting website$/ do
  assert(@driver.current_url.include?(PropLoader.getProps['user_registration_app_host_url']))
end

Then /^I am directed to an acknowledgement page.$/ do
  assertText("Your request was submitted.")
end

Then /^I get (\d+) record$/ do |count|
  assert(@validatedRecords.size == convert(count), "Expected #{count}, received #{@validatedRecords.size}")
end

Then /^"([^\"]*)" is "([^\"]*)"$/ do |inKey, value|
  key = toCamelCase(inKey)
  if (@validatedRecords.size == 1)
    assert(@validatedRecords[0]["body"][key] == convert(value), "Expected #{value} for #{inKey},
        received #{@validatedRecords[0]["body"][key]}")
  else
    raise("Error: received more than 1 record (unhandled case)")
  end
end

Then /^an email verification link is generated$/ do
  emailToken = getEmailToken(@emailAddress)
  @validationLink = @userRegAppUrl + @validationBaseSuffix + "/" + emailToken
end

Then /^I should see the text "([^\"]*)"$/ do |text|
  assertText(text)
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

def assertText(text)
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text #{text}")
end

def toCamelCase(key)
  return "userName" if key == "Email"
  str = key.sub(" ", "")
  return str.camelize(:lower)
end

def getEmailToken(email)
  intializaApprovalEngineAndLDAP(@emailConf, @prod)
  userInfo= ApprovalEngine.get_user(email)
  return userInfo[:emailtoken]
end