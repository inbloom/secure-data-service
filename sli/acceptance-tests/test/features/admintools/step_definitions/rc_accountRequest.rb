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
require "socket"
require 'net/imap'
require_relative '../../cross_app_tests/step_definitions/rc_integration_samt'
require_relative '../../liferay/step_definitions/all_steps.rb'


Before do
  @rc_admintools_url = "https://rcadmin.slidev.org"
  @rc_portal_url = "https://rcportal.slidev.org/portal"
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I go to the account registration page on RC$/ do
  @driver.get @rc_admintools_url + "/registration"
end

Given /^I go to the portal page on RC$/ do
  @driver.get @rc_portal_url
end

Given /^I received an email to verify my email address$/ do
  subject_string = "Email Confirmation"
  content_string = "RCTest"
  content = check_email_for_verification(subject_string, content_string)
  content.split("\n").each do |line|
    if(/#{@rc_admintools_url}/.match(line))
      @email_verification_link = line
    end
  end
  assert(!@email_verification_link.nil?, "Cannot find the link from the email")
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I click the link to verify my email address$/ do
  steps "Given I have an open web browser"
  @driver.get @email_verification_link
end

When /^I approve his account$/ do
  @driver.find_element(:id,"approve_button_"+@user_name).click
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should be notified that my email is verified$/ do
  assertText("Email Confirmed")
end

Then /^I should receive an email telling me my account is approved$/ do
  subject_string = "Welcome to the Shared Learning Collaborative"
  content_string = "Welcome RCTest"
  content = check_email_for_verification(subject_string, content_string)
  assert(!content.nil?, "Cannot find email telling me that my account is approved")
end

Then /^I should see an account with name "([^\"]*)"$/ do |user_name|
  user = @driver.find_element(:id, "username."+user_name)
  assert(user.text == user_name, "Didnt find the account with name #{user_name}")
  @user_name = user_name
end

Then /^I click on Account Approval$/ do
  @driver.find_element(:link_text, 'Account Approval').click
end

Then /^I should be on the Authorize Developer Account page$/ do
  assertText("Authorize Developer Account")
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

private

def check_email_for_verification(subject_substring = nil, content_substring = nil)
  imap_host = 'imap.gmail.com'
  imap_port = 993
  imap_user = 'testdev.wgen@gmail.com'
  imap_password = 'testdev.wgen1234'
  not_so_distant_past = Date.today.prev_day
  not_so_distant_past_imap_date = "#{not_so_distant_past.day}-#{Date::ABBR_MONTHNAMES[not_so_distant_past.month]}-#{not_so_distant_past.year}"
  imap = Net::IMAP.new(imap_host, imap_port, true, nil, false)
  imap.login(imap_user, imap_password)
  imap.examine('INBOX')

  retry_attempts = 30
  retry_attempts.times do
    sleep 1
    messages_new = imap.search(['SINCE', not_so_distant_past_imap_date])
    unless(messages_new.empty?)
      messages = imap.fetch(messages_new, ["BODY[HEADER.FIELDS (SUBJECT)]", "BODY[TEXT]"])
      messages.each do |message|
        content = message.attr["BODY[TEXT]"]
        subject = message.attr["BODY[HEADER.FIELDS (SUBJECT)]"]
        if((content_substring.nil? || (!content.nil? && content.include?(content_substring))) &&
            (subject_substring.nil? || (!subject.nil? && subject.include?(subject_substring))))
          return content
        end
      end
    end
  end
  imap.disconnect
  fail("timed out getting email with subject substring = #{subject_substring}, content substring = #{content_substring}")
end
