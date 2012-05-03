require 'approval'
require 'rumbster'
require 'message_observers'
require_relative '../../../../../utils/sli_utils.rb'

Before do 
  
end

After do |scenario|
  puts "in after hook, yay"
  @rumbster.stop if @rumbster
end

Given /^I have a "([^"]*)" SMTP\/Email server configured$/ do |live_or_mock|
  sender_email_address = "admin@SLC.org"
  email_name = "SLC Admin"
  test_port = 2525
  
  if live_or_mock == "live"
    @email_conf = {
      :host => 'mon.slidev.org',
      :sender_name => email_name,
      :sender_email_addr => sender_email_address
    }
  else
    @rumbster = Rumbster.new(test_port)
    @message_observer = MailMessageObserver.new
    @rumbster.add_observer @message_observer
    @rumbster.start
    @email_conf = {
      :host => '127.0.0.1',
      :port => test_port,
      :sender_name => email_name,
      :sender_email_addr => sender_email_address
    }
  end
end

Given /^a production account request for vendor "([^"]*)"$/ do |arg1|
  if defined? @userinfo
    @userinfo[:vendor] = arg1
  else
    @userinfo = Hash.new
    @userinfo[:vendor] = arg1
  end
end

Given /^first name "([^"]*)" and last name "([^"]*)"$/ do |arg1, arg2|
  @userinfo[:first] = arg1
  @userinfo[:last]  = arg2
end

Given /^login name "([^"]*)" pending in the account request queue$/ do |arg1|
  intializaApprovalEngineAndLDAP()
  
  @userinfo[:email] = "devldapuser@slidev.org"
  @userinfo[:password] = "1234"
  @userinfo[:emailtoken] = "qwerty"
  
  # delete if there and create a new user to set fixture
  ApprovalEngine.remove_user(@userinfo[:email])
  ApprovalEngine.add_disabled_user(@userinfo)
  
  # set status manually to pending to test Approval Engine transitions
  @userinfo[:status] = "pending"
  @ldap.update_user_info(@userinfo)
  
  assert(@ldap.read_user(@userinfo[:email])[:status] == "pending", "User #{arg1} is not in pending state")
  
end

When /^I approve the account request$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], "approve")
  
  email = @message_observer.messages.first
  assert(email != nil, "email was not received")
  assert(email.to[0] == @userinfo[:email], "email address was incorrect")
  assert(email.subject == "Your account has been approved.", "email did not have correct subject")
  
  assert(@ldap.read_user(@userinfo[:email])[:status] == "approved", "User #{@userinfo[:email]} is not in pending state")
end

Then /^a new account is created in production LDAP with login name "([^"]*)" and the role is "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^an email is sent to the requestor with a link to the application registration tool$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I reject the account request$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^a no account exists in production LDAP with login name "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^an approved production account for vendor "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^login name "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

When /^I disable the account$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^production LDAP account with login name "([^"]*)" is set as inactive$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^I submit a request for a a sandbox account request for vendor "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^a new account is created automatically in sandbox LDAP with login name "([^"]*)" and the role is "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^an email is sent to the requestor with a link to provision sandbox and a link for sandbox application registration tool$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^an approved sandbox account for vendor "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^sandbox LDAP account with login name "([^"]*)" is set as inactive$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

#### Common methods ##############
def intializaApprovalEngineAndLDAP(email_conf = @email_conf, sandbox=false)
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap.hostname'], 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  
  email = Emailer.new email_conf
  ApprovalEngine.init(@ldap, email, sandbox)
  
end
