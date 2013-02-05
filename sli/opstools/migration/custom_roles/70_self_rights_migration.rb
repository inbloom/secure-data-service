#!/usr/bin/ruby
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

require 'logger'
require 'mongo'

@log = Logger.new(STDOUT)

if ARGV.count < 1
  @log.warn "Usage: 70_self_rights_migration.rb <host>:<port>"
  @log.warn "\t host - hostname for mongo instance"
  @log.warn "\t port - port mongo is running on (27017 is common)"
  @log.warn "*** Note: These parameters must exist in the order they are presented ***"
  exit
end

@log.info "--------------------------------------"
@log.info " Migration script for self-context    "
@log.info "--------------------------------------"

host_port = ARGV[0].strip.split(":")
mongodb   = Mongo::Connection.new(host_port[0], host_port[1].to_i, :pool_size => 10, :pool_timeout => 25, :safe => {:wtimeout => 500})
databases = mongodb.database_names
databases.each do |database|
  # skip these databases --> won't have customRole collection
  next if ['admin', 'config', 'ingestion_batch_job', 'sli', 'local'].include?(database)

  @log.info "Adding self rights for custom roles in db: #{database}"
  db               = mongodb.db(database)
  custom_roles     = db.collection('customRole')
  custom_role_docs = custom_roles.find({})
  custom_role_docs.each do |custom_role_doc|
    roles = custom_role_doc["body"]["roles"]
    roles.each do |role|
      if role["groupTitle"] == "Educator"
        role["selfRights"] = ["READ_RESTRICTED"] 
      elsif role["groupTitle"] != "Leader" && role["groupTitle"] != "IT Administrator"
        role["selfRights"] = ["READ_GENERAL", "READ_RESTRICTED"]
      end
    end
    custom_roles.remove({"_id" => custom_role_doc["_id"]})
    custom_roles.insert(custom_role_doc)
  end
end
