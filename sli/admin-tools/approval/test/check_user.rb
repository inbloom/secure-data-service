#!ruby 

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
require 'yaml'


LDAP_WO_CONSTANTS = {
    :loginShell           =>  "/sbin/nologin"
}
RO_LDAP_ATTR_MAPPING = {
    :createTimestamp => :created,
    :modifyTimestamp => :updated,
    :cn              => :cn
}
LDAP_DESCRIPTION_FIELD = :description
LDAP_ATTR_MAPPING = {
    :givenname            => :first,
    :sn                   => :last,
    :uid                  => :email,
    :userPassword         => :password,
    :o                    => :vendor,
    :displayName          => :emailtoken,
    :destinationIndicator => :status,
    :homeDirectory        => :homedir,
    :uidNumber            => :uidnumber,
    :gidNumber            => :gidnumber,
    :mail                 => :emailAddress
}
COMBINED_LDAP_ATTR_MAPPING = LDAP_ATTR_MAPPING.merge(RO_LDAP_ATTR_MAPPING)

if __FILE__ == $0
  unless ARGV.length == 2
    puts "Usage: " + $0 + " config.yml environment account_name "
    exit(1)
  end

  config = YAML::load( File.open( ARGV[0] ) )[ARGV[1]]
  puts "BASE: #{config['ldap_base']}"
  ldap = LDAPStorage.new(config['ldap_host'], config['ldap_port'], config['ldap_base'], config['ldap_user'], config['ldap_pass'], false)

  users = ldap.read_users
  puts "-------------------------------"
  users.each do |u|
    puts u[:email]
  end
  puts "FOUND #{users.length}"
end
