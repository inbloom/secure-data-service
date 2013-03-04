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

if __FILE__ == $0
  unless ARGV.length == 1
    puts "Usage: " + $0 + " email_address "
    puts
    puts "Removes the user from LDAP (including all groups she belongs to"
    exit(1)
  end

  # the email address that identifies the user to remove
  email_address = ARGV[0]

  # set up ldap
  # ldap = LDAPStorage.new("ldap.slidev.org",
  #                        389,
  #                        "ou=SLIAdmin,dc=slidev,dc=org",
  #                        "cn=DevLDAP User, ou=People,dc=slidev,dc=org",
  #                        "Y;Gtf@w{", false)
  ldap = LDAPStorage.new("rcldap01.slidev.org", 636, "ou=SLIAdmin,ou=rcEnvironment,dc=slidev,dc=org", "cn=Admin,dc=slidev,dc=org", "Y;Gtf@w{", true)

  # make sure the user is removed from all groups even if it doesn't exist in people anymore
  curr_groups = ldap.get_user_groups(email_address)
  curr_groups.each do |group_id|
    ldap.remove_user_group(email_address, group_id)
  end
  new_groups = ldap.get_user_groups(email_address)
  if new_groups.empty?
    if !curr_groups.empty?
      puts "Successfully removed user #{email_address} from all groups."
    end
  else
    puts "Could not remove user #{email_address} from these groups: #{new_groups}."
  end

  # get the username
  if ldap.user_exists?(email_address)
    ldap.delete_user(email_address)
    if !ldap.user_exists?(email_address)
      puts "Success. Removed user #{email_address}."
    else
      puts "Failure. Could not remove #{email_address}."
    end
  else
    puts "User #{email_address} does not exist. "
  end
end
