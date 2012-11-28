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

def check_email(config = {})
  imap_host = config[:imap_host] || PropLoader.getProps['email_imap_host']
  imap_port = config[:imap_port] || PropLoader.getProps['email_imap_port']
  imap_username = config[:imap_username] || PropLoader.getProps['email_imap_registration_user']
  imap_password = config[:imap_password] || PropLoader.getProps['email_imap_registration_pass']
  content_substring = config[:content_substring].gsub(/\s/, '') # remove spaces because the imap client add unnecessary spaces
  subject_substring = config[:subject_substring].gsub(/\s/, '') # remove spaces because the imap client add unnecessary spaces
  initial_wait_time = config[:initial_wait_time] || 1
  retry_attempts = config[:retry_attempts] || 30
  retry_wait_time = config[:retry_wait_time] || 1

  imap = Net::IMAP.new(imap_host, imap_port, true, nil, false)
  puts "username = #{imap_username}, password = #{imap_password}"
  imap.login(imap_username, imap_password)
  imap.examine('INBOX')
  past = Date.today.prev_day.prev_day
  past_date = "#{past.day}-#{Date::ABBR_MONTHNAMES[past.month]}-#{past.year}"
  messages_before = imap.search(['SINCE', past_date])

  yield

  sleep initial_wait_time
  retry_attempts.times do
    sleep retry_wait_time
    imap.examine('INBOX')

    messages_after = imap.search(['SINCE', past_date])
    messages_new = messages_after - messages_before
    messages_before = messages_after
    unless(messages_new.empty?)
      messages = imap.fetch(messages_new, ["BODY[HEADER.FIELDS (SUBJECT)]", "BODY[TEXT]"])
      messages.each do |message|
        content = message.attr["BODY[TEXT]"].gsub(/\s/, '') # remove spaces because the imap client add unnecessary spaces
        subject = message.attr["BODY[HEADER.FIELDS (SUBJECT)]"].gsub(/\s/, '') # remove spaces because the imap client add unnecessary spaces
        if((content_substring.nil? || (!content.nil? && content.include?(content_substring))) &&
            (subject_substring.nil? || (!subject.nil? && subject.include?(subject_substring))))
          return content
        else
          puts "incorrect email content = #{content}"
          puts "incorrect email subject = #{subject}"
        end
      end
    end
  end
  imap.disconnect
  fail("timed out getting email with subject substring = #{subject_substring}, content substring = #{content_substring}")
end