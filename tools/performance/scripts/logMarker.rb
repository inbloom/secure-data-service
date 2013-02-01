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

require 'rubygems'
require 'net/ssh'
  
module LogMarker
  
  class LocalLogMarker
  
    def markLog(log, marker)
      logFile = File.open(log, 'a')
      logFile.write("#{marker}\n")
      logFile.close
    end
  
  end
  
  class RemoteLogMarker
  
    def initialize(host, user, password)
      @host = host
      @user = user
      @password = password
    end
  
    def markLog(remoteLog, marker)
      Net::SSH.start(@host, @user, :password => @password) do |ssh|
        puts "echo #{marker} >> #{remoteLog}"
        output = ssh.exec!("echo #{marker} >> #{remoteLog}")
        puts output if output != nil
      end
    end
  
  end

end

