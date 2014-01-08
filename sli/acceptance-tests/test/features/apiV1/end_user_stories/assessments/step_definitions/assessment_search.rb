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

require 'mongo'
require 'stomp'
require 'json'
require_relative '../../../../search/step_definitions/search_indexer_steps.rb'

# TODO: figure out a clean way to remove deterministic IDs from stepdef
When /^I update the "(.*?)" with ID "(.*?)" field "(.*?)" to "(.*?)"$/ do |collection, id, field, value|
  conn = Mongo::Connection.new(Property["ingestion_db"], Property["ingestion_db_port"])
  mdb = conn.db(MIDGAR_DB_NAME)

  coll = mdb[collection]
  entity = coll.find_one({"_id" => id})
  assert(entity, "cant find #{collection} with id #{id}")
  entry = entity
  subfields = field.split(".")
  last = subfields.pop
  subfields.each { |subfield|
    entry = entry[subfield]
  }
  entry[last] = value
  puts "saving entity: #{entity}"
  coll.save(entity)
end

When /^I delete the "(.*?)" with ID "(.*?)"$/ do |collection, id|
  conn = Mongo::Connection.new(Property["ingestion_db"], Property["ingestion_db_port"])
  mdb = conn.db(MIDGAR_DB_NAME)

  coll = mdb[collection]
  doc = coll.find_one({"_id" => id})
  assert(doc, "cant find #{collection} with id #{id}")
  puts "deleting entity: #{doc}"
  coll.remove({"_id" => id})

  # save it so that it can be restored later.
  @last_deleted_doc = doc
  @last_deleted_doc_collection = collection
end

When /^I create the previously deleted entity$/ do
  conn = Mongo::Connection.new(Property["ingestion_db"], Property["ingestion_db_port"])
  mdb = conn.db(MIDGAR_DB_NAME)
  coll = mdb[@last_deleted_doc_collection]
  puts "inserting entity: #{@last_deleted_doc}"
  coll.insert(@last_deleted_doc)
  @last_created_doc = @last_deleted_doc
  @last_created_doc_collection = @last_deleted_doc_collection
end

When /^I send an update event to the search indexer for collection "(.*?)" and ID "(.*?)"$/ do |collection, id|
  update_message = {
      "ns" => "#{MIDGAR_DB_NAME}.#{collection}",
      "o" => { "$set" => { "type" => collection } },
      "o2" => { "_id" => id },
      "op" => "u"
  }
  publish_oplog_message(update_message)
end

When /^I send a delete event to the search indexer for collection "(.*?)" and ID "(.*?)"$/ do |collection, id|
  delete_message = {
      "ns" => "#{MIDGAR_DB_NAME}.#{collection}",
      "b" => true,
      "o" => { "_id" => id },
      "op" => "d"
  }
  publish_oplog_message(delete_message)
end

When /^I send an insert event to the search indexer from last created entity$/ do
  insert_message = {
      "ns" => "#{MIDGAR_DB_NAME}.#@last_created_doc_collection",
      "o" => @last_created_doc,
      "op" => "i"
  }
  publish_oplog_message(insert_message)
end

Then /^I will EVENTUALLY GET "(.*?)" with (\d+) elements$/ do |query, count|
  success = false
  10.times {
    step "I navigate to GET \"#{query}\""
    if count.to_i == @result.size
      success = true
      break
    end
    sleep 1
  }
  assert(success, "expected #{count} elements but got back #{@result.size}")
end

def publish_oplog_message(message)
  client = Stomp::Client.new
  client.publish("search", [message].to_json)
end
