
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
  puts "Usage: <dbhost:port> <database>"
  puts "\t dbhost - hostname for mongo"
  puts "\t port - port mongo is running on (27017 is common)"
  puts "\t database - the name of the database (Defaults to sli)"
  puts "*** Note: These parameters must exist in the order they are presented ***"
  exit
end

database = (ARGV[1].nil? ? 'sli' : ARGV[1])
hp = ARGV[0].split(":")
connection = Mongo::Connection.new(hp[0], hp[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})

@log = Logger.new(STDOUT)
@db = connection[database]


@app_name_to_id = {}
@db[:application].find({}).each { |app|
  @app_name_to_id[app['body']['name']] = app['_id']

  ed_org_ids = []
  state_ids = app['body']['authorized_ed_orgs']
  unless state_ids.nil?
    state_ids.each { |state_id|
      @db[:educationOrganization].find({'body.stateOrganizationId' => state_id}).each { |ed_org|
        ed_org_ids.push ed_org['_id']
      }
    }
  end

  @db[:application].update({'metaData.tenantId' => app['metaData']['tenantId'], '_id' => app['_id']},
                           {"$unset" => {"padding" => 1}, '$set' => {'body.authorized_ed_orgs' => ed_org_ids}})
  @log.info "migrated application #{app['_id']} #{app['body']['name']}"
}

@db[:applicationAuthorization].find({}).each { |app_auth|
  ed_org = @db[:educationOrganization].find_one({'metaData.tenantId' => app_auth['metaData']['tenantId'], 'body.stateOrganizationId' => app_auth['body']['authId']})
  ed_org_id = ed_org.nil? ? '' : ed_org['_id']

  @db[:applicationAuthorization].update({'metaData.tenantId' => app_auth['metaData']['tenantId'], '_id' => app_auth['_id']},
                           {"$unset" => {"padding" => 1}, '$set' => {'body.authId' => ed_org_id}})
  @log.info "migrated applicationAuthorization #{app_auth['_id']}"
}

@log.info "Finished migration."

