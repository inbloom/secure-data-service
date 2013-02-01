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


#!/usr/bin/env ruby 

require 'rubygems'
require 'mongo'
require 'fileutils'
require 'socket'
require 'net/sftp'

@SUPPORTED_ACTIONS = ["1", "2", "3", "4"]


#Hosts - can we make this default?
# nxmongo10.slidev.org:27017:sli:all,nxmongo11.slidev.org:27017:sli:all,nxmongo12.slidev.org:27017:sli:all,nxmongo13.slidev.org:27017:sli:all,nxmongo14.slidev.org:27017:sli:all,nxmongo15.slidev.org:27017:sli:all,nxmongo16.slidev.org:27017:sli:all,nxmongo17.slidev.org:27017:sli:all,nxmongo18.slidev.org:27017:sli:all,nxmongo19.slidev.org:27017:sli:all,nxmongo20.slidev.org:27017:sli:all,nxmongo21.slidev.org:27017:sli:all

# TODO - need to handle primaries vs secondaries.  Mongo will often promote a different node
# nxmongo10.slidev.org:27017:sli,nxmongo11.slidev.org:27017:sli,nxmongo12.slidev.org:27017:sli,nxmongo13.slidev.org:27017:sli,nxmongo14.slidev.org:27017:sli,nxmongo15.slidev.org:27017:sli,nxmongo16.slidev.org:27017:sli,nxmongo17.slidev.org:27017:sli,nxmongo18.slidev.org:27017:sli,nxmongo19.slidev.org:27017:sli,nxmongo20.slidev.org:27017:sli,nxmongo21.slidev.org:27017:sli

#nxmongo6.slidev.org:27017:is:all,nxmongo7.slidev.org:27017:is:all, nxmongo8.slidev.org:27017:is:all,nxmongo9.slidev.org:27017:is:all

def clearProfiles(serverConfigurations)
  profileColSize = 1024000000
  
  serverConfigurations.each do |sConf|
      temp = sConf.split(":")
      if (temp.length == 3 || temp.length == 4)
        puts "Clearning system.profile for: " + temp[0].to_s + ":" + temp[1].to_s + " - " + temp[2].to_s
        
        connection = Mongo::Connection.new(temp[0].to_s, temp[1].to_s)
        db = connection.db(temp[2].to_s)
        
        existingProfiling = db.profiling_level.to_s
        puts "Original profiling level = " + existingProfiling
        
        db.profiling_level = :off
        profileColl = db.drop_collection('system.profile')
        coll = db.create_collection("system.profile",:capped => true, :size=>profileColSize, :max=>profileColSize)
        
        if existingProfiling == "all"
          db.profiling_level = :all
        elsif existingProfiling == "slow_only"
          db.profiling_level = :slow_only
        else
          db.profiling_level = :off
        end
        
      else
        puts "Configuration " + sConf + " is not valid..."
      end
    end
end

def setProfilingLevel(serverConfigurations)
  serverConfigurations.each do |sConf|
      temp = sConf.split(":")
      if (temp.length == 4)
        puts "Setting Profiling levels for: " + temp[0].to_s + ":" + temp[1].to_s + " - " + temp[2].to_s + " - " + temp[3]
        
        connection = Mongo::Connection.new(temp[0].to_s, temp[1].to_s)
        db = connection.db(temp[2].to_s)
        
        existingProfiling = db.profiling_level.to_s
        puts "Original profiling level = " + existingProfiling
        
        if temp[3] == "all"
          db.profiling_level = :all
        elsif temp[3] == "slow_only"
          db.profiling_level = :slow_only
        else
          db.profiling_level = :off
        end
        
        existingProfiling = db.profiling_level.to_s
        puts "Profiling level is set to = " + existingProfiling
        
      else
        puts "Configuration " + sConf + " is not valid!"
      end
    end
end

def exportProfiling(serverConfigurations)
  serverConfigurations.each do |sConf|
      temp = sConf.split(":")
      if (temp.length == 3)
        puts "Exporting system.profile collection for: " + temp[0].to_s + ":" + temp[1].to_s + " - " + temp[2].to_s
        
        connection = Mongo::Connection.new(temp[0].to_s, temp[1].to_s)
        db = connection.db(temp[2].to_s)

        `#{"mongodump --host " + temp[0] + " --db " + temp[2].to_s + " --collection system.profile"}`
        `#{"bsondump dump/sli/system.profile.bson > dump/sli/system.profile.json"}`
        puts "Exported system.properties to dump/sli/system.properties.json file\n\n"
        
        puts "Parsing and reviewing system profile"
        `#{"python parse-profile-dump.py dump/sli/system.profile.json ../indexes/sli_indexes.js"}`
                
      else
        puts "Configuration " + sConf + " is not valid."
      end
    end
