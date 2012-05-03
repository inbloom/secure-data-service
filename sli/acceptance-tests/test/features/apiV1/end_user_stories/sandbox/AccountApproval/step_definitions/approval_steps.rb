require 'approval'
require_relative '../../../../../utils/sli_utils.rb'

Given /^a production account request for vendor "([^"]*)"$/ do |arg1|
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap.hostname'], 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
  ApprovalEngine.init(@ldap,false)
  
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
  @userinfo[:email] = arg1
end

When /^I approve the account request$/ do
  @userinfo[:password] = "1234"
  @userinfo[:status] = "pending"
  @userinfo[:emailtoken] = "qwerty"
  ApprovalEngine.remove_user(@userinfo[:email])
  ApprovalEngine.add_disabled_user(@userinfo)
  
  ApprovalEngine.change_user_status(@userinfo[:email], "approved")
  
  puts @ldap.read_user(@userinfo[:email])
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
