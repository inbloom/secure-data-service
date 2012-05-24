require 'rubygems'
require 'net/ldap'
require 'date'

ldap_conf = { :host => "ldap.slidev.org",
     :port => 389,
     :base => "ou=DevTest,dc=slidev,dc=org",
     :auth => {
           :method => :simple,
           :username => "cn=DevLDAP User, ou=People,dc=slidev,dc=org",
           :password => "Y;Gtf@w{"
     }}


ldap_conf = { 
    :host => "rcldap01.slidev.org",
    :port => 636,
    :base => "ou=people,dc=slidev,dc=org",
    :auth => {
      :method => :simple,
      :username => "cn=admin,dc=slidev,dc=org",
      :password => "Y;Gtf@w{"
    },
    :encryption => {
      :method => :simple_tls
    }
}

puts "TRYING TO OPEN"
ldap = Net::LDAP.new ldap_conf

# ldap = Net::LDAP.new
# ldap.host = "ldap.slidev.org"
# ldap.port = 389
# ldap.base = "ou=DevTest, dc=slidev, dc=org"
# #ldap.auth "devldapuser", "Y;Gtf@w{"
# ldap.auth "cn=DevLDAP User, ou=People,dc=slidev,dc=org"

# adding a record



if ldap.bind
  puts "Authentication successfull."
else
  puts "Authentication failed."
end
raise "DONE"

filter = Net::LDAP::Filter.eq( "cn", "*" )
fetch_attributes = [
    :givenname,
    :sn,
    :uid,
    :userpassword,
    :o,
    :displayname,
    :destinationindicator,
    :createTimestamp,
    :modifyTimestamp
]
ldap.search(:filter => filter , :attributes => fetch_attributes) do |entry|
  puts "DN: #{entry.dn}"
  entry.each do |attribute, values|
    puts "   attr:#{attribute}:"
    values.each do |value|
      puts "      --->#{value}"
    end
  end
end


dn = "cn=XXX Gray,ou=people,ou=DevTest,dc=slidev,dc=org"
attr = {
  :cn => "XXX Gray",
  :objectclass => ["top", "inetOrgPerson"],
  :sn => "Gray",
  # :gn => "Charles",
  # :mail => "charles@example.com",
  # :uid  => "charles@example.com",
  :userPassword => "something",
  :uid => "charles@example.com"
}

# dn = Net::LDAP::DN.new (
#   :cn => "Linda Kim",
#   :objectclass => ["abc", "efg"],
#   :sn => "new",
#   :uid => "x4732")

Net::LDAP.open(ldap_conf) do |myldap|
  if !myldap.add(:dn => dn, :attributes => attr)
    puts "ERROR ADDING: #{myldap.get_operation_result.message}"
  else
    puts "Success !"
  end
end
puts "After"
abc_group = "cn=abc,ou=groups,ou=DevTest,dc=slidev,dc=org"
efg_group = "cn=efg,ou=groups,ou=DevTest,dc=slidev,dc=org"

result = ldap.search(:base => "ou=groups,ou=DevTest,dc=slidev,dc=org", :filter => Net::LDAP::Filter.eq( "cn", "abc")).to_a()
puts "Group result search: #{result}"
group_exists = !result.empty?
puts "Group exists: #{group_exists}"

if !group_exists
  member_attrib = {
    :cn => "abc",
    :objectclass => ["groupOfNames", "top"],
    :member => dn
  }
  puts "Adding group: #{ldap.add(:dn => abc_group, :attributes => member_attrib)}"
else
  puts "Adding member: #{ldap.modify(:dn => abc_group, :operations => [[:add, "member", dn]])}"
end

result = ldap.search(:base => "ou=groups,ou=DevTest,dc=slidev,dc=org", :filter => Net::LDAP::Filter.eq( "cn", "abc")).to_a()
puts "FOUND: #{result}"
puts "\n\nFound user at index: #{result[0][:member].index(dn)}"

# if !ldap.modify(:dn => abc_group, :operations => [[:add, "member", dn]])
#   puts "ERROR: #{ldap.get_operation_result.message}"
# end

