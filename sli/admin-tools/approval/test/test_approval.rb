$LOAD_PATH << '.'
require 'approval'

ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
emailer = Emailer.new(:host => 'mon.slidev.org', :sender_name => 'Test Sender', :sender_email_addr => 'devldapuser@slidev.org')


jd_email = "jdoe@example.com"
jd_emailtoken = "0102030405060708090A0B0C0D0E0F"
user_info = {
	:first      => "John",
	:last       => "Doe", 
	:email      => jd_email,
	:password   => "secret", 
	:vendor     => "Acme Inc.",
	:emailtoken => jd_emailtoken,
	:status     => "submitted"	
}


ApprovalEngine.init(ldap, emailer, false)
puts "Before adding user."
emailtoken = ApprovalEngine.add_disabled_user(user_info)
puts "Added user."
ApprovalEngine.verify_email(emailtoken)
puts "Approved Email"

user = ApprovalEngine.get_users()[0]
ApprovalEngine.change_user_status(jd_email, ApprovalEngine::ACTION_APPROVE)

users = ApprovalEngine.get_users
puts "#{users}"
