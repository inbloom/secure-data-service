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
require 'digest/sha1'

$stdout.sync = true
$log = Logger.new($stdout)
$log.level = Logger::INFO

if ARGV.length < 1
  puts "Usage: setupBEDeltas.rb tenant [hostname:port]"
  puts "defaults to localhost:27017"
  exit 0
end

dbname = Digest::SHA1.hexdigest ARGV[0]
host, port = (ARGV[1] or "localhost:27017").split(":")

$client = Mongo::MongoClient.new(host, port)

def shard(collection)
  db = $client['admin']
  db.command({shardCollection: collection, key: {_id: 1}})
  mids = (1..255).map{|i| i.to_s(16)}
  mids.each{ |m| db.command({split: collection, middle: {_id: m}}) }
  shards = db.command({listShards: 1})['shards'].cycle
  (["$minkey"] + mids).each{ |m| 
    begin
      db.command({moveChunk: collection, find: {_id: m}, to: shards.next['_id']})
    rescue Exception => e
      unless e.message.include? "that chunk is already on that shard" then
        puts "unable to move chunk with doc #{m} due to #{e.message}"
      end
    end
  }
end


$client[dbname]['deltas'].create_index("t")

shard("#{dbname}.deltas")
