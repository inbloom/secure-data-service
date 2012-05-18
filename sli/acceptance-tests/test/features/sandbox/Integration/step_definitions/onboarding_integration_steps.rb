require "selenium-webdriver" 
require "mongo" 
require 'approval' 
require 'rumbster'
require 'message_observers'
require 'net/imap'
require 'active_support/inflector' 
require_relative '../../../utils/sli_utils.rb' 
require_relative '../../../utils/selenium_common.rb' 


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
  id = "Loraine"                                                      if human_readable_id == "USER_FIRSTNAME"
  id = "Plyler"                                                       if human_readable_id == "USER_LASTNAME"
  id = "Super_Admin"                                                  if human_readable_id == "SUPER_ADMIN"
  
  #placeholder for provision and app registration link, need to be updated to check real link
  id = "controlpanel.example.com"                                     if human_readable_id == "URL_TO_PROVISIONING_APPLICATION"
  id = "controlpanel.example.com"                                     if human_readable_id == "URL_TO_APPLICATION_REGISTRATION"
 
  #return the translated value
  id
end

Given /^I have a SMTP\/Email server configured$/ do
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
  
  @userRegAppUrl = PropLoader.getProps['admintools_server_url']+"/user_registration"
  @prod = false 
  initializeApprovalAndLDAP(@email_conf, @prod)
  @driver.get @userRegAppUrl 
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
   assert(@driver.current_url.include?("#{@userRegAppUrl}/eula"))
   assertText("Terms of Use")
end

When /^the developer click "([^"]*)"$/ do |button|
  @driver.find_element(:xpath, "//input[contains(@id, '#{button.downcase}')]").click
end

Then /^the developer is directed to an acknowledgement page\.$/ do
  assertText("Your request was submitted.")
  assertText("You will receive an email asking you to verify your email address, " +
             "and then provide you with the next steps in the process.")
end

Then /^a verification email is sent to "([^"]*)"$/ do |email_address|
   sleep(5)
    verifyEmail()
end

When /^the developer click link in verification email$/ do
  url=getVerificationLink()
  @driver.get url
end

Then /^an account entry is made in ldap with "([^"]*)" status$/ do |status|
  user=ApprovalEngine.get_user(@email)
  puts user
  assert(user[:status]==status.downcase,"didnt create account with status is #{status}")
end

Then /^an approval email is sent to the "([^"]*)"$/ do |email|
  @email = email
  verifyEmail()
  found=@email_subject.include?("Your account has been approved")
  assert(found,"didnt receive approval email!")
end

Then /^the email has a "([^"]*)"$/ do |link|
  found = @email_content.include?(link)
  assert(found,"the email doesnt have the link for #{link}")
end

Then /^a "([^"]*)" roles is a added for the user in ldap$/ do |role|
  roles=@ldap.get_user_groups(@email)
  assert(roles.include?(role),"didnt add #{role} role in ldap")
end

When /^the user clicks on "([^"]*)"$/ do |link|
  url=PropLoader.getProps['admintools_server_url']+"/"+link
  @driver.get url
end

Then /^the user has to authenticate against ldap using "([^"]*)" and "([^"]*)"$/ do |user, pass|
  step "I submit the credentials \"#{user}\" \"#{pass}\" for the \"Simple\" login page"
end

Then /^the user is redirected to "([^"]*)"$/ do |link|
  # do nothing
end

When /^the user selects the option to use the "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^clicks on "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^an "([^"]*)" is saved to mongo$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^an "([^"]*)" is added in the application table for "([^"]*)","([^"]*)", "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  pending # express the regexp above with the code you wish you had
end

Then /^a request for a Landing zone is made with "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^a tenant entry with "([^"]*)" and "([^"]*)" is added to mongo$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the "([^"]*)" is saved in Ldap$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^the "([^"]*)" is saved in ldap$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^the user has an approved account$/ do
  pending # express the regexp above with the code you wish you had
end

When /^the user accesses the "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^the user is authenticated using the "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^the user is successfully authenticated$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^the user can access "([^"]*)", "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

When /^the user clicks on  "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^the user is authenticated using "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|

  pending # express the regexp above with the code you wish you had
end

Then /^the user can access "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

def initializeApprovalAndLDAP(emailConf, prod)
  # ldapBase need to be configured in admin-tools and acceptance test to match simple idp branch
   ldapBase=PropLoader.getProps['ldap_base']
  # ldapBase = "ou=DevTest,dc=slidev,dc=org" 
   @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], 389, ldapBase, "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{") 
   email = Emailer.new emailConf 
   ApprovalEngine.init(@ldap, email, !prod) 
 end 


def verifyEmail
  
    defaultUser = 'devldapuser'
    defaultPassword = 'Y;Gtf@w{'
    imap = Net::IMAP.new('mon.slidev.org', 993, true, nil, false)
    imap.authenticate('LOGIN', defaultUser, defaultPassword)
    imap.examine('INBOX')
    
    ids = imap.search(["FROM", @email_sender_name,"TO",@email])
    puts ids
    content = imap.fetch(ids[-1], "BODY[TEXT]")[0].attr["BODY[TEXT]"]
    subject = imap.fetch(ids[-1], "BODY[HEADER.FIELDS (SUBJECT)]")[0].attr["BODY[HEADER.FIELDS (SUBJECT)]"]
    found = true if content != nil
    @email_content = content
    @email_subject = subject
    puts subject,content
    imap.disconnect
    assert(found, "Email was not found on SMTP server")
end

def getVerificationLink
if @email_content.include? "http://"
link="http://"+@email_content.split("http://")[-1]
else 
link="https://"+@email_content.split("https://")[-1]
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
