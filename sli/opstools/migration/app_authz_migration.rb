=begin

Copyright 2012 Shared Learning Collaborative, LLC

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
require 'benchmark'
require 'set'
require 'date'
require 'logger'

if ARGV.count < 2
  puts ""
  puts "*************************************************************************"
  puts "Usage: app_authz_migration <dbhost>:<port> <tenant>"
  puts "\t dbhost - hostname for mongo connection"
  puts "\t port   - port mongo is running on (27017 is common)"
  puts "\t tenant - the tenant to perform the migration for (in sandbox: developer email)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
else
  @basic_options = {:timeout => false}
  server, port = ARGV[0].split(':')
  tenantId = ARGV[1]
  database = 'sli'
  
  connection = Mongo::Connection.new(server, port.to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
  log = Logger.new(STDOUT)
  db = connection[database] 
  
  # application migration
  # -> migrate ed org names to ed org ids in 'body.authorized_ed_orgs' in application collection
  
  # not started yet
  
  # applicationAuthorization migration
  # -> migrate application names to application ids in 'body.appIds' in applicationAuthorization collection  
  # -> start with specifying tenant in command line options
  #    - next step: query tenant collection and get unique tenants --> iterate and migrate each tenant's application authorization
  
  startTime = Time.now  
  @db['applicationAuthorization'].find({'metaData.tenantId' => tenantId}, @basic_options) { |cursor|
      cursor.each { |app_authz_doc|
        appIds = []
        i = app_authz_doc['_id']
        app_authz_doc['body']['appIds'].each { |name| 
		  @db[:student].find({'body.name' => name}, {fields: ['_id']}.merge(@basic_options)) { |item|
		  item.each { |app_id|
		    appIds << app_id
		  }
		}
		appIds = appIds.flatten.uniq
		collection.update({"metaData.tenantId" => tenant, "_id" => i}, {"$unset" => {"padding" => 1}, '$set' => {'body.appIds' => appIds}})
      }
    }
  
  finalTime = Time.now - startTime
  @log.info "\t application authorization migration took: #{finalTime} seconds."
end