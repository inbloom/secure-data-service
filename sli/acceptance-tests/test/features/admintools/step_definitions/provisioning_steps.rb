require "selenium-webdriver"
require "json"
require 'approval'

require_relative "../../utils/sli_utils.rb"
require_relative "../../utils/selenium_common.rb"
require "date"

SAMPLE_DATA_SET1_CHOICE = "ed_org_STANDARD-SEA"
SAMPLE_DATA_SET2_CHOICE = "ed_org_IL-SUNSET"
CUSTOM_DATA_SET_CHOICE = "custom"

Given /^there is a production account in ldap for vendor "([^"]*)"$/ do |vendor|
  @sandboxMode=false
  ldap_base=PropLoader.getProps['ldap_base']
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], 389, ldap_base, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  found = @ldap.read_user("sunsetadmin")
  if !found
    user_info = {
      :first      => "Sunset",
      :last       => "Admin", 
      :email      => "sunsetadmin",
      :password   => "secret", 
      :vendor     => vendor,
      :emailtoken => "0102030405060708090A0B0C0D0E0F",
      :status     => "submitted",
      :homedir    => 'test',
      :uidnumber  => '456',
      :gidnumber  => '123'
    }
    @ldap.create_user(user_info)
  end
end

When /^I go to the provisioning application$/ do
  url = PropLoader.getProps['admintools_server_url']+"/landing_zone"
  @driver.get(url)
end

Then /^I can only enter a custom high\-level ed\-org$/ do
  assertWithWait("Custom data choice does not exist") {@driver.find_element(:id, CUSTOM_DATA_SET_CHOICE) != nil}
  assert(@driver.find_elements(:id, SAMPLE_DATA_SET1_CHOICE).empty?, "Sample data choices exist on production")
  assert(@driver.find_elements(:id, SAMPLE_DATA_SET2_CHOICE).empty?, "Sample data choices exist on production")
end

When /^I set the custom high\-level ed\-org to "([^"]*)"$/ do |arg1|
  @driver.find_element(:id, "custom_ed_org").send_keys arg1
end

When /^I select the first sample data set$/ do
  @driver.find_element(:id, SAMPLE_DATA_SET1_CHOICE).click
end

When /^I click the Provision button$/ do
  @driver.find_element(:id, "provisionButton").click
end

Then /^I get the success message$/ do
  assertWithWait("No success message") {@driver.find_element(:id, "successMessage") != nil}
end

Given /^there is a sandbox account in ldap for vendor "([^"]*)"$/ do |arg1|
  @sandboxMode=true
end

Then /^I can select between the the high level ed\-org of the sample data sets or enter a custom high\-level ed\-org$/ do
  assertWithWait("Sample data choice does not exist") {@driver.find_element(:id, SAMPLE_DATA_SET1_CHOICE) != nil}
  #assertWithWait("Sample data choice does not exist") {@driver.find_element(:id, SAMPLE_DATA_SET2_CHOICE) != nil}
  assertWithWait("Custom data choice does not exist") {@driver.find_element(:id, CUSTOM_DATA_SET_CHOICE) != nil}
end

Then /^I get a conflict error message$/ do
  assertWithWait("No conflict error message") {@driver.find_element(:id, "conflictMessage") != nil}
end
