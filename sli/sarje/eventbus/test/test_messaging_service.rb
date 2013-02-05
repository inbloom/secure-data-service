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
require 'logger'

class TestMessagingService < Test::Unit::TestCase
    TEST_TOPIC = "/topic/test_topic"
    TEST_QUEUE = "/queue/test_queue"

    def setup
       @logger = Logger.new(STDOUT)
       @logger.level = Logger::INFO
    end

    def test_topic
      config = {
        :node_name => 'eventsubscriber',
      }
      messaging = Eventbus::MessagingService.new(config, @logger)

      pub = messaging.get_publisher(TEST_TOPIC)
      sub_1 = messaging.get_subscriber(TEST_TOPIC)
      sub_2 = messaging.get_subscriber(TEST_TOPIC)

      sub_counter_1 = 0
      sub_1.handle_message do |msg| 
        sub_counter_1 += 1
      end 

      sub_counter_2 = 0
      sub_2.handle_message do |msg| 
        sub_counter_2 += 1
      end 

      n_messages = 10 

      sleep(3)
      n_messages.times do 
        msg = {
          "timestamp" => Time.now.to_i
        }
        pub.publish(msg)
        sleep(1)
      end 
      sleep(3)

      assert_equal n_messages, sub_counter_1
      assert_equal n_messages, sub_counter_2
    end

    def xxxtest_queue
      config = {
        :node_name => 'eventsubscriber',
      }
      messaging = Eventbus::MessagingService.new(config, @logger)
      pub = messaging.get_publisher(TEST_QUEUE)
      sub_1 = messaging.get_subscriber(TEST_QUEUE)
      sub_2 = messaging.get_subscriber(TEST_QUEUE)

      sub_counter_1 = 0
      sub_1.handle_message do |msg| 
        sub_counter_1 += 1
      end 

      sub_counter_2 = 0
      sub_2.handle_message do |msg| 
        sub_counter_2 += 1 
      end 

      sleep(3)
      n_messages = 10 
      counter = 0 
      n_messages.times do 
        counter += 1
        msg = {
          "count" => counter, 
          "timestamp" => Time.now.to_i
        }
        pub.publish(msg)
        sleep(0.5)
      end 

      sleep(3)
      assert_equal (sub_counter_1 + sub_counter_2),  n_messages

    end
end
