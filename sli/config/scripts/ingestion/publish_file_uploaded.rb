#!/usr/bin/env ruby

#
# Copyright 2012 Shared Learning Collaborative, LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

require 'rubygems'
require 'stomp'

operation = ARGV[0]

if operation == 'STOR'

  path = ARGV[1]

  queue = 'ingestion.landingZone'

  client = Stomp::Client.new '', '', 'localhost', 61613, false

  client.publish queue, 'File upload completed.', { :persistent => true, :filePath => path }

  puts 'Sent message to queue: #{queue} file has been uploaded: #{path}'
  client.close

end
