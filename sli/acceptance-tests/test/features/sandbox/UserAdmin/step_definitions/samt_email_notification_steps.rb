=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

SAMT_EMAIL_NOTIFICATION_SUBJECT_SANDBOX = "SLC Sandbox Account - Email Confirmation"

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
end

Given /^I have a SMTP\/Email server configured$/ do
  @email_sender_name= "SLC Administrator"
  @email = PropLoader.getProps['email_imap_registration_user_email']
  defaultUser = PropLoader.getProps['email_imap_registration_user']
  defaultPassword = PropLoader.getProps['email_imap_registration_pass']
  @imap = Net::IMAP.new(PropLoader.getProps['email_imap_host'], PropLoader.getProps['email_imap_port'], true, nil, false)
  @imap.authenticate('LOGIN', defaultUser, defaultPassword)
  @imap.examine('INBOX')

  @last_id = get_last_email_id
  puts @last_id
end

Given /^the testing user "(.*?)" does not already exists in LDAP$/ do |email|
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+email, format, sessionId)
end

Then /^I enter Full Name "(.*?)" and Email "(.*?)" into the required fields$/ do |fullName, email|
  pending # express the regexp above with the code you wish you had
end

Then /^a verify email notification is sent to user$/ do
  pending # express the regexp above with the code you wish you had
end

def get_last_email_id
  ids = @imap.search(["FROM", @email_sender_name,"TO",@email])
  last_id = ids[-1]
  return last_id
end

def get_email_subject(id)
  subject = @imap.fetchimap.fetch(id, "BODY[HEADER.FIELDS (SUBJECT)]")[0].attr["BODY[HEADER.FIELDS (SUBJECT)]"]
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
      if get_email_subject == SAMT_EMAIL_NOTIFICATION_SUBJECT_SANDBOX
      found = true
      end
    end
  end
end

