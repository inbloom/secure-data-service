require 'approval'
require 'rumbster'
require 'message_observers'
require_relative '../../../../../utils/sli_utils.rb'

Before do 
  
end

After do |scenario|
  @rumbster.stop if @rumbster
  sleep(1)
end

Given /^I have a "([^"]*)" SMTP\/Email server configured$/ do |live_or_mock|
  sender_email_address = "admin@SLC.org"
  email_name = "SLC Admin"
  test_port = 2525
  @live = (live_or_mock == "live")
  
  if @live
    @email_conf = {
      :host => 'mon.slidev.org',
      :port => 3000,
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

Given /^a ([^"]*) account request for vendor "([^"]*)"$/ do |environment, vendor|
  if defined? @userinfo
    @userinfo[:vendor] = vendor
  else
    @userinfo = Hash.new
    @userinfo[:vendor] = vendor
  end

  @prod = (environment == "production")
end

Given /^first name "([^"]*)" and last name "([^"]*)"$/ do |first_name, last_name|
  @userinfo[:first] = first_name
  @userinfo[:last]  = last_name
end

Given /^login name "([^"]*)" ([^"]*) in the account request queue$/ do |email, status|
  intializaApprovalEngineAndLDAP(@email_conf, @prod)
  if @live
    @userinfo[:email] = "devldapuser@slidev.org"
  else
    @userinfo[:email] = email
  end
  
  @userinfo[:password] = "1234"
  @userinfo[:emailtoken] = "qwerty"
  
  # delete if there and create a new user to set fixture
  ApprovalEngine.remove_user(@userinfo[:email])
  ApprovalEngine.add_disabled_user(@userinfo)
  
  # set status manually to pending to test Approval Engine transitions
  # @userinfo[:status] = status
  if @prod
    #@ldap.update_user_info(@userinfo)
    ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_VERIFY_EMAIL, true)
    assert(@ldap.read_user(@userinfo[:email])[:status] == status, "User #{email} is not in #{status} state")
  else
    # verifying the email puts user in pending state which sends the email and goes directly to approved state
    ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_VERIFY_EMAIL, true)
    assert(@ldap.read_user(@userinfo[:email])[:status] == ApprovalEngine::STATE_APPROVED, "User #{email} is not in approved state")
  end
  
end

When /^I approve the account request$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEnginer::ACTION_APPROVE)
  state = @ldap.read_user(@userinfo[:email])[:status]
  assert(state == "approved", "User #{@userinfo[:email]} is in #{state} but should be in approved state")
end

Then /^a new account is created in ([^"]*) LDAP with login name "([^"]*)" and the role is "([^"]*)"$/ do |environment, login_name, role|
  login_name = "devldapuser@slidev.org" if @live
  assert(@ldap.read_user(@userinfo[:email])[:email] == login_name, "User #{@userinfo[:email]} is not created in LDAP")
  #assert(@ldap.read_user(@userinfo[:email])[:role] == role, "User #{@userinfo[:email]} is does not have role #{role}")
end

Then /^an email is sent to the requestor with a link to the application registration tool$/ do
## TODO: verify link
  verifyEmail
end

When /^I reject the account request$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], "reject")
  assert(@ldap.read_user(@userinfo[:email])[:status] == "rejected", "User #{@userinfo[:email]} is in rejected state")
end

Then /^an account exists in production LDAP with login name "([^"]*)"$/ do |login_name|
  assert(ApprovalEngine.user_exists?(@userinfo[:email]), "User " << @userinfo[:email] << " does not exist in LDAP")
  assert(ApprovalEngine.user_exists?(login_name), "User " << login_name << " does not exist in LDAP")
end

Then /^state is "([^"]*)"$/ do |state|
  user_state = @ldap.read_user(@userinfo[:email])[:status] 
  assert(user_state == state, "User #{@userinfo[:email]} is in #{user_state} but should be in #{state} state")
end

When /^I disable the account$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_DISABLE, true)
end

Then /^([^"]*) LDAP account with login name "([^"]*)" is set as inactive$/ do |arg1, arg2|
  assert(@ldap.read_user(@userinfo[:email])[:status] == "disabled", "User #{@userinfo[:email]} is not in disabled state")
end

Then /^an email is sent to the requestor with a link to provision sandbox and a link for sandbox application registration tool$/ do
## TODO: verify links
  verifyEmail
end

#### Common methods ##############
def intializaApprovalEngineAndLDAP(email_conf = @email_conf, prod=true)
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap.hostname'], 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  
  email = Emailer.new email_conf
  ApprovalEngine.init(@ldap, email, !prod)
end

def verifyEmail
  if @live
  else
    assert(@message_observer.messages.size == 1, "Number of messages is #{@message_observer.messages.size} but should be 1")
    email = @message_observer.messages.first
    assert(email != nil, "email was not received")
    assert(email.to[0] == @userinfo[:email], "email address was incorrect")
    assert(email.subject == "Your account has been approved.", "email did not have correct subject")
  end
end
