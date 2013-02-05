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
require 'approval'
require "mongo" 
require 'rumbster'
require 'digest'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require_relative '../../sandbox/UserAdmin/step_definitions/User_Admin_Interface_steps.rb'

Before("@RALLY_3071") do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  @db = Mongo::Connection.new.db(convertTenantIdToDbName(PropLoader.getProps['api_database_name']))
end

After("@RALLY_3071") do
  step "the prod testing user does not already exists in LDAP"
end

Given /^I already have a SLC Operator account$/ do
  #do nothing, guaranteed by configuration
end

Given /^I have a valid account as a SEA Administrator$/ do
  #do nothing, guaranteed by configuration
end

Given /^I have a valid account as a LEA Administrator$/ do
  #do nothing, guaranteed by configuration
end 

Given /^There is a user with "(.*?)", "(.*?)", "(.*?)", and "(.*?)" in "(.*?)" "(.*?)" LDAP Server$/ do |full_name, role, addition_roles, email, tenant, edorg|
  new_user=create_new_user(full_name.gsub("hostname", Socket.gethostname), role, addition_roles)
  new_user['email']=email.gsub("hostname", Socket.gethostname)
  new_user['uid']=new_user['email']
  new_user['tenant']=tenant
  new_user['edorg']=edorg

  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"

  restHttpDelete("/users/#{new_user['uid']}", format, sessionId)
  restHttpPost("/users", new_user.to_json, format, sessionId)
  @user_full_name="#{new_user['firstName']} #{new_user['lastName']}"
  @user_unique_id=new_user['uid']
end

Given /^There is a user with "(.*?)", "(.*?)", "(.*?)", and "(.*?)" in LDAP Server$/ do |full_name, role, addition_roles, email|
    step "There is a user with \"#{full_name}\", \"#{role}\", \"#{addition_roles}\", and \"#{email}\" in \"Midgar\" \"IL-SUNSET\" LDAP Server"
end

Given /^there is a production "(.*?)" with tenancy "(.*?)" and in "(.*?)"$/ do |role, tenant, edorg|
  @sandboxMode=false
  ApprovalEngine.init(@ldap, nil, false)
  @email = "#{Socket.gethostname}_prodtestuser2@testwgen.net"

  if ApprovalEngine.user_exists?(@email)
     ApprovalEngine.remove_user(@email)
  end
  sleep(1)

  @user_info = {
       :first => "SAMT",
       :last => "Test_#{Socket.gethostname}",
       :email => @email,
       :emailAddress => @email,
       :password => "test1234",
       :emailtoken => "token",
       :status => "submitted",
       :homedir => "/dev/null",
       :uidnumber => "500",
       :gidnumber => "500",
       :tenant => "#{tenant}",
       :edorg => "#{edorg}"
  }
 
  ApprovalEngine.add_disabled_user(@user_info)
  ApprovalEngine.change_user_status(@email, ApprovalEngine::ACTION_ACCEPT_EULA)
  @user_info = ApprovalEngine.get_user(@email)
  ApprovalEngine.verify_email(@user_info[:emailtoken])


  @ldap.add_user_group(@email, role)

end

Given /^there is a "(.*?)" with tenancy "(.*?)" and in "(.*?)"$/ do |role, tenant, edorg|
    step "there is a production \"#{role}\" with tenancy \"#{tenant}\" and in \"#{edorg}\""
end

Given /^there is no users in edorg "(.*?)"$/ do |edorg| 
    idpRealmLogin("operator", nil)
    step "I navigate to GET \"/users\""
    sessionId = @sessionId
    format = "application/json"
    if @res.code == 200 
      @result.each { |user|
        if user['edorg'] == edorg
          puts "delete #{user['uid']}"
          restHttpDelete("/users/#{user['uid']}", format, sessionId)
        end 
      }   
    end 
end 

Then /^I can navigate to the User Management Page with that production user$/ do
  step "I navigate to the sandbox user account management page"
  step "I submit the credentials \"#{@user_info[:email]}\" \"test1234\" for the \"Simple\" login page"
end

When /^I navigate to the User Management Page$/ do
  step "I navigate to the sandbox user account management page"
end

Given /^the prod testing user does not already exists in LDAP$/ do
    idpRealmLogin("operator", nil)
    sessionId = @sessionId
    format = "application/json"
    restHttpDelete("/users/"+Socket.gethostname+"_prodtestuser@testwgen.net", format, sessionId)
    restHttpDelete("/users/"+Socket.gethostname+"_prodtestuser2@testwgen.net", format, sessionId)
end


Then /^the new user has (.*?) updated to (.*?)$/ do |field_name, value|
  @user_unique_id=@user_email
  step "the user has #{field_name} updated to #{value}"
end


Then /^the (.*?) textbox is disabled$/ do |field_name|
  sleep 3
  field=getField(field_name)
  assert(field.attribute("disabled")=="true", 
            "#{field_name} is expected to be disabled, but its disabled status is: #{field.attribute("disabled")}")
