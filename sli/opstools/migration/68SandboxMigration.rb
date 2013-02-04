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

require 'rubygems'
require 'mongo'
require 'logger'

if ARGV.count < 1
  puts "This script is for SANDBOX environment only"
  puts "This script updates the customRole realmId attribute to have the same ID as the Shared Learning Collaborative realm"
  puts "Usage: <dbhost:port> <database>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the SLI database (Defaults to sli)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
end

database = (ARGV[1].nil? ? 'sli' : ARGV[1])
hp = ARGV[0].split(":")
connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25)

@log = Logger.new(STDOUT)

@db = connection[database]
if @db[:realm].remove({'body.uniqueIdentifier' => 'SandboxIDP'})
  @log.info "Deleted old SandboxIDP realm"
else
  "Failed to delete old SandboxIDP realm."
end

realm = @db[:realm].find_one({'body.uniqueIdentifier' => 'Shared Learning Collaborative'})
realm_id = realm['_id']

@db[:tenant].find({}).each { |tenant|
  db_name = tenant['body']['dbName']

  tenant_db = connection[db_name]
  tenant_db[:customRole].update({}, {'$set' => {'body.realmId' => realm_id}})
  @log.info "Updated realmId for customRole for tenant db: #{db_name}"
}                          

@log.info "Finished migration."
