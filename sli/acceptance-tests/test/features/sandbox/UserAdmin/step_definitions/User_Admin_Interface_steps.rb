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
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

Before do
  extend Test::Unit::Assertions
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 120)
end

When /^I navigate to the sandbox user account management page$/ do
  samt_url = PropLoader.getProps['admintools_server_url'] + PropLoader.getProps['samt_app_suffix']
  @driver.get samt_url
end

Then /^I am redirected to "(.*?)" page$/ do |pageTitle|
  begin
    assertText(pageTitle)
  rescue Exception => e
    puts @driver.page_source
    raise e
  end
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
  fullName = fullName.gsub("hostname", Socket.gethostname)
  fullName_element = @explicitWait.until{@driver.find_element(:xpath,"//tr[td='#{fullName}']")}

  assert_not_nil(fullName_element, "Cannot find user with full name is #{fullName}")
  @userFullName = fullName
end

Then /^I see "(.*?)" has "(.*?)" and "(.*?)" role$/ do |full_name, role1, role2|
  roles=[ role1, role2 ]
  roles.sort! { |a,b| a.downcase <=> b.downcase }
  step "I see a user with Full Name is \"#{full_name}\" in the table"
  roles_element = @explicitWait.until{@driver.find_element(:id,"#{full_name}_role")}
  displayed_roles = roles_element.text().split(",")
  displayed_roles.each do |str|
    str.strip!
  end
  displayed_roles.sort! { |a,b| a.downcase <=> b.downcase }
  assert_equal(roles.size, displayed_roles.size, "roles size do not match")
  for idx in 0 ... roles.size
    assert_equal(roles[idx], displayed_roles[idx], "user roles do not match #{roles[idx]} #{displayed_roles[idx]}")
  end
end

Then /^the user "(.*?)" is "(.*?)"$/ do |key, value|
  element = @explicitWait.until{@driver.find_element(:id,"#{@userFullName}_#{key}")}
  assert_equal(value, element.text,"the user #{key} is #{element.text} but expected #{value}")
end

Then /^I will get an error message that "(.*?)"$/ do |message|
  assertText(message)
end

Given /^There is a sandbox user with "(.*?)" and "(.*?)" in LDAP Server$/ do |fullName, role|
  @test_env = "sandbox"
  new_user = create_new_user(fullName, role)
  puts new_user

  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
  puts restHttpPost("/users", new_user.to_json, format, sessionId)

end

Given /^There is a sandbox user with "(.*?)", "(.*?)", "(.*?)", and "(.*?)" in LDAP Server$/ do |full_name, role, addition_roles, email|
  @test_env = "sandbox"
  new_user = create_new_user(full_name, role, addition_roles)
  new_user['email'] = email.gsub("hostname", Socket.gethostname)
  new_user['uid'] = new_user['email']

  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"

  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
  restHttpPost("/users", new_user.to_json, format, sessionId)
  @user_full_name = new_user['fullName']
  @user_unique_id = new_user['uid']
end

When /^I click the "(.*?)" link for "(.*?)"$/ do |button_name, user_name|
  @user_full_name = user_name.gsub("hostname", Socket.gethostname)
  @explicitWait.until{@driver.find_element(:xpath, "//a[@id='#{@user_full_name}_#{button_name}']")}.click
end

Then /^the (.*?) field is prefilled with "(.*?)"$/ do |field_name, value|
  field=getField(field_name)
  value=value.gsub("hostname", Socket.gethostname)
  assert_equal(value, field.attribute("value"))
end

Then /^the Role combobox is populated with (.*?)$/ do |primary_role|
  drop_down = @explicitWait.until{@driver.find_element(:id, "user_primary_role")}
  option = @explicitWait.until{drop_down.find_element(:xpath, ".//option[text()=#{primary_role}]")}
  assert_equal("true", option.attribute("selected"), "#{primary_role} does not match what's expected}")
end

