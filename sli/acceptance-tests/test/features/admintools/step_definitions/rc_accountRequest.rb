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


require 'net/imap'
require_relative 'accountRequest.rb'

Given /^I go to the account registration page on RC$/ do
  @driver.get "https://rcadmin.slcedu.org/registration"
end

private

def check_email_for_verification(subject_substring = "Get Gmail on your mobile phone", content_substring = "The days")
  imap_host = "imap.gmail.com"
  imap_port = "993"
  imap_user = "testdev.wgen@gmail.com"
  imap_password = "testdev.wgen1234"
  imap = Net::IMAP.new(imap_host, imap_port, true)
  imap.login(imap_user, imap_password)
  imap.examine('INBOX')
  not_so_distant_past = Date.today.prev_day.prev_day
  not_so_distant_past_imap_date = "#{not_so_distant_past.day}-#{Date::ABBR_MONTHNAMES[not_so_distant_past.month]}-#{not_so_distant_past.year}"
  messages_before = imap.search(['SINCE', not_so_distant_past_imap_date])
  puts messages_before
  imap.disconnect

  retry_attempts = 30
  retry_attempts.times do
    sleep 1
    imap = Net::IMAP.new(imap_host, imap_port, true)
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
          puts content
        end
      end
    end
    imap.disconnect
  end
  fail("timed out getting email with subject substring = #{subject_substring}, content substring = #{content_substring}")
end

Given /^test$/ do
  check_email_for_verification
end
