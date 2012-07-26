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
require 'approval'
require "mongo" 
require 'rumbster'
require 'digest'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

Before do 
   @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
   @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
end



Given /^LDAP server has been setup and running$/ do
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'], 
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'], 
                          PropLoader.getProps['ldap_admin_pass'])
  @email_sender_name= "Administrator"
  @email_sender_address= "noreply@slidev.org"
  @email_conf = {
    :host =>  PropLoader.getProps['email_smtp_host'],
    :port => PropLoader.getProps['email_smtp_port'],
    :sender_name => @email_sender_name,
    :sender_email_addr => @email_sender_address
  }
end

When /^I navigate to the sandbox user account management page$/ do
  samt_url = PropLoader.getProps['admintools_server_url']+PropLoader.getProps['samt_app_suffix']
  @driver.get samt_url
end


Then /^I am redirected to "(.*?)" page$/ do |pageTitle|
  assertWithWait("Failed to navigate to the #{pageTitle} page")  {@driver.page_source.index("#{pageTitle}") != nil}
end

Then /^I will be redirected to "(.*?)" login page$/ do |idpType|
  step "I was redirected to the \"#{idpType}\" IDP Login page"
end

When /^I hit the sandbox user account management app list all users page$/ do
  step "I navigate to the sandbox user account management page"
end

Then /^I see a table with headings of "(.*?)" and "(.*?)" and "(.*?)" and "(.*?)" and "(.*?)" and "(.*?)"$/ do |fullName, email, role,edorg, dateCreated, actions|
  check_heading(fullName)
  check_heading(email)
  check_heading(role)
  check_heading(edorg)
  check_heading(dateCreated)
  check_heading(actions)
end

Then /^I see a user with Full Name is "(.*?)" in the table$/ do | fullName|
 fullName = fullName.gsub("hostname",Socket.gethostname)
 fullName_element = @driver.find_element(:xpath,"//tr[td='#{fullName}']")
 assert(fullName_element!=nil,"can not find user with full name is #{fullName}")
  @userFullName = fullName
end

Then /^I see "(.*?)" has "(.*?)" and "(.*?)" role$/ do |full_name, role1, role2|
    roles=[ role1, role2 ]
    roles.sort! { |a,b| a.downcase <=> b.downcase }
    step "I see a user with Full Name is \"#{full_name}\" in the table"
    roles_element = @driver.find_element(:id,"#{full_name}_role")
    displayed_roles = roles_element.text().split(",")
    displayed_roles.each do |str|
        str.strip!
    end
    displayed_roles.sort! { |a,b| a.downcase <=> b.downcase }
    assert(roles.size == displayed_roles.size, "roles size do not match")
    for idx in 0 ... roles.size
        assert(roles[idx] == displayed_roles[idx], "user roles do not match #{roles[idx]} #{displayed_roles[idx]}")
    end
end

Then /^the user "(.*?)" is "(.*?)"$/ do |key, value|
  element = @driver.find_element(:id,"#{@userFullName}_#{key}")
  assert(element.text==value,"the user #{key} is #{element.text} but expected #{value}")
end

Then /^I will get an error message that "(.*?)"$/ do |message|
  assertText(message)
end

Given /^There is a sandbox user with "(.*?)" and "(.*?)" in LDAP Server$/ do |fullName, role|
  @test_env = "sandbox"
  firstName = fullName.split(" ")[0]
  lastName = fullName.split(" ")[1].gsub("hostname",Socket.gethostname)
  groups = Array.new
  groups.push(role)
  uid=firstName.downcase+"_"+lastName.downcase
  new_user=build_user(uid,firstName,lastName,groups,"sandboxadministrator@slidev.org","")
  
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
  restHttpPost("/users", new_user.to_json, format, sessionId)
  
end

When /^I click on "(.*?)" icon$/ do |buttonName|
  @driver.find_element(:xpath, "//button[@id='#{@userFullName}_#{buttonName}']/a").click
end


Then /^I am asked to confirm the delete action$/ do
   #do nothing
end

