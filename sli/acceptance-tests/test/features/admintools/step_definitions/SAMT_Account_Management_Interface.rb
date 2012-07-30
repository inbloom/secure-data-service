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


When /^I navigate to the User Management Page$/ do
    step "I navigate to the sandbox user account management page"
end

Given /^the prod testing user does not already exists in LDAP$/ do
  idpRealmLogin("operator", nil)
  sessionId = @sessionId
  format = "application/json"
  restHttpDelete("/users/"+Socket.gethostname+"_prodtestuser@testwgen.net", format, sessionId)
end
