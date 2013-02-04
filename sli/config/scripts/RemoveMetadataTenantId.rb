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

require 'rubygems'
require 'mongo'

if ((ARGV[0] == "--help") || (ARGV[0] == "-h"))
  puts "Usage: ruby #{__FILE__} [mongoHost] [mongoPort]"
  exit 0
end

mongoHost = (ARGV[0].nil?) ? "localhost" : ARGV[0]
mongoPort = (ARGV[1].nil?) ? "27017" : ARGV[1]

begin
  # Connect to mongo on the specified host and port.
  connection = Mongo::Connection.new(mongoHost, mongoPort)
  puts "Connected to mongo host #{mongoHost}, port #{mongoPort}"

  # Loop through the databases.
  connection.database_names.each do |dbName|
    # Loop through the collections in the database.
    db = connection.db(dbName)
    count = 0
    db.collections.each do |collection|
      # Remove all metaData.tenantId fields from all records in the collection.
      count += collection.find({"metaData.tenantId" => {"$exists" => true}}).count
      collection.update({}, {"$unset" => {"metaData.tenantId" => 1}}, {:upsert => false, :multi => true})
    end
    puts "#{dbName}: removed #{count} records with metaData.tenantId"
  end

# Error!
rescue
  # Notify user and exit.
  $stderr.print "Mongo call failed: "
  raise

# Cleanup.
ensure
  # Close the mongo connection.
  connection.close unless connection.nil?

end