When /^I confirm the delete action$/ do
  begin
    @driver.switch_to.alert.accept
  rescue
  end
  sleep(3)
end

Then /^that user is removed from LDAP$/ do
 if @test_env == "sandbox"
   idpRealmLogin("sandboxoperator", nil)
  else
  idpRealmLogin("operator", nil)
  end
  sessionId = @sessionId
  format = "application/json"
  restHttpGet("/users",format,sessionId)
  notFound = true
  result = JSON.parse(@res.body)
  result.each do |user|
  if user["fullName"] == @userFullName
  notFound = false 
  end
  puts user["fullName"]
  end
  assert(notFound,"the user #{@userFullName} is not removed from LDAP") 
end

Then /^the user entry is removed from the table$/ do
element =nil
  begin
   element = @driver.find_element(:xpath,"//tr[td='#{@userFullName}']")
  rescue
  end
  assert(element==nil,"the user #{@userFullName} is not removed from the table")
end

Then /^I see my Full Name is "(.*?)" in the table$/ do |fullName|
  step "I see a user with Full Name is \"#{fullName}\" in the table"
end

Then /^the "(.*?)" button is disabled$/ do |buttonName|
delete_button=nil
 # begin
  delete_button = @driver.find_element(:xpath,"//button[@id='#{@userFullName}_#{buttonName}' and @disabled = 'disabled']")
#  rescue
 # end
  assert(delete_button!=nil,"the #{buttonName} button is not disabled")
end

When /^I click on (.*?) button$/ do |buttonName|
  @driver.find_element(:xpath, "//a[text()=#{buttonName}]").click
end

Given /^the testing user does not already exists in LDAP$/ do
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+Socket.gethostname+"_testuser@testwgen.net", format, sessionId)
end
    
When /^I have entered Full Name and Email into the required fields$/ do
  @driver.find_element(:name, 'user[fullName]').send_keys "Sandbox AcceptanceTests"
  @driver.find_element(:name, 'user[email]').send_keys Socket.gethostname+"_testuser@testwgen.net"
end

#And I can select "Application Developer" from a choice between a "Sandbox Administrator" and "Application Developer" and "Ingestion User" Role 
Then /^I can select "(.*?)" from a choice between a (.*?), (.*?) and (.*?) Role$/ do |role, choice1, choice2, choice3| 
    drop_down = @driver.find_element(:id, "user_primary_role")
    drop_down.click
    for i in [ choice1, choice2, choice3 ] do
        option = drop_down.find_element(:xpath, ".//option[text()=#{i}]")
        assert(option != nil)
    end
   
    drop_down.send_keys "#{role}.chr" 
    #option = dropDown.find_element(:xpath, ".//option[text()=#{role}]")
    #option.send_keys "\r"
end

Then /^I can also check "(.*?)" Role$/ do |r|
    @driver.find_element(:id, "#{r.downcase.gsub(" ", "_")}_role").click 
end
 
When /^I click (.*?) link$/ do |link|
  @driver.find_element(:xpath, "//a[text()=#{link}]").click
end 

When /^I click "Save"$/ do
  @driver.find_element(:name, "commit").click
end

Then /^a "(.*?)" message is displayed$/ do |message|
    assertText(message)
end

def check_heading(heading_name)
heading_element=@driver.find_element(:xpath, "//tr[th='#{heading_name}']")
assert(heading_element!=nil,"can not find heading name #{heading_name}")
end

def assertText(text)
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text \"#{text}\"")
end

def build_user(uid,firstName, lastName,groups,tenant,edorg)
new_user = {
      "uid" => uid,
      "groups" => groups,
      "firstName" => firstName,
      "lastName" => lastName,
      "password" => "#{uid}1234",
      "email" => "testuser@wgen.net",
      "tenant" => tenant,
      "edorg" => edorg,
      "homeDir" => "/dev/null"
  }
  append_hostname(new_user)
  
end
  
def append_hostname(user )
  oldUid = user["uid"]
  newUid = oldUid+"_"+Socket.gethostname
  user.merge!({"uid" => newUid})
  return user
end




