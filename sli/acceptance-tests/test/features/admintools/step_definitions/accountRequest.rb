require "selenium-webdriver"
require 'approval'
require 'active_support/inflector'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
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
  url = "lalsop_#{Socket.gethostname}@acme.com"                 if human_readable_id == "USER_ACCOUNT"
  url = "lalsop_#{Socket.gethostname}@acme.com"                 if human_readable_id == "USER_ACCOUNT_EMAIL"
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

Given /^there is an (\w+) account with login name "([^\"]*)"$/ do |status, email|
   assert(ApprovalEngine.user_exists?(email), "#{email} does not exists in LDAP")
   user = ApprovalEngine.get_user(email)
   assert(user[:status] == status, "#{email} has status #{user[:status]}, expected #{status}.")
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

When /^I query LDAP for EULA acceptance for account with login name "([^\"]*)"$/ do |email|
   assert(ApprovalEngine.user_exists?(email), "#{email} does not exists in LDAP")
   user = ApprovalEngine.get_user(email)
   assert(user[:status] == "eula-accepted" || user[:status] == "pending" || user[:status] == "approved", "#{email} did not accept the EULA.")
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

Then /^a captcha form is shown$/ do
  @driver.find_element(:id, "recaptcha_widget_div").should_not == nil
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

Then /^I get a record for "([^\"]*)"$/ do |email|
  @record = ApprovalEngine.get_user(email)
  @record.should_not == nil
end

Then /^"([^\"]*)" is "([^\"]*)"$/ do |inKey, value|
  key_map = { "First Name" => :first, "Last Name" => :last, "Email" => :email, "Vendor" => :vendor }
  @record.should_not == nil
  @record[key_map[inKey]].should == value
  
  # key = toCamelCase(inKey)
  # if (@validatedRecords.size == 1)
  #   assert(@validatedRecords[0]["body"][key] == convert(value), "Expected #{value} for #{inKey},
  #       received #{@validatedRecords[0]["body"][key]}")
  # else
  #   raise("Error: received more than 1 record (unhandled case)")
  # end
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

Then /^the account for "([^\"]*)" is removed from LDAP$/ do |email|
   assert(ApprovalEngine.user_exists?(email), "Account for #{email} is not removed")
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
end
