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


require_relative 'accountRequest.rb'

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I go to the account registration page on RC$/ do
  @driver.get PropLoader.getProps["admintools_server_url"] + "/registration"
end

Given /^I received an email to verify my email address$/ do
  subject_string = "Email Confirmation"
  content_string = "RCTest"
  content = check_email_for_verification(subject_string, content_string)
  content.split("\n").each do |line|
    if(/#{PropLoader.getProps["admintools_server_url"]}/.match(line))
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

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should be notified that my email is verified$/ do
  assertText("Email Confirmed")
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

private

def check_email_for_verification(subject_substring = nil, content_substring)
  imap_host = 'imap.gmail.com'
  imap_port = 993
  imap_user = 'testdev.wgen@gmail.com'
  imap_password = 'testdev.wgen1234'
  not_so_distant_past = Date.today.prev_day.prev_day
  not_so_distant_past_imap_date = "#{not_so_distant_past.day}-#{Date::ABBR_MONTHNAMES[not_so_distant_past.month]}-#{not_so_distant_past.year}"
  imap = Net::IMAP.new(imap_host, imap_port, true, nil, false)
  imap.login(imap_user, imap_password)
  imap.examine('INBOX')
  messages_before = imap.search(['SINCE', not_so_distant_past_imap_date])
  imap.disconnect

  retry_attempts = 30
  retry_attempts.times do
    sleep 1
    imap = Net::IMAP.new(imap_host, imap_port, true, nil, false)
    imap.login(imap_user, imap_password)
    imap.examine('INBOX')

    messages_after = imap.search(['SINCE', not_so_distant_past_imap_date])
    messages_new = messages_after - messages_before
    messages_before = messages_after
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
    imap.disconnect
  end
  fail("timed out getting email with subject substring = #{subject_substring}, content substring = #{content_substring}")
end
