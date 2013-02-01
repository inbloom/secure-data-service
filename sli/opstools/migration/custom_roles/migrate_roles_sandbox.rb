
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
require 'SecureRandom'

if ARGV.count < 3
  puts "Usage: <dbhost:port> <sli.mongodb.database> <sandbox.realm.uniqueId>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t sli.mongodb.database - from canonical_config.yml, the name of the database (sli is common)"
  puts "\t sandbox.realm.uniqueId - from canonical_config.yml, the unique id of the sandbox realm (SandboxIDP is common)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
end

sandbox_realm_id = ARGV[2]
database = ARGV[1]
hp = ARGV[0].split(":")
class PKFactory
  def create_pk(row)
    return row if row[:_id]
    row.delete(:_id)      # in case it exists but the value is nil
    row['_id'] ||= SecureRandom.uuid
    row
  end
end
connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})

@log = Logger.new(STDOUT)
@db = connection.db(database, :pk => PKFactory.new)

@sli_role_to_rights = {}
@db[:roles].find({}).each { |role|
  @sli_role_to_rights[role['body']['name']] = role['body']['rights']
}

@sandbox_realm_id = {'_id' => "", 'body' => {}}
@db[:realm].find({}).each { |realm|
  @log.info "migrating realm #{realm['_id']} #{realm['body']['name']}"
  @sandbox_realm = realm if realm['body']['uniqueIdentifier'] == sandbox_realm_id
  realm['body'].delete('mappings')
  @db[:realm].update({'_id' => realm['_id']}, realm)
  @log.info "finished migrating realm #{realm['_id']} #{realm['body']['name']}"
}

#for each tenant, create a new roles entity
@db[:tenant].find({}).each { |tenant|
  @log.info "migrating tenant #{tenant['_id']} #{tenant['body']['tenantId']}"
  role = { 
    body: {
      realmId: @sandbox_realm['_id'],
      roles: [
        {groupTitle: "Educator", names: ["Educator"], rights: ["READ_GENERAL", "AGGREGATE_READ", "READ_PUBLIC"]},
        {groupTitle: "IT Administrator", names: ["IT Administrator"], rights: ["WRITE_RESTRICTED", "READ_GENERAL", "AGGREGATE_READ", "READ_PUBLIC", "READ_RESTRICTED", "WRITE_GENERAL"]}, 
        {groupTitle: "Leader", names: ["Leader"], rights: ["READ_GENERAL", "AGGREGATE_READ", "READ_PUBLIC", "READ_RESTRICTED"]}, 
        {groupTitle: "Aggregate Viewer", names: ["Aggregate Viewer"], rights: ["AGGREGATE_READ", "READ_PUBLIC"]} 
      ], 
      customRights: [] 
    }, 
    metaData: {
      tenantId:  tenant['body']['tenantId']
    }
  }

  @db[:customRole].save(role)
  @log.info "created customRole: #{role}"
}

@log.info "Finished migration."

