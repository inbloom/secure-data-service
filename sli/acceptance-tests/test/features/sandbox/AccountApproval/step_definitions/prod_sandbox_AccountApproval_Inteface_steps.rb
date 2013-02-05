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
require "socket"

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

Before do
  extend Test::Unit::Assertions
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 120)
end

Given /^I navigate to the account management page$/ do
  url =PropLoader.getProps['admintools_server_url']+"/account_managements"
  @driver.get url
end

Given /^LDAP server has been setup and running$/ do
  @email = "devldapuser"+Socket.gethostname+"@slidev.org"
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'], 
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'], 
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
end

Given /^there are accounts in requests pending in the system$/ do
  clear_users()
  sleep(1)
  user_info = {
      :first => "Loraine",
      :last => "Plyler_"+Socket.gethostname, 
       :email => @email,
       :password => "secret", 
       :emailtoken => "token",
       :vendor => "Macro Corp",
       :status => "pending",
       :homedir => "test",
       :uidnumber => "500",
       :gidnumber => "500",
       :emailAddress => @email
   }
  @ldap.create_user(user_info)
  sleep(1)
end

And /^I got the 404 page$/ do
  assertText("The page you were looking for doesn't exist")    
end

When /^I hit the Admin Application Account Approval page$/ do
  url = PropLoader.getProps['admintools_server_url']+"/account_managements"
  @driver.get url
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end

Then /^I see a table with headings of "([^"]*)" and "([^"]*)" and "([^"]*)" and "([^"]*)" and "([^"]*)"$/ do |vendor, user_name, last_update, status, action|
 vendor_header= @driver.find_element(:id,"vendor")
 assert(vendor_header.text==vendor,"didnt find #{vendor} in the heading")
 user_name_header= @driver.find_element(:id,"user_name")
 assert(user_name_header.text==user_name,"didnt find #{user_name} in the heading")
 last_update_header= @driver.find_element(:id,"last_update")
 assert(last_update_header.text==last_update,"didnt find #{last_update} in the heading")
 status_header= @driver.find_element(:id,"status")
 assert(status_header.text==status,"didnt find #{status} in the heading")
 action_header= @driver.find_element(:id,"action")
 assert(action_header.text==action,"didnt find #{action} in the heading")
end

Then /^on the next line there is vendor name in the "([^"]*)" column$/ do |arg1|

  vendors=@driver.find_elements(:xpath,"//td[@class='vendor']")
  assert(vendors.length>0,"didnt find vendor name in #{arg1} column")
end

Then /^User Name in the "([^"]*)" column$/ do |arg1|
  user_names=@driver.find_elements(:xpath,"//td[@class='user_name']")
  assert(user_names.length>0,"didnt find User name in #{arg1} column")
end

Then /^last update date in the "([^"]*)" column$/ do |arg1|
  last_updates=@driver.find_elements(:xpath,"//td[@class='last_update']")
  assert(last_updates.length>0,"didnt find Last Update in #{arg1} column")
end

Then /^status in the "([^"]*)" column$/ do |arg1|
  status_elements=@driver.find_elements(:xpath,"//td[@class='account_management_table_pendingStatus']")
  assert(status_elements.length>0,"didnt find status in #{arg1} column")
end

Then /^the "([^"]*)" column has (\d+) buttons "([^"]*)", "([^"]*)", "([^"]*)", and "([^"]*)"$/ do |arg1,arg2,approve_button, reject_button, disable_button, enable_button|
 approve_buttons=@driver.find_elements(:xpath,"//input[@value='Approve']")
 assert(approve_buttons.length>0,"didnt find #{approve_button} in action column")
 reject_buttons=@driver.find_elements(:xpath,"//input[@value='Reject']")
 assert(reject_buttons.length>0,"didnt find #{reject_button} in action column")
 disable_buttons=@driver.find_elements(:xpath,"//input[@value='Disable']")
 assert(disable_buttons.length>0,"didnt find #{disable_button} in action column")
 enable_buttons=@driver.find_elements(:xpath,"//input[@value='Enable']")
 assert(enable_buttons.length>0,"didnt find #{enable_button} in action column")
end

Given /^there is a "([^"]*)" production account request for vendor "([^"]*)"$/ do |status,vendor|
  create_account(status, vendor)
end

Then /^I see one account with name "([^"]*)"$/ do |user_name|
 user_name = user_name+"_"+Socket.gethostname
  puts "And i am looking to find the element with id username.", user_name
  user=@driver.find_element(:id,"username."+user_name)
  assert(user.text==user_name,"didnt find the account with name #{user_name}")
  @user_name=user_name
end

Then /^his account status is "([^"]*)"$/ do |arg1|
  status=@driver.find_element(:id,"status."+@user_name)
  assert(status.text==arg1,"user account status is not #{arg1}")
end

When /^I click the "([^"]*)" button$/ do |button_name|
  buttons = @driver.find_elements(:id,button_name.downcase+"_button_"+@user_name)
  puts "Warning: not exactly one element found: #{buttons.size}" unless buttons.size == 1
  @driver.find_element(:id,button_name.downcase+"_button_"+@user_name).click
end

When /^I am asked "([^"]*)"$/ do |arg1|
     # do nothing
end

Then /^his account status changed to "([^"]*)"$/ do |arg1|
  step "I navigate to the account management page"
  found = false
  statuses = nil
  5.times do
    @driver.navigate.refresh
    statuses = @driver.find_elements(:id,"status."+@user_name)
    statuses.each do |status|
      if status.text==arg1
        found=true
      end
    end
    break if found
    sleep(10)
  end


  assert(found,"user account status is not #{arg1}, was instead #{statuses.map{|s|s.text}}")
  clear_users()
end



Given /^there is an approved sandbox account  for vendor "([^"]*)"$/ do |vendor|
 clear_users()
  sleep(1)
  user_info = {
      :first => "Loraine",
      :last => "Plyler_"+Socket.gethostname, 
       :email => @email,
       :password => "secret", 
       :emailtoken => "token",
       :vendor => vendor,
       :status => "approved",
       :homedir => "test",
       :uidnumber => "500",
       :gidnumber => "500",
       :emailAddress => @email
   }
   puts "And my email is ", @email
  @ldap.create_user(user_info)
  sleep(1)
end

def create_account(status, vendor)
  clear_users()
  sleep(1)
  user_info = {
      :first => "Loraine",
      :last => "Plyler_"+Socket.gethostname, 
       :email => @email,
       :password => "secret", 
       :emailtoken => "token",
       :vendor => vendor,
       :status => status,
       :homedir => "test",
       :uidnumber => "500",
       :gidnumber => "500",
       :emailAddress => @email
   }
  @ldap.create_user(user_info)
  sleep(1)
end

def clear_users
  # remove all users that have this hostname in their email address
  if @ldap.user_exists?(@email)
      @ldap.delete_user(@email)    
    end
end

def assertText(text)
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text \"#{text}\"")
end
