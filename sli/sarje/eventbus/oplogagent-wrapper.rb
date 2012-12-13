#!/usr/bin/env ruby

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


Signal.trap("TERM") do
   exit
end
Signal.trap("QUIT") do
   exit
end
Signal.trap("INT") do
   exit
end

while true
   pid = Process.fork do
      #change value for correct path
      load '/opt/sarje/oplogagent_daemon.rb'
   end
   Process.waitpid(pid)
end
