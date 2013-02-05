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
require_relative '../../logDownloader.rb'
require 'test/unit'

class LogDownloaderTest < Test::Unit::TestCase
  
  def test_downloadLog
    sourceFile = '../resources/downloadable_file.log'
    targetFile = "/var/tmp/uploaded_#{@timestamp}.log"
    localFile = "downloaded_#{@timestamp}.log"

    expectedChecksum = Digest::MD5.new.hexdigest(File.read(sourceFile))
    
    putFile(sourceFile, targetFile, @host, @user, @password)
   
    downloader = LogDownloader.new(@host, @user, @password)
    downloader.downloadLog(targetFile, localFile)

    checksum = Digest::MD5.new.hexdigest(File.read(localFile))
    
    assert(File.exists?(localFile), "File was not downloaded: #{localFile}")
    assert(expectedChecksum == checksum, "File checksum doesn't match expected")

    deleteFile(targetFile, @host, @user, @password)
    File.delete(localFile)
  end
  
  protected

  def setup
    @timestamp = getTimestamp
    props = getProps

    @host = props['host']
    @user = props['user']
    @password = props['password']

    propFile = getPropFile
    assert(!@host.nil? && !@host.empty?, "Host name (host) must be specified in #{propFile}")
    assert(!@user.nil? && !@user.empty?, "User name (user) must be specified in #{propFile}")
    assert(!@password.nil? && !@password.empty?, "Password (password) must be specified in #{propFile}")
  end

end

