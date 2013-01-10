require 'ldapstorage'


if (ARGV.length < 8)
  puts "usage: ldap_host ldap_port use_ssl ldap_base ldap_admin_user ldap_pass number_to_create offset group ..."
  puts "       use_ssl = {true | false}"
  exit(1)
end

ldap_host = ARGV[0]
ldap_port = ARGV[1].to_i
use_ssl = (ARGV[2] == "true")
ldap_base = ARGV[3]
ldap_user = ARGV[4]
ldap_pass = ARGV[5]

count = ARGV[6].to_i
offset = ARGV[7].to_i

groups = []
ARGV[8..-1].each {|g| groups << g}

ldap_storage = LDAPStorage.new(ldap_host, ldap_port, ldap_base, ldap_user, ldap_pass, use_ssl)

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