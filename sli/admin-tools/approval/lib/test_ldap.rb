require './ldapstorage'

ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=DevTest,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
user_info = {
	:first      => "John",
	:last       => "Doe", 
	:email      => "jdoe@example.com",
	:password   => "secret", 
	:vendor     => "Acme Inc.",
	:emailtoken => "0102030405060708090A0B0C0D0E0F",
	:status     => "submitted"	
}

result = ldap.create_user(user_info)
puts "#{result}"
found_user = ldap.read_user("jdoe@example.com")

puts "DN: #{found_user}"
found_user.each do |attribute, values|
	puts "   attr:#{attribute}:"
	values.each do |value|
		puts "      --->#{value}"
	end
end
