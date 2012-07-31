=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"

require 'eventbus'
require 'test/unit'

class TestOpLogAgent < Test::Unit::TestCase
  def setup
  end

  def test_oplog_throttler
    throttler = Eventbus::OpLogThrottler.new
    doc1 = {"ns" => "initial"}
    doc2 = {"ns" => "darth.vader"}
    doc3 = {"ns" => "william.adama"}
    doc4 = {"ns" => "wario"}
    doc5 = {"ns" => "optimus.prime"}
    10.times do
      throttler.push(doc1)
    end
    evil_collections = ["darth.vader", "wario"]
    good_collections = ["optimus.prime", "power.rangers"]
    throttler.set_collection_filter(evil_collections)

    collection_received = Set.new
    throttler.run do |events|
      events["collections"].each do |collection|
        collection_received << collection
      end
    end
    sleep 10
    100.times do
      throttler.push(doc1)
      throttler.push(doc2)
      throttler.push(doc3)
      throttler.push(doc4)
      throttler.push(doc5)
    end
    sleep 10
    assert_equal(2, collection_received.size)
    assert_equal(2, (collection_received & evil_collections).size)

    throttler.set_collection_filter(good_collections)
    collection_received = Set.new
    sleep 10
    100.times do
      throttler.push(doc1)
      throttler.push(doc2)
      throttler.push(doc3)
      throttler.push(doc4)
      throttler.push(doc5)
    end
    sleep 10
    assert_equal(1, collection_received.size)
    assert_equal(1, (collection_received & good_collections).size)
  end

  #def test_oplog_agent
  #  Eventbus::OpLogAgent.new
  #
  #  def connect_to_mongo
  #    begin
  #      @conn = Mongo::Connection.new
  #      @db   = @conn['sample-db']
  #      @coll = @db['test']
  #      @coll.remove
  #    rescue
  #      puts "Cannot connect to mongo"
  #      sleep 1
  #      retry
  #    end
  #  end
  #
  #  Thread.new do
  #    connect_to_mongo
  #    100.times do |i|
  #      begin
  #        @coll.insert({'a' => i+1})
  #        sleep 1
  #      rescue Exception => e
  #        puts e
  #        connect_to_mongo
  #      end
  #    end
  #  end
  #
  #  sleep
  #end
end
