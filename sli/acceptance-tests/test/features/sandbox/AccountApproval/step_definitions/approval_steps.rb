require 'approval'
require 'rumbster'
require 'message_observers'
require 'net/imap'
require_relative '../../../utils/sli_utils.rb'

Before do

end

After do |scenario|
  @rumbster.stop if @rumbster
  sleep(1)
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
  if @mode
    @userinfo[:email] = "devldapuser@slidev.org"
  else
    @userinfo[:email] = email
  end

  @userinfo[:password] = "1234"
  @userinfo[:emailtoken] = "qwerty"
  @userinfo[:homedir] = "test"
  @userinfo[:uidnumber] = "500"
  @userinfo[:gidnumber] = "500"

  # delete if there and create a new user to set fixture
  ApprovalEngine.remove_user(@userinfo[:email])
  ApprovalEngine.add_disabled_user(@userinfo)

  # set status manually to pending to test Approval Engine transitions
  if @prod
    if(status == ApprovalEngine::STATE_PENDING)
      ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_VERIFY_EMAIL, true)
    end
    if(status == ApprovalEngine::STATE_APPROVED)
      ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_VERIFY_EMAIL, true)
      ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_APPROVE, true)
    end
  else #it is sandbox
    # verifying the email puts user in pending state which sends the email and goes directly to approved state
    if(status == ApprovalEngine::STATE_APPROVED)
      ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_VERIFY_EMAIL, true)
    end
  end
  assert(@ldap.read_user(@userinfo[:email])[:status] == status, "User #{email} is not in #{status} state")
end

When /^I approve the account request$/ do
  @time = Time.now.getutc
  ApprovalEngine.change_user_status(@userinfo[:email], ApprovalEngine::ACTION_APPROVE)
  state = @ldap.read_user(@userinfo[:email])[:status]
  assert(state == "approved", "User #{@userinfo[:email]} is in #{state} but should be in approved state")
end

Then /^a new account is created in ([^"]*) LDAP with login name "([^"]*)" and the roles are "([^"]*)"$/ do |environment, login_name, roles|
  login_name = "devldapuser@slidev.org" if @mode
  roles_arr = roles.strip.split(",").map {|x| x.strip }
  assert(@ldap.read_user(@userinfo[:email])[:email] == login_name, "User #{@userinfo[:email]} is not created in LDAP")
  assert(@ldap.get_user_groups(@userinfo[:email]).sort == roles_arr.sort, "User #{@userinfo[:email]} is does not have roles #{roles_arr}")
end

Then /^an email is sent to the requestor with a link to the application registration tool$/ do
  verifyEmail
end

When /^I reject the account request$/ do
  ApprovalEngine.change_user_status(@userinfo[:email], "reject")
  assert(@ldap.read_user(@userinfo[:email])[:status] == "rejected", "User #{@userinfo[:email]} is in rejected state")
end

Then /^an account exists in production LDAP with login name "([^"]*)"$/ do |login_name|
  assert(ApprovalEngine.user_exists?(@userinfo[:email]), "User " << @userinfo[:email] << " does not exist in LDAP")
  #assert(ApprovalEngine.user_exists?(login_name), "User " << login_name << " does not exist in LDAP")
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
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")

  email = Emailer.new email_conf
  ApprovalEngine.init(@ldap, email, !prod)
end

def verifyEmail
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
    assert(subject.include?("Account Approval"), "Subject in email is not correct")
  else
    assert(@message_observer.messages.size == 1, "Number of messages is #{@message_observer.messages.size} but should be 1")
    email = @message_observer.messages.first
    assert(email != nil, "email was not received")
    assert(email.to[0] == @userinfo[:email], "email address was incorrect")
    assert(email.subject.include?("Account Approval"), "email did not have correct subject")
  end
end
