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

require_relative 'helper.rb'
require_relative '../../logMarker.rb'
require 'test/unit'
require 'fileutils'

class LogMarkerTest < Test::Unit::TestCase
  
  def test_localLogMarker_markLog
    # mark log file
    marker = LogMarker::LocalLogMarker.new
    mark = "Marker Marker Marker"
    marker.markLog(@log, mark)
    
    # check that log file was marked
    found = false
    numLines = 0
    foundLine = 0
    logFile = File.open(@log, 'r')
    logFile.each do |line|
      numLines = numLines + 1
      if (line.start_with?(mark)) 
        puts "Found mark on line #{numLines}: #{line}"
        foundLine = numLines
        found = true
      end
    end
    logFile.close

    # assert that mark was found in the log
    assert(found, "Mark should have been found in the log file")
   
    # assert that mark was found on the last line of the log
    assert(foundLine == 101, "Mark should have been found on line 101")

    # assert that the number of lines in the log increased by one (didn't overwrite anything)
    assert(numLines == 101, "Should be 101 lines in the log file")

    # remove log file
    File.delete(@log)
  end

  def test_remoteLogMarker_markLog
    # place log on remote server
    props = getProps

    @host = props['host']
    @user = props['user']
    @password = props['password']

    propFile = getPropFile
    assert(!@host.nil? && !@host.empty?, "Host name (host) must be specified in #{propFile}")
    assert(!@user.nil? && !@user.empty?, "User name (user) must be specified in #{propFile}")
    assert(!@password.nil? && !@password.empty?, "Password (password) must be specified in #{propFile}")

    remoteLog = "/var/tmp/uploaded_log_#{@timestamp}.log"

    putFile(@log, remoteLog, @host, @user, @password)

    # mark remote log file
    mark = "Marker Marker Marker"
    marker = LogMarker::RemoteLogMarker.new(@host, @user, @password)
    marker.markLog(remoteLog, mark)

    # download log file
    localLog = "downloaded_log_#{@timestamp}.log"
    getFile(remoteLog, localLog, @host, @user, @password)

    # check that log file was marked
    found = false
    numLines = 0
    foundLine = 0
    logFile = File.open(localLog, 'r')
    logFile.each do |line|
      numLines = numLines + 1
      if (line.start_with?(mark)) 
        puts "Found mark on line #{numLines}: #{line}"
        foundLine = numLines
        found = true
      end
    end
    logFile.close

    # assert that mark was found in the log
    assert(found, "Mark should have been found in the log file")
   
    # assert that mark was found on the last line of the log
    assert(foundLine == 101, "Mark should have been found on line 101")

    # assert that the number of lines in the log increased by one (didn't overwrite anything)
    assert(numLines == 101, "Should be 101 lines in the log file")

    # delete remote log
    deleteFile(remoteLog, @host, @user, @password)
   
    # remove downloaded file
    File.delete(localLog)

    # remove log file
    File.delete(@log)
  end

  protected

  def setup
    @timestamp = getTimestamp
    @log = "test_#{@timestamp}.log"
    FileUtils.copy_file('../resources/unmarked_log.log', @log)
  end

end

