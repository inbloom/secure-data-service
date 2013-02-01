=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


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

ldap = LDAPStorage.new("ldap.slidev.org", 389, "ou=SLIAdmin,dc=slidev,dc=org", "cn=DevLDAP User, ou=People,dc=slidev,dc=org", "Y;Gtf@w{", false)
ldap.create_user(User_info)
ldap.add_user_group(Jd_email, "Application Developer")
