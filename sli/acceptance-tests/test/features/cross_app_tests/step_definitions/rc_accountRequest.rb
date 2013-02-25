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

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

require_relative '../../utils/email.rb'

Given /^I go to the account registration page on RC$/ do
  @driver.get PropLoader.getProps['admintools_server_url'] + PropLoader.getProps['registration_app_suffix']
end

Given /^I go to the mini sandbox account registration page$/ do
  @baseUrl = PropLoader.getProps['minisb_admintools_server_url']
  @driver.get PropLoader.getProps['minisb_admintools_server_url'] + PropLoader.getProps['registration_app_suffix']
end

Given /^I go to the portal page on RC$/ do
  @driver.get PropLoader.getProps['portal_server_address'] + PropLoader.getProps['portal_app_suffix']
end

Given /^I am running in Sandbox mode$/ do
  @mode = "SANDBOX"
end

Given /^when I click Accept$/ do 

  if (@mode == "SANDBOX")
    @imap_username = PropLoader.getProps['developer_sb_email_imap_registration_user']
    @imap_password = PropLoader.getProps['developer_sb_email_imap_registration_pass']
  else
    @imap_username = PropLoader.getProps['developer_email_imap_registration_user']
    @imap_password = PropLoader.getProps['developer_email_imap_registration_pass']
  end

  @content = check_email({:imap_host => PropLoader.getProps['email_imap_hostname'],
                         :imap_port => PropLoader.getProps['email_imap_portname'],
                         :content_substring => "RCTest",
                         :subject_substring => "Email Confirmation",
                         :imap_username => @imap_username ,
                         :imap_password => @imap_password }) do

    @driver.find_element(:xpath, "//input[contains(@id, 'accept')]").click
    puts "Accepted Terms and Conditions."

  end
end

Given /^I received an email to verify my email address$/ do
  if (@mode == "SANDBOX") 
    admin_tools_server = PropLoader.getProps['admintools_server_url']
  else
    admin_tools_server = PropLoader.getProps['minisb_admintools_server_url']
  end
  puts @content
  puts admin_tools_server
  @content.split("\n").each do |line|
    if(/#{admin_tools_server}/.match(line))
      @email_verification_link = line
      puts @email_verification_link
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

Then /^he should receive an email telling him his account is approved$/ do
  subject_string = "Welcome to the inBloom"
  subject_string = "Welcome to the inBloom Developer Sandbox" if (@mode == "SANDBOX")
  content_string = "Welcome RCTest"
  content = check_email_for_verification(subject_string, content_string)
  assert(!content.nil?, "Cannot find email telling me that my account is approved")
end

Then /^I should see an account with name "([^\"]*)"$/ do |user_name|
  user = @driver.find_element(:id, "username."+user_name)
  assert(user.text == user_name, "Didnt find the account with name #{user_name}")
  @user_name = user_name
end

Then /^I click on Approve Account$/ do
  @driver.find_element(:link_text, 'Approve Account').click
end

Then /^I should be on the Authorize Developer Account page$/ do
  assertText("Authorize Developer Account")
end

Then /^I am redirected to the developer get-started page$/ do
  assertWithWait("Was not redirected to #{PropLoader.getProps['sb_get_started']}") {
    @driver.current_url.include?(PropLoader.getProps['sb_get_started'])
  }
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

private

def check_email_for_verification(subject_substring = nil, content_substring = nil)

  if (@mode == "SANDBOX")
    @imap_username = PropLoader.getProps['developer_sb_email_imap_registration_user']
    @imap_password = PropLoader.getProps['developer_sb_email_imap_registration_pass']
  else
    @imap_username = PropLoader.getProps['developer_email_imap_registration_user']
    @imap_password = PropLoader.getProps['developer_email_imap_registration_pass']
  end

  imap_host = PropLoader.getProps['email_imap_hostname'] 
  imap_port = PropLoader.getProps['email_imap_portname'] 
  imap_user = @imap_username
  imap_password = @imap_password 
  not_so_distant_past = Date.today.prev_day
  not_so_distant_past_imap_date = "#{not_so_distant_past.day}-#{Date::ABBR_MONTHNAMES[not_so_distant_past.month]}-#{not_so_distant_past.year}"
  imap = Net::IMAP.new(imap_host, imap_port, true, nil, false)
  imap.login(imap_user, imap_password)
  imap.examine('INBOX')

  retry_attempts = 60
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
