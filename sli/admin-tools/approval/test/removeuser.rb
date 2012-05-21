require "socket"

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'

if __FILE__ == $0
	unless ARGV.length == 1
		puts "Usage: " + $0 + " email_address "
		puts
		puts "Removes the user from LDAP (including all groups she belongs to"
		exit(1)
	end

	# the email address that identifies the user to remove
	email_address = ARGV[0]

	# set up ldap 
	ldap = LDAPStorage.new("ldap.slidev.org", 
						   389, 
						   "ou=SLIAdmin,dc=slidev,dc=org", 
						   "cn=DevLDAP User, ou=People,dc=slidev,dc=org", 
						   "Y;Gtf@w{")

	# get the username
	if ldap.user_exists?(email_address)
		ldap.delete_user(email_address)
	   	if !ldap.user_exists?(email_address)
	   		puts "Success. Removed user #{email_address}."
	   	else
	   		puts "Failure. Could not remove #{email_address}."
		end	
	else
		puts "User #{email_address} does not exist. "
	end
end
