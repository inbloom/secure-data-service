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
require_relative '../../logReducer.rb'
require 'test/unit'
require 'fileutils'

class LogReducerTest < Test::Unit::TestCase
  
  def test_localLogReducer_reduceLog
    # reduce log file
    reducer = LogReducer::LocalLogReducer.new
    reducedLog = "reduced_#{@timestamp}.log"
   
    # match any line after the last appearance of the marker with a "data: 3" in it
    pattern = "data: 3"
    reducer.reduceLog(@log, reducedLog, @marker, pattern)
    
    numLines = 0
    logFile = File.open(reducedLog, 'r')
    logFile.each do |line|
      numLines = numLines + 1
      assert_not_nil(line.match(pattern), "Line #{numLines} did not match pattern #{pattern}: #{line}")
    end
    logFile.close
 
    # assert that the number of lines in the reduced log is 11 
    assert(numLines == 11, "Should be 11 lines in the reduced log file")

    # remove reduced log file
    File.delete(reducedLog)

    # remove log file
    File.delete(@log)
  end
  
  def test_remoteLogReducer_reduceLog
    # put log file on remote server
    props = getProps

    @host = props['host']
    @user = props['user']
    @password = props['password']

    propFile = getPropFile
    assert(!@host.nil? && !@host.empty?, "Host name (host) must be specified in #{propFile}")
    assert(!@user.nil? && !@user.empty?, "User name (user) must be specified in #{propFile}")
    assert(!@password.nil? && !@password.empty?, "Password (password) must be specified in #{propFile}")

    fullLog = "/var/tmp/full_#{@timestamp}.log"
    putFile(@log, fullLog, @host, @user, @password)

    # reduce log file
    reducer = LogReducer::RemoteLogReducer.new(@host, @user, @password)
    reducedLog = "/var/tmp/reduced_#{@timestamp}.log"
   
    # match any line after the last appearance of the marker with a "data: 3" in it
    pattern = "data: 3"
    reducer.reduceLog(fullLog, reducedLog, @marker, pattern)
    
    # download reduced log
    localLog = "downloaded_reduced_#{@timestamp}.log"
    getFile(reducedLog, localLog, @host, @user, @password)

    numLines = 0
    logFile = File.open(localLog, 'r')
    logFile.each do |line|
      numLines = numLines + 1
      assert_not_nil(line.match(pattern), "Line #{numLines} did not match pattern #{pattern}: #{line}")
    end
    logFile.close
 
    # assert that the number of lines in the reduced log is 11 
    assert(numLines == 11, "Should be 11 lines in the reduced log file")

    # remove remote log files
    deleteFile(fullLog, @host, @user, @password)
    deleteFile(reducedLog, @host, @user, @password)

    # remove reduced log file
    File.delete(localLog)

    # remove original log file
    File.delete(@log)
  end
  
  protected

  def setup
    @timestamp = getTimestamp
    @log = "test_#{@timestamp}.log"
    @marker = "Marker Marker Marker"
    FileUtils.copy_file('../resources/marked_log.log', @log)
  end

end

