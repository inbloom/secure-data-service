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
require 'net/sftp'
require 'net/ssh'

module LogReducer  

  class LocalLogReducer

    #################################################################
    # TODO - This should be done without all of the system commands #
    #################################################################
    def reduceLog(log, reducedLog, marker, pattern)
      logFile = File.open(log, 'r')
      reducedLogFile = File.open(reducedLog, 'w')

      puts "wc -l #{log} | awk '{print $1}'"
      totalLines = `wc -l #{log} | awk '{print $1}'`
      totalLines.chomp!
      puts "#{log} has #{totalLines} lines"
    
      puts "grep -n \"#{marker}\" #{log} | awk -F: '{print $1}' | tail -1"
      ln = `grep -n \"#{marker}\" #{log} | awk -F: '{print $1}' | tail -1`
      ln.chomp!
      puts "#{marker} is on line #{ln}"
    
      tailLines = totalLines.to_i - ln.to_i + 1
    
      puts "tail -#{tailLines} #{log} | grep -P \"#{pattern}\" > #{reducedLog}"
      output = `tail -#{tailLines} #{log} | grep -P \"#{pattern}\" > #{reducedLog}`
      puts output if !output.nil? && !output.empty?

      logFile.close
      reducedLogFile.close
    end

  end

  class RemoteLogReducer
    
    def initialize(host, user, password)
      @host = host
      @user = user
      @password = password
    end

    def reduceLog(remoteLog, reducedLog, marker, pattern)
      Net::SSH.start(@host, @user, :password => @password) do |ssh|
        puts "wc -l #{remoteLog} | awk '{print $1}'"
        totalLines = ssh.exec!("wc -l #{remoteLog} | awk '{print $1}'")
        totalLines.chomp!
        puts "#{remoteLog} has #{totalLines} lines"
    
        puts "grep -n \"#{marker}\" #{remoteLog} | awk -F: '{print $1}' | tail -1"
        ln = ssh.exec!("grep -n \"#{marker}\" #{remoteLog} | awk -F: '{print $1}' | tail -1")
        ln.chomp!
        puts "#{marker} is on line #{ln}"
    
        tailLines = totalLines.to_i - ln.to_i + 1
    
        puts "tail -#{tailLines} #{remoteLog} | grep -P \"#{pattern}\" > #{reducedLog}"
        output = ssh.exec!("tail -#{tailLines} #{remoteLog} | grep -P \"#{pattern}\" > #{reducedLog}")
        puts output if !output.nil? && !output.empty?
      end
    end
    
  end

end  
