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

require "mongo"

db_host = "localhost"
super_docs =[]
super_docs << "student"
super_docs << "section"
super_docs << "cohort"
super_docs << "program"

if ARGV.length == 0
  # use default host
  elsif ARGV.length == 1
    db_host = ARGV[0]
  else
    puts "Usage: #{$0} <mongodb_host>"
    puts "If no argument is given, the default is:"
    puts "\tmongodb_host = #{db_host}"
    exit(1)
  end

@db = Mongo::Connection.new(db_host).db('sli')
@update_log = []
tenant_coll = @db['tenant']
tenant_coll.find().each do |tenant|
  if tenant["body"].has_key? "dbName"
    @tenant_db = Mongo::Connection.new(db_host).db(tenant['body']['dbName'])
    superdoc_update_record = {}
    super_docs.each do |super_doc|
      if @tenant_db.collection_names.include? super_doc
        super_doc_coll = @tenant_db[super_doc]
        # super_doc_coll.ensure_index([['type',Mongo::ASCENDING]])
        update_count = 0
        super_doc_coll.find({"type" => {"$exists" => false}}).each do |super_doc_entity|
        # puts "found super doc without type field"
        # puts super_doc_entity
          update_count += 1
          super_doc_entity_updated = super_doc_entity.merge({"type" => super_doc})
          #puts "updated super doc entity is"
          #puts super_doc_entity_updated
          super_doc_coll.save(super_doc_entity_updated)
        end
        superdoc_update_record = superdoc_update_record.merge({super_doc => update_count}) if update_count >0
      end
    end
    @update_log << {"tenantId" => tenant['body']['tenantId'],"update_log" => superdoc_update_record}
  end
end

@update_log.each do |update|
  puts  "\nsuperdoc updates for tenantdb #{update['tenantId']}\n(collection_name => updated_entity_count)\n#{update['update_log']}" if update['update_log'].size>0
  puts  "\nno updates for tenantdb #{update['tenantId']}" if update['update_log'].size==0
end
