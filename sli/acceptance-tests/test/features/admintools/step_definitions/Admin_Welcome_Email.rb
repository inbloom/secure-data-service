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

require 'net/imap'
require 'test/unit'
require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require_relative '../../utils/email.rb'

SAMT_WELCOME_TEST_UID_PREFIX = "SAMT_Welcome_Email_"
NEW_PASSWORD = "password1234"

Before("@RALLY_US3459") do
  extend Test::Unit::Assertions
end

After("@RALLY_US3459") do |scenario|
  cleanup_users(SAMT_WELCOME_TEST_UID_PREFIX, @mode) #for good measure, clean up users after a test
end

Given /^I have a new account with (.*?) in "([^"]*)"$/ do |groups, mode|
  cleanup_users(SAMT_WELCOME_TEST_UID_PREFIX, mode)
  @mode = mode
  @newly_created_user = create_user(SAMT_WELCOME_TEST_UID_PREFIX, groups, mode)
end

When /^I set my password$/ do
  content = check_email({:content_substring => @newly_created_user[:firstname]}) do
    @driver.get(PropLoader.getProps["admintools_server_url"] + "/forgot_passwords")
    @driver.find_element(:id, "user_id").clear
    @driver.find_element(:id, "user_id").send_keys @newly_created_user[:uid]
    @driver.find_element(:id, "submit").click
  end

  reset_password_link = nil
  content.split("\n").each do |line|
    if(/#{PropLoader.getProps["admintools_server_url"]}/.match(line))
      reset_password_link = line
    end
  end
  reset_password_link = reset_password_link.strip[0..-("<br>".size + 1)]
  puts "reset password link = #{reset_password_link}"
  @driver.get(reset_password_link)

  @welcome_email_content = check_email({:content_substring => @newly_created_user[:firstname]}) do
    @driver.find_element(:id, "new_account_password_new_pass").clear
    @driver.find_element(:id, "new_account_password_new_pass").send_keys NEW_PASSWORD
    @driver.find_element(:id, "new_account_password_confirmation").clear
    @driver.find_element(:id, "new_account_password_confirmation").send_keys NEW_PASSWORD
    if(@mode == "sandbox")
      @driver.find_element(:id, "terms_and_conditions").click
    end
    @driver.find_element(:id, "submitForgotPasswordButton").click
  end
  puts @welcome_email_content
end

Then /^I get a welcome email of (.*?)$/ do |email_type|
  puts email_type
  strings_included = []
  case email_type
    when "SLC Operator"
      strings_included = ["You can access the Admin Account Management feature from the Admin page in the right corner of the inBloom portal at:",
                          "Contact inBloom to obtain documentation on Admin Account Management."]
    when "LEA or SEA only"
      strings_included = ["You can access the Admin Account Management feature from the Admin page in the right corner of the inBloom portal at:",
                          "Contact inBloom to obtain documentation on Admin Account Management."]
    when "(LEA or SEA) and Realm Admin"
      strings_included = ["You can access several administrative features from the Admin page in upper right of the inBloom portal at:",
                          "* Admin Account Management - Use this feature to create new administrative accounts.",
                          "* Realm Administration - Use this feature to create and manage realms for your education organization.",
                          "Contact inBloom to obtain documentation on Admin Account Management."
      ]
    when "(LEA or SEA) and Ingestion"
      strings_included = ["You can access several administrative features from the Admin page in upper right of the inBloom portal at:",
                          "* Admin Account Management - Use this feature to create new administrative accounts.",
                          "* Provision Landing Zone - Use this feature for managing landing zones, required for ingesting data for your education organization.",
                          "Contact inBloom to obtain documentation on Admin Account Management."
      ]
    when "Realm Admin only"
      strings_included = ["You can access the Realm Administration feature from the Admin page in the right corner of the inBloom portal at:"]
    when "Ingestion only"
      strings_included = ["You can access the Provision Landing Zone feature from the Admin page in the right corner of the inBloom portal at:"]
    when "Sandbox Admin only"
      strings_included = ["Access Admin Account Management feature to create new administrative accounts for the sandbox.",
                          "Click the Admin link in the upper right corner of the inBloom portal to open the Admin page and access this feature:"]
    when "Sandbox Admin and Sandbox Ingestion"
      strings_included = ["* Use the Admin Account Management feature to create new administrative accounts for the sandbox.",
                          "* Use the Provision Landing Zone feature for managing landing zones, required for ingesting data for your"]
    when "Sandbox all rights"
      strings_included = ["* Use the Admin Account Management feature to create new administrative accounts for the sandbox.",
                          "* Use the Provision Landing Zone feature for managing landing zones, required for ingesting data for your",
                          "While you develop inBloom-compatible applications, refer to the documentation available at",
                          "When you are ready to register your application, use the Application Registration feature. Click the Admin"]
    when "Application Developer and Sandbox Ingestion"
      strings_included = ["Your request for an application developer account has been approved. You can use this account to develop",
                          "Use the Provision Landing Zone feature for managing landing zones, required for ingesting data for your",
                          "While you develop inBloom-compatible applications, refer to the documentation available at",
                          "When you are ready to register your application, use the Application Registration feature. Click the Admin"]
    when "Sandbox Ingestion only"
      strings_included = ["Use the Provision Landing Zone feature for managing landing zones, required for ingesting data for your"]
    when "Application Developer only"
      strings_included = ["When you are ready to register your application, select Application Registration from the"]
    when "Sandbox Admin and Application Developer"
      strings_included = ["Your request for an application developer account has been approved. You can use this account to develop",
                          "Use the Admin Account Management feature to create new administrative accounts for the sandbox.  Click the Admin link",
                          "While you develop inBloom-compatible applications, refer to the documentation available at",
                          "When you are ready to register your application, use the Application Registration feature. Click the Admin"]
  end
  strings_included.each do |string_included|
    assert(@welcome_email_content.include?(string_included), "FAILED:\n\nemail = #{@welcome_email_content}\n\nstring_included = <#{string_included}>\n\n")
  end
end

Then /^the email has a link to "([^"]*)"$/ do |substring|
  found = @welcome_email_content.include?(substring)
  puts @welcome_email_content
  assert(found, "welcome email does not have a link to #{substring}")
