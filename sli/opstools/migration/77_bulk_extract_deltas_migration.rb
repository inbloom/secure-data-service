#!/usr/bin/env ruby
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
# require 'digest/sha1'

$stdout.sync = true
$log = Logger.new($stdout)
$log.level = Logger::INFO

if ARGV.length < 1
  puts "Usage: 77_bulk_extract_deltas_migration.rb (tenant|--all) [hostname:port] [--index-only]"
  puts "tenant: sha of tenant name"
  puts "--all: run against all tenant dbs"
  puts "--index-only:  Only add the required index.  Does not shard and pre-split the deltas collection."
  puts "hostname and port defaults to localhost:27017"
  exit 0
end

# dbname = Digest::SHA1.hexdigest ARGV[0]
dbname = ARGV[0]

host = 'localhost'
port = '27017'
INDEX_ONLY = nil

ARGV[1..ARGV.size].each do |arg|
  host, port = arg.split(":") if arg.include? ':'
  INDEX_ONLY = true if arg == "--index-only"
end

$client = Mongo::MongoClient.new(host, port)

def shard(collection)
  db = $client['admin']
  begin
    db.command({:shardCollection => collection, :key => {'_id' => 1}})
  rescue Exception => e
    if e.message.include? "already sharded"
      puts "Already sharded.  Skipping."
      return
    elsif e.message.include? "sharding not enabled for db"
      puts "Sharding is not enabled for database. Skipping."
      return
    else
      raise e
    end
  end
  mids = (1..15).map{|i| i.to_s(16)}
  mids.each{ |m| db.command({:split => collection, :middle => {'_id' => m}}) }
  shards = db.command({listShards: 1})['shards'].cycle
  (["$minkey"] + mids).each{ |m| 
    begin
      db.command({:moveChunk => collection, :find => {'_id' => m}, :to => shards.next['_id']})
    rescue Exception => e
      unless e.message.include? "that chunk is already on that shard" then
        puts "unable to move chunk with doc #{m} due to #{e.message}"
      end
    end
  }
  puts "Done"
end

def clean_deltas(db)
  $client[db]['deltas'].drop()
end

def migrate(db, name = nil)
  name ||= db
  if INDEX_ONLY.nil?
    puts "Indexing and sharding: #{db}"
  else
    puts "Indexing: #{db}"
  end
  clean_deltas(db)
  $client[db]['deltas'].create_index({"t" => Mongo::ASCENDING, "_id" => Mongo::ASCENDING})
  shard("#{db}.deltas") if INDEX_ONLY.nil?
end

if "--all" == dbname
  puts "Applying to all tenants..."
  tenants = []
  $client['sli']['tenant'].find({}).each do |tenant|
    tenants << tenant
  end
  tenants.each do |tenant|
    db = tenant['body']['dbName']
    name = tenant['body']['tenantId']
    migrate(db, name)
  end
else
  migrate(dbname)
end