Then /^the Role checkbox is checked with "(.*?)"$/ do |additional_role|
  roles = additional_role.split(",")
  roles.each do |str|
    str.strip!
  end
  checkboxes = @explicitWait.until{@driver.find_elements(:xpath, "//form/fieldset/div/div/label/input[@type=\"checkbox\"]")}
  checkboxes.each do |checkbox|
    value = checkbox.attribute("value")
    if checkbox.attribute("checked")
      assert(roles.include?(value), "Checkbox #{value} should not be checked!")
    else
      assert(!roles.include?(value), "Checkbox #{value} should be checked!")
    end
  end
end

Then /^I can update the (.*?) field to "(.*?)"$/ do |field_name, new_value|
  field = getField(field_name)
  field.clear
  if field_name == "\"Tenant\"" or field_name == "\"EdOrg\""  #don't localize tenant and edorg value
    value = new_value
  else
    value = localize(new_value)
  end
  field.send_keys value
  if field_name == "\"Full Name\""
    @user_full_name = value
    @userFullName = value
  end
  if field_name == "\"Email\""
    @user_email = value
  end
end

Then /^I can delete text in (.*?) field$/ do |field_name|
  field = getField(field_name)
  field.clear
end

Then /^the user has "(.*?)" updated to "(.*?)"$/ do |table_header, new_value|
  if table_header == "Tenant" or table_header == "EdOrg" #don't localize tenant and edorg value
    value = new_value
  else
    value = localize(new_value);
  end
  td = @explicitWait.until{@driver.find_element(:id, "#{@user_full_name}_#{table_header.downcase.gsub(" ", "_")}")}
  assert_equal(value, td.text(), "#{table_header} not updated")
end

Then /^the user still has (.*?) as (.*?)$/ do |table_header, new_value|
  step "the user has #{table_header} updated to #{new_value.gsub("hostname_", "")}"
end

Then /^I can change the Role from the dropdown to (.*?)$/ do |primary_role|
  step "I can select #{primary_role} from a choice between \"Ingestion User, Application Developer, Sandbox Administrator\" Role"
end

Then /^I can add additional Role "(.*?)"$/ do |optional_role|
  checkboxes = @explicitWait.until{@driver.find_elements(:xpath, "//form/fieldset/div/div/label/input[@type=\"checkbox\"]")}
  checkboxes.each do |checkbox|
    value = checkbox.attribute("value")
    if optional_role == value && checkbox.attribute("checked") != "true"
      sleep 1
      checkbox.click
    end
    if optional_role != value && checkbox.attribute("checked") == "true"
      sleep 1
      checkbox.click
    end
  end
end

Then /^the user has Roles as "(.*?)"$/ do |roles|
  roles_list = roles.split(",")
  roles_list.each do |role|
    role.strip!
  end
  roles_list.sort!
  tr = @explicitWait.until{@driver.find_element(:id, @user_unique_id)}
  td = @explicitWait.until{tr.find_element(:id, "#{@user_full_name}_role")}
  displayed = td.text().split(",")
  displayed.each do |str|
    str.strip!
  end
  assert_equal(roles_list.size, displayed.size)
  displayed.sort!
  (0..roles_list.size).each do |idx|
    assert_equal(displayed[idx], roles_list[idx])
  end
end

Then /^the user now has roles "(.*?)" and "(.*?)"$/ do |role1, role2|
  step "the user has Roles as \"#{role1}, #{role2}\""
end

When /^I click on "(.*?)" icon$/ do |buttonName|
  puts "click on #{@userFullName}_#{buttonName}"
  @explicitWait.until{@driver.find_element(:xpath, "//a[@id='#{@userFullName}_#{buttonName}']")}.click
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
  found = false
  30.times do
    restHttpGet("/users",format,sessionId)
    found = false
    result = JSON.parse(@res.body)
    result.each do |user|
      if user["fullName"] == @userFullName
        found = true
        break
      end
      puts user["fullName"]
    end
    if !found
      break
    else
      sleep 1
    end
  end
  assert(!found,"the user #{@userFullName} is not removed from LDAP")
end

Then /^the user entry is removed from the table$/ do
  element = nil
  begin
    element = @driver.find_element(:xpath,"//tr[td='#{@userFullName}']")
  rescue
  end
  assert_nil(element,"the user #{@userFullName} is not removed from the table")
