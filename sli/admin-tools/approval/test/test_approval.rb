testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'
require 'test/unit'

class MockEmailer 
	def send_approval_email(email_address, first, last)
		@last_call = [email_address, first, last]
	end
end

class TestApprovalEngine < Test::Unit::TestCase

	def setup
	end

	

















ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
#mock_emailer = Emailer.new(:host => 'mon.slidev.org', :sender_name => 'Test Sender', :sender_email_addr => 'devldapuser@slidev.org')
mock_emailer = MockEmailer.new

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

ApprovalEngine.init(ldap, mock_emailer, false)
puts "Before adding user."
emailtoken = ApprovalEngine.add_disabled_user(user_info)
puts "Added user."
ApprovalEngine.verify_email(emailtoken)
puts "Approved Email"

user = ApprovalEngine.get_users()[0]
ApprovalEngine.change_user_status(jd_email, ApprovalEngine::ACTION_APPROVE)

users = ApprovalEngine.get_users
puts "#{users}"

ApprovalEngine.remove_user(jd_email)


# ApprovalEngine.init(ldap, mock_emailer, true)
# emailtoken = ApprovalEngine.add_disabled_user(user_info)
# puts "Added user."
# ApprovalEngine.verify_email(emailtoken)
# puts "Approved Email"

# users = ApprovalEngine.get_users
# users.each do |u| 
# 	puts "#{u[:first]} #{u[:last]} (#{u[:email]}) : #{u[:status]}"
# end 

# ApprovalEngine.remove_user(jd_email)


