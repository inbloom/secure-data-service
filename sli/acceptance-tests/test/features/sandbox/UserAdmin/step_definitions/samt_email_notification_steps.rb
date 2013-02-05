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
require "mongo"
require 'net/imap'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

SAMT_EMAIL_NOTIFICATION_SUBJECT_SANDBOX = "inBloom Sandbox Account - Email Confirmation"
SAMT_EMAIL_NOTIFICATION_SUBJECT_PROD = "inBloom Administrator Account - Email Confirmation"
TEST_EMAIL = "peacefrog@slidev.org"
TEST_EMAIL_USER ="peacefrog"
TEST_EMAIL_PASS ="demouser"

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
end

After do
  remove_user(TEST_EMAIL)
end

Given /^I have a SMTP\/Email server configured$/ do
  @email_sender_name= "SLC Administrator"
  @email = TEST_EMAIL
  defaultUser = TEST_EMAIL_USER
  defaultPassword = TEST_EMAIL_PASS
  @imap = Net::IMAP.new(PropLoader.getProps['email_imap_host'], PropLoader.getProps['email_imap_port'], true, nil, false)
  @imap.authenticate('LOGIN', defaultUser, defaultPassword)

  @last_id = get_last_email_id
  puts "current email id is: #{@last_id}"
end

Given /^the testing user "(.*?)" does not already exists in LDAP$/ do |email|
  remove_user(email)
  @test_env="sandbox"
end

Given /^the prod testing user "(.*?)" does not already exists in LDAP$/ do |email|
  remove_user(email)
  @test_env="prod"
end

When /^I navigate to the User Management Page$/ do
  step "I navigate to the sandbox user account management page"
end

Then /^I enter Full Name "(.*?)" and Email "(.*?)" into the required fields$/ do |fullName, email|
  @explicitWait.until{@driver.find_element(:name, 'user[fullName]')}.send_keys fullName
  @explicitWait.until{@driver.find_element(:name, 'user[email]')}.send_keys email
end

Then /^a verify email notification is sent to user$/ do
  sleep(3)
  verify_email

end

def get_last_email_id
  @imap.examine('INBOX')
  ids = @imap.search(["FROM", @email_sender_name,"TO",@email])
  last_id = ids[-1]
  return last_id
end

def get_email_subject(id)
  subject = @imap.fetch(id, "BODY[HEADER.FIELDS (SUBJECT)]")[0].attr["BODY[HEADER.FIELDS (SUBJECT)]"]
  return subject
end

def get_email_content(id)
  content=@imap.fetch(id, "BODY[TEXT]")[0].attr["BODY[TEXT]"]
end

def verify_email
  current_id = get_last_email_id
  if  (current_id <= @last_id)
  found = false
  else
    ids = (@last_id+1)..current_id
    ids.each do |id|

      if ((get_email_subject(id).include? SAMT_EMAIL_NOTIFICATION_SUBJECT_SANDBOX) && @test_env =="sandbox")||((get_email_subject(id).include? SAMT_EMAIL_NOTIFICATION_SUBJECT_PROD) && @test_env =="prod")
        found = true
        puts "current email id is: #{id}"
        puts get_email_subject(id)
        puts get_email_content(id)
      end
    end
  end
  assert(found,"did not receive the samt notification email in mailbox #{TEST_EMAIL}")
end

def remove_user(email)
  # remove user created for sandbox
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+email, format, sessionId)

  # remove user created for production
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+email, format, sessionId)
end

