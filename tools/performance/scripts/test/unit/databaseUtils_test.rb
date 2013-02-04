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

require_relative '../../databaseUtils.rb'
require 'test/unit'
require 'mongo'

class DatabaseUtilsTest < Test::Unit::TestCase

  HOST = 'localhost'
  DATABASE = 'apiperf_test'

  def test_getTokenForUser
    user = 'test_user'
    
    # add in three tokens for user
    conn = Mongo::Connection.new(HOST)
    db = conn.db(DATABASE)
    coll = db.collection("userSession")
    # TODO fix this to only remove records for test_user
    coll.remove

    for i in 0..2
      token = "token_#{i}"
      time = Time.new
      updatedTstamp = time.strftime("%Y-%m-%dT%H:%M:%S")
      doc = {
        "body" => {
          "principal" => {
            "externalId" => user
          },
          "appSession" => [{
            "token" => token
          }]
        },
        "metaData" => {
          "updated" => updatedTstamp
        }
      }
      coll.save(doc)
      sleep 1
    end

    # get newest token
    newestToken = getTokenForUser(HOST, DATABASE, user)

    # check that newest token is expected value
    assert(newestToken == "token_2", "Expected token_2, received #{newestToken}")
    
    coll.drop
    conn.close
  end

  def test_countRecords
    populateDatabase
    
    # have to sleep to get correct counts
    sleep 1
    count = countRecords(HOST, DATABASE)
    assert(count == 15, "Expected 15 total records, received #{count}")
  end

  def test_countEach
    populateDatabase

    # have to sleep to get correct counts
    sleep 1

    collectionCounts = countEach(HOST, DATABASE)
   
    # this function includes system.* collections
    assert(collectionCounts.size == 4, "Expected counts for four collections")
    assert_not_nil(collectionCounts['system.indexes'], "Expected to receive count for system.indexes")
    assert_not_nil(collectionCounts['coll0'], "Expected to receive count for col0")
    assert_not_nil(collectionCounts['coll1'], "Expected to receive count for col1")
    assert_not_nil(collectionCounts['coll2'], "Expected to receive count for col2")

    assert(collectionCounts['system.indexes'] == 3, "Expected three records in system.indexes")
    assert(collectionCounts['coll0'] == 5, "Expected five records in coll0, received #{collectionCounts['coll0']}")
    assert(collectionCounts['coll1'] == 5, "Expected five records in coll1, received #{collectionCounts['coll1']}")
    assert(collectionCounts['coll2'] == 5, "Expected five records in coll2,, received #{collectionCounts['coll2']}")
  end

  def test_count
    populateDatabase
    
    # have to sleep to get correct counts
    sleep 1

    collectionCount = count(HOST, DATABASE, 'system.indexes')
    assert(collectionCount == 3, "Expected three records in system.indexes, received #{collectionCount}")
    
    collectionCount = count(HOST, DATABASE, 'coll0')
    assert(collectionCount == 5, "Expected five records in coll0, received #{collectionCount}")
  end

  def test_copyDatabase
    populateDatabase

    # have to sleep to get correct counts
    sleep 1

    newDatabase = 'apiperf_test_new'
    conn = Mongo::Connection.new(HOST)
    conn.drop_database(newDatabase)
    sourceDb = conn.db(DATABASE)
    targetDb = conn.db(newDatabase)
    
    sourceStats = sourceDb.stats
    targetStats = targetDb.stats
  
    # ensure dbs don't match yet
    assert(sourceStats['collections'] != targetStats['collections'], "Source and target dbs have same number of collections, bad test")
    assert(sourceStats['objects'] != targetStats['objects'], "Source and target dbs have same number of collections, bad test")

    copyDatabase(HOST, DATABASE, newDatabase)

    sourceStats = sourceDb.stats
    targetStats = targetDb.stats

    assert(sourceStats['collections'] == targetStats['collections'], "Source and target dbs have different number of collections")
    assert(sourceStats['objects'] == targetStats['objects'], "Source and target dbs have different number of collections")
  end

  protected

  def populateDatabase
    conn = Mongo::Connection.new(HOST)
    # start with a clean database
    conn.drop_database(DATABASE)
    db = conn.db(DATABASE)

    # create three collections with five records each
    for i in 0..2
      collName = "coll#{i}"
      coll = db.collection(collName)
      for j in 0..4
        record = {
          "recordName" => "rec#{j}",
          "inCollection" => collName
        }
        coll.save(record)
      end
    end

    conn.close
  end

end
