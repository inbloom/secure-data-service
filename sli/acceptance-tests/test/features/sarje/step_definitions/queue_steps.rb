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
require 'stomp'
require_relative '../../utils/sli_utils.rb'

Given /^I bind to queue "(.*?)"$/ do |queueName|
  @queue = "/queue/" + queueName
end

Given /^I send a terminate message to the queue$/ do 
  # ensure we're publishing to the end of the queue (after data is loaded into mongo)
  sleep 3
  publisher = Stomp::Client.new "", "", PropLoader.getProps['activemq_host'], PropLoader.getProps['activemq_port'] , true
  publisher.publish(@queue,"", {'amq-msg-type'=>'text', 'type'=>'terminate'})
  publisher.close 
end

Given /^I read the queue$/ do
  subscriber = Stomp::Client.new "", "", PropLoader.getProps['activemq_host'], PropLoader.getProps['activemq_port'] , true

  @msgs = []
  subscriber.subscribe(@queue, { :ack => :client }) do |msg|
    if (msg.headers['type'] == 'terminate')
      subscriber.acknowledge msg
      subscriber.close
    else
      @msgs << msg.body
      subscriber.acknowledge msg
    end
  end
  subscriber.join
end

Given /^I expect "(.*?)" messages to be dequeued$/ do |numMessages|
    assert(@msgs.length == numMessages.to_i, "Expected #{numMessages} Actual: #{@msgs.length}")
end

Given /^I expect "(.*?)" to be in one of the messages written on the queue$/ do |expectedContent|
  found = false
  @msgs.each do |msg|
    if (msg.include? expectedContent)
      found = true
      break
    end
  end
  assert(found, "#{expectedContent} is not found in dequeued messages")
end