end

Then /^I can log in with my username and password$/ do
  @driver.get(PropLoader.getProps["admintools_server_url"])
  step "I submit the credentials \"#{@newly_created_user[:uid]}\" \"#{NEW_PASSWORD}\" for the \"Simple\" login page"
  actual_page_content = @driver.find_element(:tag_name, "body")
  expected_page_content = "Admin Tool"
  assert(actual_page_content.text.include?(expected_page_content), "Cannot find page id: #{expected_page_content}")
end

def cleanup_users(uid_prefix, mode)
  if mode == "production"
    idpRealmLogin("operator", nil)
  else
    idpRealmLogin("sandboxoperator", nil)
  end

  sessionId = @sessionId
  format = "application/json"

  # clean up users
  restHttpGet("/users", format, sessionId)
  users = JSON.parse(@res.body)
  prefix = uid_prefix + Socket.gethostname
  assert(users.kind_of?(Array), "Problem getting users: #{users}")
  users.each do |user|
    if(/#{prefix}/.match("#{user["uid"]}"))
      #puts "deleting user #{user['uid']}"
      restHttpDelete("/users/#{user['uid']}", format, sessionId)
    end
  end
end

def create_user(uid_prefix, groups, mode)
  if mode == "production"
    idpRealmLogin("operator", nil)
  else
    idpRealmLogin("sandboxoperator", nil)
  end

  sessionId = @sessionId
  format = "application/json"

  # create the new user
  prefix = uid_prefix + Socket.gethostname
  letters = [('a'..'z'),('A'..'Z')].map{|i| i.to_a}.flatten
  firstname, lastname = (0..1).map{(0..50).map{letters[rand(letters.length)]}.join}
  uid = prefix + lastname
  groups = groups.split(',').map{|group| group.strip}
  new_user = {
      "uid" => uid,
      "groups" => groups,
      "fullName" => "#{firstname} #{lastname}",
      "password" => uid + "1234",
      "email" => PropLoader.getProps['email_imap_registration_user_email'],
      "homeDir" => "/dev/null"
  }
  if (["SLC Operator", "Sandbox SLC Operator"] & groups).empty?
    new_user["tenant"] = "Midgar"
    new_user["edorg"] = "IL-DAYBREAK"
  end
  puts "creating user = #{new_user}"
  restHttpPost("/users", new_user.to_json, format, sessionId)
  {:firstname => firstname, :uid => uid, :password => new_user["password"]}
end
