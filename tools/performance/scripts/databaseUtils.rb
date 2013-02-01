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
require 'pp'

def getTokenForUser(host, database, user)
  conn = Mongo::Connection.new(host)
  db = conn.db(database)
  coll = db.collection("userSession")

  token = nil
  newestSession = nil

  coll.find({"body.principal.externalId" => user}, :fields => ["body.appSession.token","metaData.updated"]).to_a.each do |rec|
    bodyMap = rec["body"]
    metaDataMap = rec["metaData"]
    
    thisToken = bodyMap["appSession"][0]["token"]
    thisUpdated = metaDataMap["updated"].to_s

#    puts "Found #{thisToken} from #{thisUpdated}"
    
    if (token.nil? or newestSession.nil? or thisUpdated > newestSession)
      token = thisToken
      newestSession = thisUpdated
    end
  end

  conn.close

  return token
end

def countRecords(host, database)
  conn = Mongo::Connection.new(host)
  db = conn.db(database)

  count = 0
  db.collection_names.each do |collectionName|
    if (!collectionName.start_with?('system.'))
      coll = db.collection(collectionName)
      count += coll.count
    end
  end

  conn.close

  return count
end

def countEach(host, database)
  conn = Mongo::Connection.new(host)
  db = conn.db(database)

  collectionCounts = {}

  db.collection_names.sort.each do |collectionName|
    coll = db.collection(collectionName)
    count = coll.count
    collectionCounts[collectionName] = count
  end

  conn.close

  return collectionCounts
end

def count(host, database, collectionName)
  conn = Mongo::Connection.new(host)
  db = conn.db(database)

  coll = db.collection(collectionName)
  conn.close

  return coll.count
end

def copyDatabase(host, sourceDb, targetDb)
  conn = Mongo::Connection.new(host)

  conn.drop_database(targetDb)
  conn.copy_database(sourceDb, targetDb)
  conn.close
end


#########################################################################
# Quick checks
#########################################################################

#########################################################################

#token = getTokenForUser("localhost","sli","rrogers")
#token = getTokenForUser("localhost","sli","linda.kim")
#puts token

#########################################################################

#numRecords = countRecords("localhost","sli")
#numRecords = countRecords("localhost","db_900k_stamped")
#puts numRecords

#########################################################################

#collCounts = countEach("localhost", "sli")
#staffCount = collCounts["staff"]
#indexCount = collCounts["system.indexes"]
#puts staffCount
#puts indexCount

#########################################################################

#count = count("localhost", "sli", "educationOrganization")
#puts count

#########################################################################
