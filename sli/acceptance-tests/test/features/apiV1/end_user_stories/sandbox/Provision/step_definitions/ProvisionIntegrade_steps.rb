require "selenium-webdriver"
require 'approval'

require_relative '../../../../../utils/sli_utils.rb'
require_relative '../../../../../utils/selenium_common.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "sunsetadmin"                                if human_readable_id == "USERID"
  id = "sunsetadmin1234"                            if human_readable_id == "PASSWORD"
  id
end

Given /^I am authenticated to SLI IDP as user "([^"]*)" with pass "([^"]*)"$/ do |arg1, arg2|
  puts arg1, arg2
  url =PropLoader.getProps['admintools_server_url']+"/landing_zone"
  @driver.get url
  assertWithWait("Failed to navigate to the SLI IDP to authenticate")  {@driver.find_element(:id, "IDToken1")}
  @driver.find_element(:id, "IDToken1").send_keys arg1
  @driver.find_element(:id, "IDToken2").send_keys arg2
  @driver.find_element(:name, "Login.Submit").click
  begin
    @driver.switch_to.alert.accept
  rescue
  end
end

Given /^LDAP server has been setup and running$/ do
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap.hostname'], 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  email_conf = {
      :host => 'mon.slidev.org',
      :port => 3000,
      :sender_name => "SLC Admin",
      :sender_email_addr => "admin@SLC.org"
    }
  ApprovalEngine.init(@ldap,email_conf,false)
end

Given /^there is an account in ldap for vendor "([^"]*)"$/ do |vendor|
@vendor = vendor
  
end

Given /^the account has a tenantId "([^"]*)"$/ do |tenantId|
@email="devldapuser@slidev.org"
if @ldap.user_exists?(@email)
  @ldap.delete_user(@email)
end

  user_info = {
      :first => "Loraine",
      :last => "Plyler", 
       :email => @email,
       :password => "secret", 
       :emailtoken => "token",
       :vendor => @vendor,
       :status => "pending",
       :homedir => "changeit",
       :uid => "devldapuser@slidev.org",
       :gid => "testgroup",
     #  :tenantId => tenantId
   }
  
  @ldap.create_user(user_info)
  ApprovalEngine.change_user_status(@email,"approve",true)
end

When /^I go to the provisioning application web page$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I provision with high\-level ed\-org to "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^an ed\-org is created in Mongo with the "([^"]*)" is "([^"]*)" and "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  pending # express the regexp above with the code you wish you had
end

Then /^a request to provision a landing zone is made$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the directory structure for the landing zone is stored in ldap$/ do
  pending # express the regexp above with the code you wish you had
end


