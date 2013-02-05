
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

if ARGV.count < 1
  puts "Usage: <dbhost:port> <database>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the database (Defaults to sli)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
end

database = (ARGV[1].nil? ? 'sli' : ARGV[1])
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


#for each realm, create a new roles entity
@db[:realm].find({}).each { |realm|
  @log.info "migrating realm #{realm['_id']} #{realm['body']['name']}"
  role = { 
    body: {
      realmId: realm['_id'], 
      roles: [], 
      customRights: []
    }, 
    metaData: {
      tenantId: realm['body']['tenantId']
    }
  }
  role[:metaData][:tenantId] = realm['metaData']['tenantId'] unless realm['metaData'].nil? || realm['metaData']['tenantId'].nil?

  unless realm['body']['mappings'].nil? || realm['body']['mappings']['role'].nil?
    realm['body']['mappings']['role'].each { |realmRole|
      names = realmRole['clientRoleName']
      rights = @sli_role_to_rights[realmRole['sliRoleName']]
      role[:body][:roles].push({names: names, rights: rights})
    }
  end

  @db[:customRole].save(role) unless realm['body']['uniqueIdentifier'] == "Shared Learning Infrastructure"
  @log.info "created customRole: #{role}"

  realm['body'].delete('mappings')
  @db[:realm].update({'_id' => realm['_id']}, realm)
  @log.info "finished migrating realm #{realm['_id']} #{realm['body']['name']}"
}

@log.info "Finished migration."

