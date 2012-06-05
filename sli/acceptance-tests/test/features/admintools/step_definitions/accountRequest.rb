require "selenium-webdriver"
require "mongo"
require 'approval'
require 'active_support/inflector'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
  @baseUrl = PropLoader.getProps['admintools_server_url']
  @registrationAppSuffix = PropLoader.getProps['registration_app_suffix']
  @validationBaseSuffix = PropLoader.getProps['validation_base_suffix']
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
  url = @validationLink                                         if human_readable_id == "ALREADY VERIFIED LINK"
  url = @baseUrl + @validationBaseSuffix + "/invalid123"        if human_readable_id == "INVALID VERIFICATION LINK"
  #return the translated value
  url
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I go to the account registration page$/ do
  userRegAppUrl = @baseUrl + @registrationAppSuffix
  @driver.get userRegAppUrl
end

Given /^there is no registered account for "([^\"]*)" in the SLI database$/ do |email|
  removeUser(email)
  step "the account for \"#{email}\" is removed from SLI database"
end

Given /^there is no registered account for "([^\"]*)" in LDAP$/ do |email|
  removeUser(email)
  assert(!ApprovalEngine.user_exists?(email), "#{email} still exists in LDAP")
end

Given /^I go to the production account registration page$/ do
  @prod = true
  initializeApprovalAndLDAP(@emailConf, @prod)
end

Given /^I go to the sandbox account registration page$/ do
  @prod = false
  initializeApprovalAndLDAP(@emailConf, @prod)
end

Given /^there is an approved account with login name "([^\"]*)"$/ do |email|
  records = getRecordsFromMongo("body.userName", email)
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
  if button == 'Cancel'
    @driver.find_element(:xpath, "//a[text()=\"#{button}\"]").click
  else
    @driver.find_element(:xpath, "//input[contains(@id, '#{button.downcase}')]").click
  end
end

Then /^my field entries are validated$/ do
  errorMsgs = @driver.find_elements(:xpath, "//td[contains(@id, 'error_explanation')]")
  assert(errorMsgs.size == 0, "Found input error(s) in page")
end

Then /^I am redirected to a page with terms and conditions$/ do
  assert(@driver.current_url.include?("#{@baseUrl}/eula"))
  assertText("Terms and Conditions")
end

Then /^I receive an error that the account already exists$/ do
  step "I should see the error message \"An account with this email already exists\""
end

Then /^I am redirected to the hosting website$/ do
  assert(@driver.current_url.include?(PropLoader.getProps['user_registration_app_host_url']))
end

Then /^I am directed to an acknowledgement page.$/ do
  assertText("Thank You")
  assertText("Be on the lookout for a confirmation email.")
end

Then /^I get (\d+) record for "([^\"]*)"$/ do |count, email|
  foundRecords = 0
  @validatedRecords.each do |record|
    if (record["body"]["userName"] == email)
      foundRecords = foundRecords + 1
    end
  end
  assert(foundRecords == convert(count), "Expected #{count}, received #{foundRecords}")
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

Then /^an email verification link for "([^\"]*)" is generated$/ do |email|
  emailToken = getEmailToken(email)
  @validationLink = @baseUrl + @validationBaseSuffix + "/" + emailToken
  puts @validationLink
end

Then /^I should see the text "([^\"]*)"$/ do |text|
  assertText(text)
end

Then /^I should see the error message "([^\"]*)"$/ do |errorMsg|
  assert(@driver.find_element(:xpath, "//span[@class='help-inline' and contains(text(), '#{errorMsg}')]").size != 0,
         "Cannot find error message \"#{errorMsg}\"")
end

Then /^the account for "([^\"]*)" is removed from SLI database$/ do |email|
  records = getRecordsFromMongo("body.userName", email)
  assert(records.size == 0, "Account for #{email} is not removed")
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

def initializeApprovalAndLDAP(emailConf, prod)
  ldapBase = PropLoader.getProps['ldap_base']
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], 389, ldapBase, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  email = Emailer.new emailConf
  ApprovalEngine.init(@ldap, email, nil, !prod)
end

def assertText(text)
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text \"#{text}\"")
end

def toCamelCase(key)
  return "userName" if key == "Email"
  str = key.sub(" ", "")
  return str.camelize(:lower)
end

def getEmailToken(email)
  userInfo= ApprovalEngine.get_user(email)
  return userInfo[:emailtoken]
end

def removeUser(email)
  if ApprovalEngine.user_exists?(email)
    ApprovalEngine.remove_user(email)
  end
  coll = @db["userAccount"]
  coll.remove("body.userName" => email)
end

def getRecordsFromMongo(field, value)
  @prod ? env = "Production" : env = "Sandbox"
  coll = @db["userAccount"]
  return coll.find(field => value, "body.environment" => env).to_a
end