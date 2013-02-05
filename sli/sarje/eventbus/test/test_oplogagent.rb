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

testdir = File.dirname(__FILE__)
$LOAD_PATH << testdir + "/../lib"

require 'eventbus'
require 'test/unit'
require 'mongo'

class TestOpLogAgent < Test::Unit::TestCase

  def setup
  end

  def test_oplog_reader
    threads = []

    # create one config for each sharded replica set 
    configs = [
        { :mongo_host => "localhost:10001, localhost:10002, localhost:10003" },
        { :mongo_host => "localhost:10004, localhost:10005, localhost:10006" }
    ]

    oplog_queue = Queue.new
    threads = [] 
    configs.each do |conf|
        threads << Thread.new do
          oplog_reader = Eventbus::OpLogReader.new(conf)
          oplog_reader.handle_oplogs do |oplog_doc|
              # only record inserts 
              if oplog_doc["op"] == "i"
                  oplog_queue << oplog_doc 
              end
          end
        end
    end
    sleep 5   # wait for initial reading to clear

    conn = Mongo::Connection.new
    db   = conn['sample-db']
    coll = db['test']
    coll.remove

    oplog_count = 5
    threads << Thread.new do
      oplog_count.times do |i|
        # puts "inserting #{i}"
        coll.insert({'a' => i+1})
      end
    end
    sleep 5 # wait for oplog to queue up

    assert_equal(oplog_count, oplog_queue.size)

    threads.each do |thread|
      thread.kill
    end
  end

  def test_oplog_throttler
    threads = []

    config = {
      :collect_events_interval => 1
    }

    throttler = Eventbus::OpLogThrottler.new(config)
    oplog1 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"gummy.bear", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog2 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"darth.vader", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog3 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"philip.j.fry", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog4 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"waluigi", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog5 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"optimus.prime", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplogs = [oplog1, oplog2, oplog3, oplog4, oplog5]

    subscription_event1 = {
        "eventId" => "1",
        "triggers" => [{"op"=>"i", "ns"=>"darth.vader"}, {"op"=>"u", "ns"=>"philip.j.fry"}]
    }
    subscription_event2 = {
        "eventId" => "2",
        "triggers" => [{"op"=>"u", "ns"=>"waluigi"}, {"op"=>"i", "ns"=>"jon.snow"}]
    }
    subscription_event3 = {
        "eventId" => "3",
        "triggers" => [{"ns"=>"optimus.prime"}]
    }
    
    subscription_event4 = {
      "eventId" => "4",
      "triggers" => [{"op"=>"i", "ns"=>"darth.vader"}],
      "queue" => "search",
      "publishOplog" => true
    }
    
    subscription_event5 = {
      "eventId" => "5",
      "triggers" => [{"op"=>"i", "ns"=>"gummy.bear"}, {"op"=>"i", "ns"=> "waluigi"}],
      "queue" => "non_oplog_queue",
      "publishOplog" => true
    }

    # check that no event is received before oplog messages get sent
    event_received = Set.new
    threads << Thread.new do
      throttler.handle_events do |events|
          events.each do |event_per_queue|
            event_per_queue.each_value { |event_content|
              event_content.each do |event|
                event_received << event.to_s
              end
            }
          end
      end
    end
    sleep 2
    assert_equal(0, event_received.size)

    def push_oplogs(oplogs, throttler)
      5.times do
        oplogs.each do |oplog|
          throttler.push(oplog)
        end
      end
    end

    # check for subscription 1 & 2
    throttler.set_subscription_events([subscription_event1, subscription_event2])
    push_oplogs(oplogs, throttler)
    sleep 2
    assert_equal(1, event_received.size)
    assert(event_received.include?("1"))

    #check for subscription 1 & 3
    throttler.set_subscription_events([subscription_event1, subscription_event3])
    event_received = Set.new
    push_oplogs(oplogs, throttler)
    sleep 2
    assert_equal(2, event_received.size)
    assert(event_received.include?("1"))
    assert(event_received.include?("3"))

    #check for subscription 2 & 3
    throttler.set_subscription_events([subscription_event2, subscription_event3])
    event_received = Set.new
    push_oplogs(oplogs, throttler)
    sleep 2
    assert_equal(1, event_received.size)
    assert(event_received.include?("3"))

    #check for empty subscription
    throttler.set_subscription_events([])
    event_received = Set.new
    push_oplogs(oplogs, throttler)
    sleep 2
    assert_equal(0, event_received.size)

    #check for nil subscription
    throttler.set_subscription_events(nil)
    push_oplogs(oplogs, throttler)
    sleep 2
    assert_equal(0, event_received.size)
    
    #check for search queue specific subscription
    throttler.set_subscription_events([subscription_event4])
    event_received = Set.new
    push_oplogs(oplogs, throttler)
    sleep 2 
    assert_equal(1, event_received.size)
    assert(event_received.inspect.include?("darth.vader"))
    
    #check for other non-oplog queue and oplog queue in the same event
    throttler.set_subscription_events([subscription_event1, subscription_event5])
    event_received = Set.new
    push_oplogs(oplogs, throttler)
    sleep 2 
    assert_equal(3, event_received.size)
    assert(event_received.inspect.include?("gummy.bear"))
    assert(event_received.inspect.include?("waluigi"))
    assert(event_received.include?("1"))
   
    threads.each do |thread|
      thread.kill
    end
  end

  def test_regex_trigger
    config = {
        :collect_events_interval => 1
    }

    throttler = Eventbus::OpLogThrottler.new(config)
    oplog1 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"gummy.bear", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog2 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"gummy.worm", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog3 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"animal.bear", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplog4 = {"ts"=>BSON::Timestamp.new(1344000397,0), increment: 1, "h"=>3960979106658223967, "op"=>"i", "ns"=>"animal.otter", "o"=>{"_id"=>BSON::ObjectId('501bd18d2a63f618d2000002'), "a"=>2}}
    oplogs = [oplog1, oplog2, oplog3, oplog4]

    subscription_event1 = {
        "eventId" => "1",
        "triggers" => [{"op"=>"i", "ns"=>"[.]bear"}],
        "publishOplog" => true
    }
    throttler.set_subscription_events([subscription_event1])
    oplogs.each do |oplog|
      throttler.push oplog
    end

    result = []
    throttler.process_messages do |messages|
      messages.each do |message|
        result << message
      end
    end
    assert_equal(2, result.size)
    namespaces = []
    result.each do |message|
      namespaces << message["oplog"][0]["ns"]
    end
    assert(namespaces.include?("gummy.bear"))
    assert(namespaces.include?("animal.bear"))
  end
end