end

Then /^EdOrg choice is limited to "(.*?)" for me$/ do |edorg| 
  drop_down = @driver.find_element(:id, "user_edorg")
  options = drop_down.find_elements(:xpath, ".//option")
  options.each do |option| 
    assert(option.text() == edorg, "I see other options: #{option.text()} ") 
  end
end

#TODO is there a better way to figure out the elements are not there?  This wait for a timeout and takes a long time
Then /^There is no textbox for (.*?)$/ do |field_name|
  label=@driver.find_elements(:xpath, "//label[text()=#{field_name}]")
  assert(label.size==0, "#{field_name} should not be visible!")
end 

#TODO is there a better way to figure out the elements are not there?  This wait for a timeout and takes a long time
Then /^I do not see (.*?) Role$/ do |role| 
  drop_down = @driver.find_element(:id, "user_primary_role")
  option = drop_down.find_elements(:xpath, ".//option[text()=#{role}]")
  assert(option.size==0, "Should not see #{role} from the Roles drop down menu")
end

Then /^I do not see Role selection nor EdOrg dropdown menu$/ do 
  roles_edorg_block = @driver.find_element(:id, "roles_edorg_block")
  assert(roles_edorg_block.displayed? == false, "role and edorg block is visible")
end

Then /^I do not see an option to change my primary admin role$/ do
  primary_role_block = @driver.find_element(:id, "primary_role_block")
  assert(primary_role_block.displayed? == false, "I can still see the primary role block")
end

Then /^I can change the EdOrg dropdown to "(.*?)"$/ do |selection| 
    drop_down = Selenium::WebDriver::Support::Select.new(@driver.find_element(:id, "user_edorg"))
    drop_down.select_by(:text, selection)
end 


Then /^the new user has the same "(.*?)" field as "(.*?)" has$/ do |field_name, match_user| 
  @user_unique_id=@user_email
  td=@driver.find_element(:id, "#{match_user}_#{field_name.downcase.gsub(" ", "_")}")
  step "the user has \"#{field_name}\" updated to \"#{td.text()}\""
end

Then /^the new user has Roles as (.*?)$/ do |roles|
  @user_unique_id=@user_email
  step "the user has Roles as #{roles}"
end 

Then /^the (.*?) field is prefilled$/ do |field_name|
  field=getField(field_name)
  assert("#{field.attribute("value")}" != "", "#{field_name} is empty!") 
end

Given /^I have a account as a "LEA Administrator"$/ do 
end

Then /^I am redirected to "(.*?)" page which has a table of all accounts for my tenancy$/ do |pageTitle|
  assertWithWait("Failed to navigate to the #{pageTitle} page")  {@driver.page_source.index("#{pageTitle}") != nil}
  assertWithWait("Failed to find the table of accounts") {@driver.find_element(:id => "Users_Management_Table") != nil}
end

Then /^I can update email address$/ do
  step "I can update the \"Email\" field to \"sunsetadmin2@slcedu.org\""
end

Then /^I can update the Fullname$/ do
  step "I can update the \"Email\" field to \"Sunset Admin 2\""
end

Then /^I cannot update any other field$/ do
          pending # express the regexp above with the code you wish you had
end

Then /^I do not see any other LEA admin from (.*?) besides me$/ do |edorg|
  my_name="#{@user_info[:first]} #{@user_info[:last]}"
  my_edorg="#{@user_info[:edorg]}"
  found_myself=false
  table=@driver.find_element(:id, "Users_Management_Table")
  table.find_elements(:xpath, ".//tbody/tr").each do |tr|
    full_name = tr.find_elements(:xpath, ".//td")[0].text()
    roles = tr.find_element(:id, "#{full_name}_role").text()
    edorg = tr.find_element(:id, "#{full_name}_edorg").text()
    if(roles.rindex("LEA Administrator") != nil) 
        if(full_name == my_name) 
            found_myself = true
        end 
        assert(my_edorg != edorg || full_name == my_name, "I see other LEA besides me #{full_name}") 
    end 
  end

  assert(found_myself, "I didn't show up on the LEA list")
end


Then /^any other LEA admin belongs to "(.*?)"$/ do |edorgs| 
  my_name="#{@user_info[:first]} #{@user_info[:last]}"
  sub_edorgs = edorgs.split(",")
  sub_edorgs.each do |sub|
    sub.strip!
  end
      
  table=@driver.find_element(:id, "Users_Management_Table")
  table.find_elements(:xpath, ".//tbody/tr").each do |tr|
    full_name = tr.find_elements(:xpath, ".//td")[0].text()
    roles = tr.find_element(:id, "#{full_name}_role").text()
    edorg = [tr.find_element(:id, "#{full_name}_edorg").text()]
    if(roles.rindex("LEA Administrator") != nil and my_name != full_name) 
        assert((sub_edorgs & edorg).size==1, " other LEA #{full_name} besides me in #{edorg[0]}") 
    end 
  end
end
