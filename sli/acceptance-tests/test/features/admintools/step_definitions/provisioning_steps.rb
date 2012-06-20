require "selenium-webdriver"
require "json"
require 'approval'

require_relative "../../utils/sli_utils.rb"
require_relative "../../utils/selenium_common.rb"
require "date"

SAMPLE_DATA_SET1_CHOICE = "ed_org_STANDARD-SEA"
SAMPLE_DATA_SET2_CHOICE = "ed_org_IL-SUNSET"
CUSTOM_DATA_SET_CHOICE = "custom"


Given /^LDAP server has been setup and running$/ do
  ldap_base=PropLoader.getProps['ldap_base']
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], 389, ldap_base, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
   @email_sender_name= "Administrator"
     @email_sender_address= "noreply@slidev.org"
      email_conf = {
       :host => 'mon.slidev.org',
       :port => 3000,
       :sender_name => @email_sender_name,
       :sender_email_addr => @email_sender_address
     }
  ApprovalEngine.init(@ldap,Emailer.new(email_conf),nil,true)
   @edorgId =  "Test_Ed_Org"
   @email = "devldapuser_#{Socket.gethostname}@slidev.org"
end

Given /^there is a production account in ldap for vendor "([^"]*)"$/ do |vendor|
  @sandboxMode=false
end

When /^I go to the provisioning application$/ do
  url = PropLoader.getProps['admintools_server_url']+"/landing_zone"
  @driver.get(url)
end

Then /^I can only enter a custom high\-level ed\-org$/ do
  lower_timeout_for_same_page_validation
  assertWithWait("Custom data choice does not exist") {@driver.find_element(:id, CUSTOM_DATA_SET_CHOICE) != nil}
  assert(@driver.find_elements(:id, SAMPLE_DATA_SET1_CHOICE).empty?, "Sample data choices exist on production")
  assert(@driver.find_elements(:id, SAMPLE_DATA_SET2_CHOICE).empty?, "Sample data choices exist on production")
  reset_timeouts_to_default
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

Given /^there is a sandbox account in ldap for vendor "([^"]*)"$/ do |vendor|
  @sandboxMode=true
  @tenantId = @email
remove_user(@email)
sleep(1)

  user_info = {
      :first => "Provision",
      :last => "test",
       :email => @email,
       :emailAddress => @email,
       :password => "test1234",
       :emailtoken => "token",
       :vendor => vendor,
       :status => "submitted",
       :homedir => "changeit",
       :uidnumber => "500",
       :gidnumber => "500",
       :tenant => @tenantId
   }

  ApprovalEngine.add_disabled_user(user_info)
  ApprovalEngine.change_user_status(@email, ApprovalEngine::ACTION_ACCEPT_EULA)
  user_info = ApprovalEngine.get_user(@email)
  ApprovalEngine.verify_email(user_info[:emailtoken])
end

Then /^I can select between the the high level ed\-org of the sample data sets or enter a custom high\-level ed\-org$/ do
  assertWithWait("Sample data choice does not exist") {@driver.find_element(:id, SAMPLE_DATA_SET1_CHOICE) != nil}
  #assertWithWait("Sample data choice does not exist") {@driver.find_element(:id, SAMPLE_DATA_SET2_CHOICE) != nil}
  assertWithWait("Custom data choice does not exist") {@driver.find_element(:id, CUSTOM_DATA_SET_CHOICE) != nil}
end

Then /^I get a already provisioned message$/ do
  assertWithWait("No already provisioned message") {@driver.find_element(:id, "alreadyProvisioned") != nil}
end
def remove_user(email)
  if ApprovalEngine.user_exists?(email)
  ApprovalEngine.remove_user(email)
  end
end
