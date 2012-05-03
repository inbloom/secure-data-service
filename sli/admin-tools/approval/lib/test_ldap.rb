require './ldapstorage'

ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
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

td_email = "tdoe@example.com"
td_user_info = user_info.clone 
td_user_info[:first] = "Tom"
td_user_info[:email] = td_email 

result = ldap.create_user(user_info)
puts "#{result}"
found_user = ldap.read_user("jdoe@example.com")

ldap.create_user(td_user_info)

puts "DN: #{found_user}"
found_user.each do |attribute, values|
	puts "   attr:#{attribute}: #{values}"
	# values.each do |value|
	# 	puts "      --->#{value}"
	# end
end

any_email = "anything@example.com" 
puts "User #{jd_email} exists: #{ldap.user_exists?(jd_email)}"
puts "User #{any_email} exists: #{ldap.user_exists?(any_email)}"

new_user = user_info.clone 
new_user[:status] = "pending"
ldap.update_status(new_user)
found_user = ldap.read_user(jd_email)
puts "Changed status: #{found_user[:status]}"

found_user = ldap.read_user_emailtoken(jd_emailtoken)
puts "#{found_user}"

found_user = ldap.read_user_emailtoken("nothing")
if !found_user
	puts "Did not find arbitrary emailtoken."
end


puts "Fetching all users:"
all_users = ldap.read_users
all_users.each do |e|
	puts e[:email]
end 

puts "-----------------------------\nFetching pending users:"
pending_users = ldap.read_users("pending")
pending_users.each do |e|
	puts e[:email]
end

found_user = true 
ldap.delete_user(jd_email)
found_user = ldap.read_user_emailtoken("nothing")
if !found_user
	puts "Did not find delete user."
end
ldap.delete_user(td_email)
