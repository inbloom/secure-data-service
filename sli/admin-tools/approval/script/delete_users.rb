require 'ldapstorage'


if (ARGV.length < 6)
  puts "usage: ldap_host ldap_port ldap_base ldap_admin_user ldap_pass uid_wildcard"
  exit(1)
end

ldap_host = ARGV[0]
ldap_port = ARGV[1].to_i
ldap_base = ARGV[2]
ldap_user = ARGV[3]
ldap_pass = ARGV[4]

wildcard = ARGV[5]

ldap_storage = LDAPStorage.new(ldap_host, ldap_port, ldap_base, ldap_user, ldap_pass)



users = ldap_storage.search_users_raw(wildcard)

users.each_with_index do |u, i|
  puts "Deleting user #{i}/#{users.size}: #{u[:uid]}"
  u[:uid].each do |uid|
    groups = ldap_storage.get_user_groups(uid)
    groups.each do |g|
      puts "\t\tRemoving from group #{g}"
      ldap_storage.remove_user_group(uid, g)
    end
    ldap_storage.delete_user(uid)
  end

end