end

def setAndClearProfile(mode, arguments)
  done = "false"
  
  if mode == "all"
    puts "\n2. database system.profile collection clearing + profiling level setup"
    puts "Select server+db on which to clear system.profile collection and set profiling level.  Expected syntax: host1:port1:dbName2:profilingLevel1,host2:port2:dbName2:profilingLevel2,...,hostN:portN:dbNameN:profilingLevelN\n\n"
  elsif mode == "clear"
    puts "\n2. system.profile collection clearing"
    puts "Select server+db on which to clear system.profile collection.  Expected syntax: host1:port1:dbName2,host2:port2:dbName2,...,hostN:portN:dbNameN\n\n"
  elsif mode == "export"
    puts "\n2. system.profile collection export and analysis"
    puts "Select server+db on which to export system.profile collection.  Expected syntax: host1:port1:dbName2,host2:port2:dbName2,...,hostN:portN:dbNameN\n\n"
  else
    puts "\n2. database profiling level setup"
    puts "Select server+db on which to clear system.profile collection.  Expected syntax: host1:port1:dbName2:profilingLevel1,host2:port2:dbName2:profilingLevel2,...,hostN:portN:dbNameN:profilingLevelN\n\n"
  end
  
  until done == "true"
    
    if arguments == ""
      puts "Please enter desired configurations"
      input = gets.chomp
    else
      input = arguments
    end
    
    confValid = "true"
        
    puts "You have selected [" + input + "] which will clear "
    serverConfigurations = input.split(",")
    serverConfigurations.each do |sConf|
      temp = sConf.split(":")
      if (temp.length == 4 && (mode == "all" || mode == "set"))
        puts " -- " + temp[0].to_s + " : " + temp[1].to_s + " - " + temp[2].to_s + " - " + temp[3].to_s
      elsif (temp.length == 3 && (mode == "clear" || mode == "export"))
        puts " -- " + temp[0].to_s + " : " + temp[1].to_s + " - " + temp[2].to_s
      else
        puts "Configuration " + sConf + " is not valid"
        confValid = "false"
      end
    end
    
    if confValid == "false" && arguments != ""
      exit
    end
    
    if confValid == "true"
      if arguments == ""
        puts "Is this correct (Y/n)"
        confirmation = gets.chomp
      else
        confirmation = "Y"
      end
      
      if confirmation == "Y"
        if mode == "all"
          # clear + set
          clearProfiles(serverConfigurations)
          setProfilingLevel(serverConfigurations)
        elsif mode == "clear"
          # clear
          clearProfiles(serverConfigurations)
        elsif mode == "set"
          #set
          setProfilingLevel(serverConfigurations)
        elsif mode == "export"
          #export
          exportProfiling(serverConfigurations)
        else
          puts "NOT SUPPORTED OPERATION."
        end
        
        done = "true"
      end
    end
  end
end

def getActionFromUser()
  action = ""
  done = "false"
  
  puts "\n1. Select operation"
  puts "Configured operations: 1: Clear system.profile, 2: Set profiling level, 3: Clear+set, 4: export and review system.profile\n\n"
    
  until done == "true"
    puts "Please enter desired operation"
    action = gets.chomp
    if @SUPPORTED_ACTIONS.include?(action)
      done = "true"
    else
      puts "Invalid operation selected.  Supported Operations are " + @SUPPORTED_ACTIONS.to_s
    end
  end
  
  return action
end

def startMongoTools()
  puts "Mongo Profiling Tools"
  
  puts "Input Args = " + ARGV.to_s
  
  if ARGV.count<1
    selectedAction = getActionFromUser()
  else
    selectedAction = ARGV[0]
  end

  puts "Selected action = " + selectedAction
  
  inputArgs = "";
  if ARGV.count == 2
    inputArgs = ARGV[1]
  end
  
  case selectedAction
  when "1"
    setAndClearProfile("clear", inputArgs)
  when "2"
    setAndClearProfile("set", inputArgs)
  when "4"
    setAndClearProfile("export", inputArgs)
  when "3"
    setAndClearProfile("all", inputArgs)
  else
    puts "UNSUPPORTED OPERATION.  Exiting."
  end
   
end

#Start MongoTools
startMongoTools()


