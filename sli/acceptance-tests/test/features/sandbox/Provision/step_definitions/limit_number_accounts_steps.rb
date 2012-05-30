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
  props = JavaProperties::Properties.new("../config/properties/sli.properties")
  @user_account_maximum ||= Integer(props["sli.useraccount.maximum"])
  assert(!!@user_account_maximum, "Unable to obtain the property sli.useraccount.maximum from sli.properties")
end

When /^I hit User Registration for sandbox$/ do
  @driver.get(PropLoader.getProps['admintools_server_url']+'/registration')
end

When /^the number of accounts already created in database is equal to <NUMBER_OF_DEVELOPER_ACCOUNTS>$/ do
  userAccountCollection = @db["userAccount"]
  (1..(@user_account_maximum)).each do |x|
    userAccountCollection.insert({
      "userName"    => "test#{x}@test.com",
      "firstName"   => "FN",
      "lastName"    => "LN#{x}",
      "vendor"      => "TestVendor",
      "validated"   => "false",
      "environment" => "Sandbox"
    })
  end
  assert(@db["waitingListUserAccount"].count <= 0)
end

Then /^I get an error message$/ do
  assertWithWait("Waitlist Email") {@driver.find_element(:id, "new_waitlist_user") != nil}
end

When /^when I enter my email address$/ do
  @driver.find_element(:id, "waitlist_user_email").clear
  @driver.find_element(:id, "waitlist_user_email").send_keys "blah@blah.com"
end

When /^click "([^"]*)"$/ do |arg1|
  @driver.find_element(:name, "commit").click
end

Then /^my email address is stored in the database$/ do
  assert(@db["waitingListUserAccount"].count >= 1)
end

Then /^I get a success message$/ do
  (@driver.current_url).should == (PropLoader.getProps['admintools_server_url'] + "/waitlist_users/success")
end
