require "socket"

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'


# Note: Socket.gethostname is used to ensure uniqueness when testers are 
# running this simultaneously on different machines
Jd_email = "jdoe-testuser@example.com"
Jd_emailtoken = "0102030405060708090A0B0C0D0E0F"
User_info = {
:first      => "John",
:last       => "Doe", 
:email      => Jd_email,
:password   => "secret", 
:vendor     => "Acme Inc.",
:emailtoken => Jd_emailtoken,
:status     => "submitted",
:homedir    => 'test',
:uidnumber  => '456',
:gidnumber  => '123',
:tenant     => 'IL',
:edorg      => 'IL-DAYBREAK'
}

ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=SLIAdmin,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{")
ldap.create_user(User_info)
ldap.add_user_group(Jd_email, "Application Developer")
