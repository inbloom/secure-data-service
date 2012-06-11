require "selenium-webdriver"
require 'java_properties'
require 'mongo'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

Before do
   @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
   @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
end

Given /^there is number_of_developer_accounts property in the configuration file for the sandbox environment$/ do
  #props = JavaProperties::Properties.new("../config/properties/sli.properties")
  #@user_account_maximum ||= Integer(props["sli.useraccount.maximum"])
  #assert(!!@user_account_maximum, "Unable to obtain the property sli.useraccount.maximum from sli.properties")
end

When /^I hit User Registration for sandbox$/ do
  @driver.get(PropLoader.getProps['admintools_server_url']+'/registration')
end

When /^the number of accounts already created in LDAP is equal to <NUMBER_OF_DEVELOPER_ACCOUNTS>$/ do
  # TODO DE821 -- implement this
end

Then /^I get an error message$/ do
  #assert(@driver.find_element(:id, "waiting_list_page") != nil)
end

When /^when I enter my email address$/ do
   # TODO DE821 -- implement this
end

When /^click "([^"]*)"$/ do |arg1|
   # TODO DE821 -- implement this
end

Then /^my email address is stored in the database$/ do
   # TODO DE821 -- implement this
end

Then /^I get a success message$/ do
  #(@driver.current_url).should == (PropLoader.getProps['admintools_server_url'] + "/waitlist_users/success")
end
