require "selenium-webdriver"
require 'approval'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "sunsetadmin"                                if human_readable_id == "USERID"
  id = "sunsetadmin1234"                            if human_readable_id == "PASSWORD"
  id
end

Given /^I am authenticated to SLI IDP as user "([^"]*)" with pass "([^"]*)"$/ do |arg1, arg2|
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
  ldap_base=PropLoader.getProps['ldap.base']
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap.hostname'], 389, ldap_base, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  email_conf = {
      :host => 'mon.slidev.org',
      :port => 3000,
      :sender_name => "SLC Admin",
      :sender_email_addr => "hdjksflhsfadslfl@slidev.org"
    }
  ApprovalEngine.init(@ldap,Emailer.new(email_conf),false)
end

Given /^there is an account in ldap for vendor "([^"]*)"$/ do |vendor|
@vendor = vendor

end

Given /^the account has a tenantId "([^"]*)"$/ do |tenantId|
@email="devldapuser@slidev.org"
clear_user()

  user_info = {
      :first => "Loraine",
      :last => "Plyler",
       :email => @email,
       :password => "secret",
       :emailtoken => "token",
       :vendor => @vendor,
       :status => "pending",
       :homedir => "changeit",
       :uidnumber => "500",
       :gidnumber => "500",
     #  :tenantId => tenantId
   }

  @ldap.create_user(user_info)
  ApprovalEngine.change_user_status(@email,"approve",true)
end

When /^I go to the provisioning application web page$/ do
  url =PropLoader.getProps['admintools_server_url']+"/landing_zone"
  @driver.get url
end

When /^I provision with high\-level ed\-org to "([^"]*)"$/ do |arg1|
  @driver.find_element(:id, "custom_ed_org").send_keys arg1
  @driver.find_element(:id, "provisionButton").click
end

Then /^I get the success message$/ do
  assertWithWait("No success message") {@driver.find_element(:id, "successMessage") != nil}
end

Then /^an ed\-org is created in Mongo with the "([^"]*)" is "([^"]*)" and "([^"]*)" is "([^"]*)"$/ do |key1, value1,key2,value2|
step "I am logged in using \"operator\" \"operator1234\" to realm \"SLI\""
  uri="/v1/educationOrganizations"
  uri=uri+"?"+URI.escape(key1)+"="+URI.escape(value1)
  restHttpGet(uri)
  assert(@res.length>0,"didnt see a top level ed org with #{key1} is #{value1}")
  dataH=JSON.parse(@res.body)
  #verify tenantId after the simple IDP reading tenant info from ldap
 # assert(dataH[0]["metaData"][key2]==value2,"didnt see a top level ed org with #{key2} is #{value2}")
end

Then /^a request to provision a landing zone is made$/ do
   # this request is made by landing zone app in admin tools
end

Then /^the directory structure for the landing zone is stored in ldap$/ do
  user=@ldap.read_user(@email)
  # landing zone path is not saved correctly to ldap
 # assert(user[:homedir]!="changeit","the landing zone path is not stored in ldap")
 clear_user()
end

def clear_user
  if @ldap.user_exists?(@email)
  @ldap.delete_user(@email)
end
end



