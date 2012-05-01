require 'rubygems'
require 'net/ldap'

ldap_conf = { :host => "ldap.slidev.org",
     :port => 389,
     :base => "ou=DevTest,dc=slidev,dc=org",
     :auth => {
           :method => :simple,
           :username => "cn=DevLDAP User, ou=People,dc=slidev,dc=org",
           :password => "Y;Gtf@w{"
     }}

ldap = Net::LDAP.new ldap_conf

# ldap = Net::LDAP.new
# ldap.host = "ldap.slidev.org"
# ldap.port = 389
# ldap.base = "ou=DevTest, dc=slidev, dc=org"
# #ldap.auth "devldapuser", "Y;Gtf@w{"
# ldap.auth "cn=DevLDAP User, ou=People,dc=slidev,dc=org"


if ldap.bind
  puts "Authentication successfull."
else
  puts "Authentication failed."
end

filter = Net::LDAP::Filter.eq( "cn", "*" )
ldap.search(:filter => filter ) do |entry|
  puts "DN: #{entry.dn}"
  entry.each do |attribute, values|
    puts "   attr:#{attribute}:"
    values.each do |value|
      puts "      --->#{value}"
    end
  end
end


dn = "cn=Charles Gray,ou=people,ou=DevTest,dc=slidev,dc=org"
attr = {
  :cn => "Charles Gray",
  :objectclass => ["top", "person"],
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
  puts myldap.add(:dn => dn, :attributes => attr)
end

