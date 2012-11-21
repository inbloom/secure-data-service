require 'ldapstorage'


if (ARGV.length < 8)
  puts "usage: ldap_host ldap_port ldap_base ldap_admin_user ldap_pass number_to_create offset group ..."
  exit(1)
end

ldap_host = ARGV[0]
ldap_port = ARGV[1].to_i
ldap_base = ARGV[2]
ldap_user = ARGV[3]
ldap_pass = ARGV[4]

count = ARGV[5].to_i
offset = ARGV[6].to_i

groups = []
ARGV[7..-1].each {|g| groups << g}

ldap_storage = LDAPStorage.new(ldap_host, ldap_port, ldap_base, ldap_user, ldap_pass)

user = {}
user[:first] = 'First'
user[:last] = 'Last'
user[:password] = 'test1234'
user[:vendor] = 'test'
user[:emailtoken] = 'abc'
user[:status] = 'approved'
user[:uidnumber] = "500"
user[:homedir] = '/sbin/nologin'
user[:resetKey] = 'test'

(1..count).each do |i|
  user[:email] = "AAAA-generated-user-#{i + offset}@wgen.net"
  user[:emailAddress] = "AAAA-generated-user-#{i + offset}@wgen.net"
  puts "#{i}:   #{user}"
  ldap_storage.create_user(user)
  groups.each do |g|
    puts "Adding user to group #{g}"
    ldap_storage.add_user_group(user[:email], g)
  end
end