#!/usr/bin/env ruby
require 'mongo'

DB = ARGV[0]

if (ARGV.size != 1)
  puts "ruby rmv-nindexes.rb <database>"
  puts ""
  puts "Removes indexes that are not {'_id''} or {'metaData.tenantId', '_id'} from mongo on localhost."
  exit
end

@conn = Mongo::Connection.new
@db = @conn[DB]

ID_KEY = Hash["_id" => 1.0]
SHARD_KEY = Hash["metaData.tenantId" => 1.0, "_id" => 1.0]

@db.collection_names.each do |coll_name|
  @coll = @db[coll_name]
  indexes = @coll.index_information
  indexes.keys.each do |index|
    crnt_index = indexes[index]["key"]
    if crnt_index != SHARD_KEY and crnt_index != ID_KEY
      print "Removing index: " + coll_name + " "
      puts crnt_index
      @coll.drop_index(index)
    end
  end
end
