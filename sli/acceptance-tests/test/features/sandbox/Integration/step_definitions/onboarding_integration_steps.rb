require "selenium-webdriver"
require "mongo" 
require 'approval' 
require 'rumbster'
require 'message_observers'
require 'net/imap'
require 'active_support/inflector' 
require_relative '../../../utils/sli_utils.rb' 
require_relative '../../../utils/selenium_common.rb' 


SAMPLE_DATA_SET1_CHOICE = "ed_org_IL"
SAMPLE_DATA_SET2_CHOICE = "ed_org_IL-SUNSET"
CUSTOM_DATA_SET_CHOICE = "custom"

Before do 
   @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
   @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
   
end

After do |scenario|
  @rumbster.stop if @rumbster
  sleep(1)
end

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "devldapuser@slidev.org"                                       if human_readable_id == "USER_EMAIL"
  id = "test1234"                                                     if human_readable_id == "USER_PASS"
  id = "StateEdorg"                                                   if human_readable_id =="STATE_ED_ORG"
  id = "Loraine2"                                                     if human_readable_id == "USER_FIRSTNAME"
  id = "Plyler2"                                                      if human_readable_id == "USER_LASTNAME"
  id = "Super_Admin"                                                  if human_readable_id == "SUPER_ADMIN"
  id = "Application Developer"                                        if human_readable_id == "APPLICATION_DEVELOPER"
  id = "Dashboard"                                                    if human_readable_id == "DASHBOARD_APP"
  id = "Admin Tool"                                                   if human_readable_id == "ADMIN_APP"
  id = "Databrowser"                                                  if human_readable_id == "DATABROWSER_APP"
  id = "ed_org_IL"                                                    if human_readable_id == "ED-ORG_SAMPLE_DS1"
  id = "mreynolds"                                                    if human_readable_id == "DISTRICT_ADMIN_USER"
  id = "mreynolds1234"                                                if human_readable_id == "DISTRICT_ADMIN_PASS"
  #need to figure out what tenantId is for real user provision instead of sunsetadmin
  id = "devldapuser@slidev.org"                                   if human_readable_id == "Tenant_ID"
  #need to figure out what landing zone path is for real user provision instead of sunsetadmin
  id = "devldapuser@slidev.org"                                if human_readable_id == "Landing_zone_directory"
  
  id = "mreynolds"                                                       if human_readable_id == "Prod_Tenant_ID"
  id = "mreynolds/StateEdorg"                                            if human_readable_id == "Prod_Landing_zone_directory"
  
  #placeholder for provision and app registration link, need to be updated to check real link
  id = "landing_zone"                                     if human_readable_id == "URL_TO_PROVISIONING_APPLICATION"
  id = "apps"                                             if human_readable_id == "URL_TO_APPLICATION_REGISTRATION"
  id = "roles"                                            if human_readable_id == "URL_TO_ADMIN_APP"
  id = "portal"                                           if human_readable_id == "URL_TO_PORTAL"

  id = "account_managements" if human_readable_id == "ACCOUNT_MANAGEMENT_APP"
  id = "slcoperator-email@slidev.org" if human_readable_id == "SLC_OPERATOR_USER"
  id = "slcoperator-email1234" if human_readable_id == "SLC_OPERATOR_PASS"

  #return the translated value
  id
end

Given /^I have a SMTP\/Email server configured$/ do
     @mode="live"
     @email_sender_name= "Administrator"
     @email_sender_address= "noreply@slidev.org"
      @email_conf = {
       :host => 'mon.slidev.org',
       :port => 3000,
       :sender_name => @email_sender_name,
       :sender_email_addr => @email_sender_address
     }
 end

Given /^I go to the sandbox account registration page$/ do
  #the user registration path need to be fixed after talk with wolverine
  
  @admin_url = PropLoader.getProps['admintools_server_url']
  url=@admin_url+"/user_account_registrations/new"
  @prod = false 
  initializeApprovalAndLDAP(@email_conf, @prod)
  clear_users()
  @driver.get url
end 

Given /^there is no registered account for "([^"]*)" in the SLI database$/ do |email|
  removeUser(email)
end

Given /^there is no registered account for "([^"]*)" in LDAP$/ do |email|
  removeUser(email)
end

Given /^the developer type in first name "([^"]*)" and last name "([^"]*)"$/ do|first_name, last_name|
  fill_field("firstname",first_name)
  fill_field("lastname",last_name)
    
end

Given /^the developer type in email "([^"]*)" and password "([^"]*)"$/ do |email, pass|
  fill_field("email",email)
  fill_field("password",pass)
  fill_field("confirmation",pass)
  fill_field("vendor","Macro Corp")
  @email = email
end

Given /^the developer submits the account registration request$/ do
  @driver.find_element(:xpath, "//input[contains(@id, 'submit')]").click
end

Then /^the developer is redirected to a page with terms and conditions$/ do
   assert(@driver.current_url.include?("#{@admin_url}/eula"))
   assertText("Terms and Conditions")
end

When /^the developer click "([^"]*)"$/ do |button|
  @driver.find_element(:xpath, "//input[contains(@id, '#{button.downcase}')]").click
