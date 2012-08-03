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
require 'eventbus'

class TestMessagingService < Test::Unit::TestCase
    TEST_TOPIC = "/topic/test_topic"
    TEST_QUEUE = "/queue/test_queue"

    def test_topic
      config = {
        :node_name => 'eventsubscriber',
      }
      messaging = Eventbus::MessagingService.new(config)

      pub = messaging.get_publisher(TEST_TOPIC)
      sub_1 = messaging.get_subscriber(TEST_TOPIC)
      sub_2 = messaging.get_subscriber(TEST_TOPIC)

      sub_1.handle_message do |msg| 
        puts "Sub 1111: #{msg}"
      end 

      sub_2.handle_message do |msg| 
        puts "Sub 2222: #{msg}"
      end 

      sleep(3)
      10.times do 
        msg = {
          "timestamp" => Time.now.to_i
        }
        pub.publish(msg)
        sleep(1)
      end 

      sleep(5)
    end

    def test_queue
      config = {
        :node_name => 'eventsubscriber',
      }
      messaging = Eventbus::MessagingService.new(config)
      pub = messaging.get_publisher(TEST_QUEUE)
      sub_1 = messaging.get_subscriber(TEST_QUEUE)
      sub_2 = messaging.get_subscriber(TEST_QUEUE)

      sub_1.handle_message do |msg| 
        puts "Sub 1111: #{msg}"
      end 

      sub_2.handle_message do |msg| 
        puts "Sub 2222: #{msg}"
      end 

      sleep(3)
      counter = 0 
      10.times do 
        counter += 1
        msg = {
          "count" => counter, 
          "timestamp" => Time.now.to_i
        }
        pub.publish(msg)
        sleep(1)
      end 

      sleep(5)

    end
end