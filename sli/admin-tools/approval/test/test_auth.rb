testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'


ldap = LDAPStorage.new("rcldap01.slidev.org", 636, "ou=SLIAdmin,ou=rcEnvironment,dc=slidev,dc=org", "cn=admin,dc=slidev,dc=org", "Y;Gtf@w{")

username = "ychen"
password = "yC12345!"

puts "AUTH result: #{ldap.authenticate(username, password)}"