end

Then /^the developer is directed to an acknowledgement page\.$/ do
  assertText("Thank You")
  assertText("Be on the lookout for a confirmation email.")
end

Then /^a verification email is sent to "([^"]*)"$/ do |email_address|
   sleep(2)
    verifyEmail()
end

When /^the developer click link in verification email$/ do
  sleep(2)
  url=getVerificationLink()
  puts url
  @driver.get url
end

Then /^an account entry is made in ldap with "([^"]*)" status$/ do |status|
  user=ApprovalEngine.get_user(@email)
  puts user
  assert(user[:status]==status.downcase,"didnt create account with status is #{status}")
end

Then /^an approval email is sent to the "([^"]*)"$/ do |email|
  sleep(10)
  @email = email
  verifyEmail()
  approval_email_subject="Welcome to the Shared Learning Collaborative"
  found=@email_subject.downcase.include?(approval_email_subject.downcase)
  assert(found,"didnt receive approval email!")
end

Then /^the email has a "([^"]*)"$/ do |link|
 #link need to be fixed before uncomment out code
  found = @email_content.include?(link)
  assert(found,"the email doesnt have the link for #{link}")
end

Then /^a "([^"]*)" roles is a added for the user in ldap$/ do |role|
  roles=@ldap.get_user_groups(@email)
  puts roles
  assert(roles.include?(role),"didnt add #{role} role in ldap")
end

When /^the user clicks on "([^"]*)"$/ do |link|
  url=PropLoader.getProps['admintools_server_url']+"/"+link
  @driver.get url
end

Then /^the user has to authenticate against ldap using "([^"]*)" and "([^"]*)"$/ do |user, pass|
  #temporalily use sunsetadmin to login, need to use real user afer fix the code
  step "I submit the credentials \"#{user}\" \"#{pass}\" for the \"Simple\" login page"
  #step "I submit the credentials \"sunsetadmin\" \"sunsetadmin1234\" for the \"Simple\" login page"
end

Then /^the user is redirected to "([^"]*)"$/ do |link|
  sleep(1)
  assert( @driver.current_url.include?(link),"the user is not redirected to #{link}")
end

When /^the user selects the option to use the "([^"]*)"$/ do |arg1|
  @driver.find_element(:id, SAMPLE_DATA_SET1_CHOICE).click
end

When /^clicks on "([^"]*)"$/ do |arg1|
   clear_edOrg()
   clear_tenant()
   sleep(1)
   @driver.find_element(:id, "provisionButton").click
end

Then /^an "([^"]*)" is saved to mongo$/ do |arg1|
 step "I am logged in using \"operator\" \"operator1234\" to realm \"SLI\""
  uri="/v1/educationOrganizations"
  #uri=uri+"?"+URI.escape("body.stateOrganizationId")+"="+URI.escape("IL")
  restHttpGet(uri)
  dataH=JSON.parse(@res.body)
  assert(dataH.length==1,"#{arg1} is not saved to mongo}")
  @edorgId=dataH[0]["id"]
end

Then /^an "([^"]*)" is added in the application table for "([^"]*)","([^"]*)", "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  app_collection=@db["application"]
  results=app_collection.find({"body.bootstrap"=>true})
   results.each do |application|
    found=false
    ids=application["body"]["authorized_ed_orgs"]
    ids.each do |id|
      if id==@edorgId
        found=true
      end
    end
    assert(found,"#{arg1} is not added in the application table")
    end
                                                                                 
end

Then /^a request for a Landing zone is made with "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|
  # do nothing, request made by provision app
end

Then /^a tenant entry with "([^"]*)" and "([^"]*)" is added to mongo$/ do |tenantId, landing_zone_path|
  tenant_coll=@db["tenant"]
  count=tenant_coll.find().count
  assert(count==1,"tenant entry is not added to mongo")
  tenant=tenant_coll.find().to_a[0]
  assert(tenant["body"]["tenantId"]==tenantId,"tenantId:#{tenantId} is not added to mongo")
  landingZones=tenant["body"]["landingZone"]
  found=false
  landingZones.each do |landingZone|
    puts landingZone['path']
    if landingZone["path"].include?(landing_zone_path)
      found=true
    end
  end
  assert(found,"landing zone path:#{landing_zone_path} is not added to mongo")
end

Then /^the "([^"]*)" is saved in Ldap$/ do |arg1|
  #after fix login issue for real user to provision instead of sunsetadmin, need to verify tenant and homedirectory
  user = @ldap.read_user(@email)
  puts user
end

Then /^the landing zone "([^"]*)" is saved in Ldap$/ do |landing_zone_path|
  user = @ldap.read_user(@email)
  puts user
  #after fix login issue for real user to provision instead of sunsetadmin, need to uncomment out
  assert(user[:homedir].include?(landing_zone_path),"landing zone: #{landing_zone_path} is not saved in Ldap")
  
