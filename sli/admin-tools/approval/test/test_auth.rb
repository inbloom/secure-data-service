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


testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"
require 'approval'


ldap = LDAPStorage.new("rcldap01.slidev.org", 636, "ou=SLIAdmin,ou=rcEnvironment,dc=slidev,dc=org", "cn=admin,dc=slidev,dc=org", "Y;Gtf@w{", true)

username = "ychen"
password = "yC12345!"

puts "AUTH result: #{ldap.authenticate(username, password)}"
