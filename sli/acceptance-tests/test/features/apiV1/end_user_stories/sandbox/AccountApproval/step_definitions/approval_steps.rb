require 'approval'
require 'rumbster'
require 'message_observers'
require 'net/imap'
require_relative '../../../../../utils/sli_utils.rb'

Before do 
  
end

After do |scenario|
  @rumbster.stop if @rumbster
end

Given /^I have a "([^"]*)" SMTP\/Email server configured$/ do |live_or_mock|
  sender_email_address = "admin@SLC.org"
  @email_name = "SLC Admin"
  test_port = 2525
  
  if live_or_mock == "live"
    @mode = true
    @email_conf = {
      :host => 'mon.slidev.org',
      :port => 3000,
      :sender_name => @email_name,
      :sender_email_addr => sender_email_address
    }
  else
    @mode = false
    @rumbster = Rumbster.new(test_port)
    @message_observer = MailMessageObserver.new
    @rumbster.add_observer @message_observer
    @rumbster.start
    @email_conf = {
      :host => '127.0.0.1',
      :port => test_port,
      :sender_name => @email_name,
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

Given /^login name "([^"]*)" ([^"]*) in the account request queue$/ do |arg1, status|
  intializaApprovalEngineAndLDAP()
  if @mode
    @userinfo[:email] = "devldapuser@slidev.org"
  else
    @userinfo[:email] = arg1
  end
  
  @userinfo[:password] = "1234"
  @userinfo[:emailtoken] = "qwerty"
  
  # delete if there and create a new user to set fixture
  ApprovalEngine.remove_user(@userinfo[:email])
  ApprovalEngine.add_disabled_user(@userinfo)
  
  # set status manually to pending to test Approval Engine transitions
  @userinfo[:status] = status
  @ldap.update_user_info(@userinfo)
  
  assert(@ldap.read_user(@userinfo[:email])[:status] == status, "User #{arg1} is not in #{status} state")
  
end

When /^I approve the account request$/ do
  @time = Time.now.getutc
  ApprovalEngine.change_user_status(@userinfo[:email], "approve")
  assert(@ldap.read_user(@userinfo[:email])[:status] == "approved", "User #{@userinfo[:email]} is not in pending state")
end

Then /^a new account is created in production LDAP with login name "([^"]*)" and the role is "([^"]*)"$/ do |arg1, arg2|
  arg1 = "devldapuser@slidev.org" if @mode
  assert(@ldap.read_user(@userinfo[:email])[:email] == arg1, "User #{@userinfo[:email]} is not created in LDAP")
  #assert(@ldap.read_user(@userinfo[:email])[:role] == arg2, "User #{@userinfo[:email]} is does not have role #{arg2}")
end

Then /^an email is sent to the requestor with a link to the application registration tool$/ do
  if @mode
    defaultUser = 'devldapuser'
    defaultPassword = 'Y;Gtf@w{'
    imap = Net::IMAP.new('mon.slidev.org', 993, true, nil, false)
    imap.authenticate('LOGIN', defaultUser, defaultPassword)
    imap.examine('INBOX')
    
    ids = imap.search(["FROM", @email_name])
    content = imap.fetch(ids[-1], "BODY[TEXT]")[0].attr["BODY[TEXT]"]
    subject = imap.fetch(ids[-1], "BODY[HEADER.FIELDS (SUBJECT)]")[0].attr["BODY[HEADER.FIELDS (SUBJECT)]"]
    found = true if content != nil
    imap.disconnect
    assert(found, "Email was not found on SMTP server")
    assert(subject.include?("Your account has been approved."), "Subject in email is not correct")
  else
    assert(@message_observer.messages.size == 1, "Number of messages is not equal to 1")
    email = @message_observer.messages.first
    assert(email != nil, "email was not received")
    assert(email.to[0] == @userinfo[:email], "email address was incorrect")
    assert(email.subject == "Your account has been approved.", "email did not have correct subject")
  end
  
end

When /^I reject the account request$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], "reject")
  assert(@ldap.read_user(@userinfo[:email])[:status] == "rejected", "User #{@userinfo[:email]} is in rejected state")
end

Then /^an account exists in production LDAP with login name "([^"]*)"$/ do |arg1|
  assert(ApprovalEngine.user_exists?(@userinfo[:email]), "User does not exist in LDAP")
end

Then /^state is "([^"]*)"$/ do |arg1|
  assert(@ldap.read_user(@userinfo[:email])[:status] == "rejected", "User #{@userinfo[:email]} is in rejected state")
end


When /^I disable the account$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], "disable")
end

Then /^production LDAP account with login name "([^"]*)" is set as inactive$/ do |arg1|
  assert(@ldap.read_user(@userinfo[:email])[:status] == "disabled", "User #{@userinfo[:email]} is not in disabled state")
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
