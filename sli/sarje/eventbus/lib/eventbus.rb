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

require "eventbus/version"
require "messaging_service"
require "jobscheduler"
require "oplogagent"

module Eventbus

    module MessagingBase 
        Topic_Subscribe = '/topic/agent'
        Topic_Heartbeat = '/queue/listener'
    end 

    class OplogListener
        include MessagingBase 

        def initialize
            config = {
            :node_name => 'listener',
            :publish_queue_name => Topic_Subscribe,
            :subscribe_queue_name => Topic_Heartbeat,
            :start_heartbeat => false
            }
            @messaging = MessagingService.new(config)
        end

        def subscribe(events)
            @messaging.publish(events)
        end 

        def receive(&block)
            # store  a reference to the subscriber that's returned by subscribe
            # this might not be necessary, since a thread internally should store it in a
            # closure, just to be on the safe side 
            @msg_receiver = @messaging.subscribe(&block)
        end 
    end
end
