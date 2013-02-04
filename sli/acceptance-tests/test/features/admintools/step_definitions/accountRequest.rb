=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require "selenium-webdriver"
require 'net/imap'
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
      :host => PropLoader.getProps['email_smtp_host'],
      :port => PropLoader.getProps['email_smtp_port'],
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
  url = "devldapuser_#{Socket.gethostname}@slidev.org"           if human_readable_id == "USER_ID"
  url = "test1234"                                               if human_readable_id == "USER_PASS"
  url = "abcdefg"                                 if human_readable_id == "BAD_RSAKEY"
  url = "---- BEGIN SSH2 PUBLIC KEY ----\nComment: \"2048-bit RSA, converted from OpenSSH by syau@devpantheon\"\nAAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjB\nLi4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tp\na3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8\nNb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I\n3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ip\nOxzd/1/ZKeKmQ5j5R63Q==\n---- END SSH2 PUBLIC KEY ----"   if human_readable_id == "GOOD_RSAKEY"
  url = "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA6JVegtFBWdLKDE1gQplnHA8PZ+ceAnoduBTnJj+XjBLi4To5DSEFLzF1D6fLorIK3F2GIe+3yGT+wmdhXRFXEtpU+p8MD1ys/w6qR+s57kkV9/tpa3Ako7DwAL2YOIM50dEcWkvNGZbIOSBgjxM/dI6x5YEQZXsRc4wFydcJxQ8K6sN5t0fke8Nb28W0C5u7TvZrWgWTPbR6sZ2lK1dsmaXa+dsPWwHnvBPEGImThe/nyqIvpyIGXvlMd+4I3wx3PoRceyJD/PtsJBYI7NBYiflVnqiLhM1/rRKMOBNqmQat/vwsY6VVxbNkC4a12GM2ipOxzd/1/ZKeKmQ5j5R63Q== srichards@wgen.net"                                         if human_readable_id == "OPENSSH_RSAKEY"
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
  puts "field = #{field}, value = #{value}"
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
  assertWithWait("Password field is not masked") { 
    @driver.find_element(:xpath, "//input[contains(translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'password')]").attribute("type") == "password" 
  }
  assertWithWait("Confirmation field is not masked") { 
    @driver.find_element(:xpath, "//input[contains(translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'confirmation')]").attribute("type") == "password" 
  }
end

Then /^a captcha form is shown$/ do
  @driver.find_element(:id, "recaptcha_widget_div").should_not == nil
end

Then /^when I click "([^\"]*)"$/ do |button|
  if button == 'Cancel'
    @driver.find_element(:xpath, "//a[text()=\"#{button}\"]").click
    puts "Declined Terms and Conditions."
  else
    @driver.find_element(:xpath, "//input[contains(@id, '#{button.downcase}')]").click
    puts "Accepted Terms and Conditions."
  end
end

Then /^my field entries are validated$/ do
  errorMsgs = @driver.find_elements(:xpath, "//td[contains(@id, 'error_explanation')]")
  assert(errorMsgs.size == 0, "Found input error(s) in page")
end

Then /^I am redirected to a page with terms and conditions$/ do
  assertWithWait("Was not redirected to #{@baseUrl}/eula") { @driver.current_url.include?("#{@baseUrl}/eula") }
  assertText("Terms and Conditions")
  puts "Terms and Conditions displayed"
end

Then /^I receive an error that the account already exists$/ do
  step "I should see the error message \"An account with this email already exists\""
end

Then /^I am redirected to the hosting website$/ do
  assertWithWait("Was not redirected to #{PropLoader.getProps['user_registration_app_host_url']}") { 
    @driver.current_url.include?(PropLoader.getProps['user_registration_app_host_url'])
  }
end

Then /^I am directed to an acknowledgement page.$/ do
  assertText("Registration Complete")
  assertText("Email Confirmation")
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
  assert(ApprovalEngine.user_exists?(email) == false, "Account for #{email} is not removed.")
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

def initializeApprovalAndLDAP(emailConf, prod)
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'], 
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'], 
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
  ApprovalEngine.init(@ldap, nil, !prod)
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
puts email
  if ApprovalEngine.user_exists?(email)
    ApprovalEngine.remove_user(email)
  end
end