end

Then /^the tenantId "([^"]*)" is saved in Ldap$/ do |tenantId|
  user = @ldap.read_user(@email)
  #puts user
  #after fix login issue for real user to provision instead of sunsetadmin, need to uncomment out
  assert(user[:tenant]==tenantId,"tenantId: #{tenantId} is not saved in Ldap")
end


Given /^the user has an approved sandbox account$/ do
  # do nothing, use account approved from last scenario
end

When /^the user accesses the "([^"]*)"$/ do |link|
  step "the user clicks on \"#{link}\""
end

Given /^I go to the production account registration page$/ do
  @admin_url = PropLoader.getProps['admintools_server_url']
  url="#{@admin_url}/registration"
  @prod = true 
  initializeApprovalAndLDAP(@email_conf, @prod)
  clear_users()
  @driver.get url
end

When /^the SLC operator accesses the "([^"]*)"$/ do |page|
  url="#{PropLoader.getProps['admintools_server_url']}/#{page}"
  @driver.get url
end

When /^the SLC operator approves the vendor account for "([^"]*)"$/ do |email|
  @driver.find_element(:xpath, "//input[@type='hidden' and @value='#{email}']/../input[@type='submit' and @value='Approve']").click()
  @driver.switch_to().alert().accept()
end

Then /^an approval email is sent to "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^the SLC operator authenticates as "([^"]*)" and "([^"]*)"$/ do |user, pass|
  step "I submit the credentials \"#{user}\" \"#{pass}\" for the \"Simple\" login page"
end

When /^the state super admin accesses the "([^"]*)"$/ do |link|
   @admin_url = PropLoader.getProps['admintools_server_url']
   url=@admin_url+"/"+link
   @prod = true 
   initializeApprovalAndLDAP(@email_conf, @prod)
   @driver.get url
end

Then /^the state super admin authenticates as "([^"]*)" and "([^"]*)"$/ do |user, pass|
  @email = user
  step "I submit the credentials \"#{user}\" \"#{pass}\" for the \"Simple\" login page"
end

When /^the state super admin set the custom high\-level ed\-org to "([^"]*)"$/ do |arg1|
  @driver.find_element(:id, "custom_ed_org").send_keys arg1
end

def initializeApprovalAndLDAP(emailConf, prod)
  # ldapBase need to be configured in admin-tools and acceptance test to match simple idp branch
   ldapBase=PropLoader.getProps['ldap_base']
  # ldapBase = "ou=DevTest,dc=slidev,dc=org" 
   @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], 389, ldapBase, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{") 
   email = Emailer.new @email_conf
   ApprovalEngine.init(@ldap, email, nil, !prod)
 end 


def verifyEmail
  if @mode
    defaultUser = 'devldapuser'
    defaultPassword = 'Y;Gtf@w{'
    imap = Net::IMAP.new('mon.slidev.org', 993, true, nil, false)
    imap.authenticate('LOGIN', defaultUser, defaultPassword)
    imap.examine('INBOX')
    
    ids = imap.search(["FROM", @email_sender_name,"TO",@email])
    #puts ids
    content = imap.fetch(ids[-1], "BODY[TEXT]")[0].attr["BODY[TEXT]"]
    subject = imap.fetch(ids[-1], "BODY[HEADER.FIELDS (SUBJECT)]")[0].attr["BODY[HEADER.FIELDS (SUBJECT)]"]
    found = true if content != nil
    @email_content = content
    @email_subject = subject
    puts subject,content
    imap.disconnect
    assert(found, "Email was not found on SMTP server")
  else
    assert(@message_observer.messages.size == 1, "Number of messages is #{@message_observer.messages.size} but should be 1")
    email = @message_observer.messages.first
    assert(email != nil, "email was not received")
    assert(email.to[0] == @email, "email address was incorrect")
    assert(email.subject.include?("SLC Account Verification Request"), "email did not have correct subject")
    @email_content = email.body.to_s
  end
end

def getVerificationLink
  if @email_content.include? "http://"
  link="http://"+@email_content.split("http://")[-1].split("\n")[0]
  else 
  link="https://"+@email_content.split("https://")[-1].split("\n")[0]
  end
end

def removeUser(email)
  if ApprovalEngine.user_exists?(email)
    ApprovalEngine.remove_user(email)
  end
  coll = @db["userAccount"]
  coll.remove("body.userName" => email)
end

def fill_field(field,value)
  trimmed = field.sub(" ", "")
   @driver.find_element(:xpath, "//input[contains(
    translate(@id, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '#{trimmed.downcase}')
    ]").send_keys(value)
end

def assertText(text)
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text \"#{text}\"")
end

def clear_edOrg
   edOrg_coll=@db["educationOrganization"]
   edOrg_coll.remove()
end

def clear_tenant
   tenant_coll=@db["tenant"]
   tenant_coll.remove()
end

def clear_users
  @ldap.delete_user('devldapuser@slidev.org')    
end
