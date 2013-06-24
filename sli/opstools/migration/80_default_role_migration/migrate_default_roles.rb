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

def add_role_names(custom_role, role, names)
  role_names = role['names']
  if role_names != nil
    names.each do |name|
      if !exists_across_role_group(custom_role, name)
            role_names << name
        end
    end
  else
    @log.info "No role names for custom role #{custom_role['_id']}, skipping"
    end 
end

def exists_across_role_group(custom_role, name)
    roles = custom_role['body']['roles']
    exists = false
    roles.each do |role|
       if role['names']!=nil && role['names'].include?(name)
           exists = true
           break
       end
    end
    exists
end

@log = Logger.new(STDOUT)

if ARGV.count < 1
  @log.warn 'Usage: migrate_default_roles.rb <host>:<port>/'
  @log.warn 'Usage: migrate_default_roles.rb <host>:<port> <sli_database_name>'
  @log.warn 'host - hostname for mongo instance' 
  @log.warn 'port - port mongo is running on (27017 is common)'
  @log.warn 'sli_database_name - optional, defaults to \'sli\''
  @log.warn '*** Note: These parameters must exist in the order they are presented ***'
  exit
end


@log.info '---------------------------------------------'
@log.info ' Migration script for new default role names '
@log.info '---------------------------------------------'

host_port = ARGV[0].strip.split(':')
sli_database_name = ARGV[1] 
if sli_database_name == nil
    @log.info 'Using default sli database name: sli'
  sli_database_name = 'sli'
end

mongodb   = Mongo::Connection.new(host_port[0], host_port[1].to_i, :pool_size => 10, :pool_timeout => 25)
sli_db = mongodb.db(sli_database_name)
tenant_coll = sli_db.collection('tenant')
tenants = tenant_coll.find({})

tenants.each do |tenant|
  database = tenant['body']['dbName']
  @log.info "Adding addtional roles names for each default role in db: #{database}"
  tenant_db = mongodb.db(database)
  custom_roles = tenant_db.collection('customRole')
  custom_role_docs = custom_roles.find({})
  custom_role_docs.each do |custom_role_doc|
    roles = custom_role_doc['body']['roles']
    roles.each do |role|
      if role['groupTitle'] == 'Educator'
          add_role_names(custom_role_doc, role, ['Teacher'])
      elsif role['groupTitle'] == 'Leader'
          add_role_names(custom_role_doc, role, ['Superintendent', 'Principal'])
      elsif role['groupTitle'] == 'IT Administrator'
          add_role_names(custom_role_doc, role, ['School Administrative Support Staff', 'LEA System Administrator', 'LEA Administrator'])
      elsif role['groupTitle'] == 'Aggregate Viewer'
          add_role_names(custom_role_doc, role, ['Specialist/Consultant'])
      end
    end
    custom_roles.remove({'_id' => custom_role_doc['_id']})
    custom_roles.insert(custom_role_doc)
  end
end