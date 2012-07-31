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

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require_relative '../../sandbox/UserAdmin/step_definitions/User_Admin_Interface_steps.rb'

Before do 
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
end

Given /^I already have a SLC Operator account$/ do
  #do nothing, guaranteed by configuration
end

Given /^I have a valid account as a SEA Administrator$/ do
  #do nothing, guaranteed by configuration
end

When /^I navigate to the User Management Page$/ do
  step "I navigate to the sandbox user account management page"
end

Given /^the prod testing user does not already exists in LDAP$/ do
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+Socket.gethostname+"_prodtestuser@testwgen.net", format, sessionId)
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

Then /^I can change the EdOrg dropdown to "(.*?)"$/ do |selection| 
    drop_down = @driver.find_element(:id, "user_edorg")
    drop_down.click
    options = drop_down.find_elements(:xpath, ".//option");
    options.each do |option|
        if option.text() == "#{selection}"
            option.click
            drop_down.send_keys "\r"
        end
    end
    drop_down.send_keys "\r"
end 





