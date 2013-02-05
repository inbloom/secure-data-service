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

def countEach
  db = getDatabase

  totalCount = 0
  db.collection_names.sort.each do |collectionName|
    if (!collectionName.start_with?('system.'))
      coll = db.collection(collectionName)
      count = coll.count
      totalCount += count
      puts "#{collectionName}: #{count}"
    end
  end

  puts "Total records: #{totalCount}"
end

def getDatabase
  host = ARGV[0]
  if (host.nil?)
    host = 'nxmongo3.slidev.org'
  end

  database = ARGV[1]
  if (database.nil?)
    database = 'sli'
  end

  conn = Mongo::Connection.new(host)
  return conn.db(database)
end


countEach