end

Then /^I see my Full Name is "(.*?)" in the table$/ do |fullName|
  step "I see a user with Full Name is \"#{fullName}\" in the table"
end

Then /^the "(.*?)" button is disabled$/ do |buttonName|
  delete_button = @explicitWait.until{@driver.find_element(:xpath,"//button[@id='#{@userFullName}_#{buttonName}' and @disabled = 'disabled']")}
  assert(!delete_button.nil?, "the #{buttonName} button is not disabled")
end

When /^I click on (".*?") button$/ do |buttonName|
  @explicitWait.until{@driver.find_element(:xpath, "//a[text()=#{buttonName}]")}.click
end

Given /^the testing user does not already exists in LDAP$/ do
  idpRealmLogin("sandboxoperator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/" + Socket.gethostname + "_testuser@testwgen.net", format, sessionId)
end

When /^I have entered Full Name and Email into the required fields$/ do
  @explicitWait.until{@driver.find_element(:name, 'user[fullName]')}.send_keys "Sandbox AcceptanceTests"
  @explicitWait.until{@driver.find_element(:name, 'user[email]')}.send_keys Socket.gethostname+"_testuser@testwgen.net"
end

Then /^I can select "(.*?)" from a choice between "(.*?)" Role$/ do |role, choices|
  drop_down = @explicitWait.until{@driver.find_element(:id, "user_primary_role")}
  for i in choices.split(",")  do
    i.strip!
    option = @explicitWait.until{drop_down.find_element(:xpath, ".//option[text()=\"#{i}\"]")}
    assert(option != nil)
  end

  options = @explicitWait.until{drop_down.find_elements(:xpath, ".//option")}
  assert(options.size == choices.split(",").size, "Only has #{options.size} choices, but requirement has #{choices.split(",").size} chioces")

  select = Selenium::WebDriver::Support::Select.new(@explicitWait.until{@driver.find_element(:id, "user_primary_role")})
  select.select_by(:text, role)
end

Then /^I can also check "(.*?)" Role$/ do |r|
  @explicitWait.until{@driver.find_element(:id, "#{r.downcase.gsub(" ", "_")}_role")}.click
end

When /^I click (".*?") link$/ do |link|
  @explicitWait.until{@driver.find_element(:xpath, "//a[text()=#{link}]")}.click
end

When /^I click button "(.*?)"$/ do |not_in_use|
  @explicitWait.until{@driver.find_element(:name, "commit")}.click
end

Then /^a "(.*?)" message is displayed$/ do |message|
  assertText(message)
end

def check_heading(heading_name)
  heading_element = @explicitWait.until{@driver.find_element(:xpath, "//tr[th='#{heading_name}']")}
  assert(!heading_element.nil?, "Cannot find heading name #{heading_name}")
end

def assertText(text)
  body = @explicitWait.until{@driver.find_element(:tag_name, "body")}
  assert(body.text.include?(text), "Cannot find the text \"#{text}\"")
end

def build_user(uid,fullName,groups,tenant,edorg)
  new_user = {
      "uid" => uid,
      "groups" => groups,
      "fullName" => fullName,
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
  newUid = oldUid+"_" + Socket.gethostname
  user.merge!({"uid" => newUid})
  return user
end

def localize(value)
  Socket.gethostname + "_" + value
end

def getField(field_name)
  if field_name == "\"EdOrg\""
    id = "user_edorg"
  else
    label = @explicitWait.until{@driver.find_element(:xpath, "//label[text()=#{field_name}]")}
    id = label.attribute("for")
  end
  @explicitWait.until{@driver.find_element(:id, "#{id}")}
end

def create_new_user(fullName, role, addition_roles=nil)
  localizedFullName = fullName.gsub("hostname",Socket.gethostname)
  groups = Array.new
  groups.push(role)
  unless addition_roles.nil?
    more_roles = addition_roles.split(",")
    more_roles.each do |str|
      groups.push(str.strip)
    end
  end

  uid=localizedFullName.split(" ")[-1].downcase
  build_user(uid,localizedFullName, groups, "sandboxadministrator@slidev.org","")
end
