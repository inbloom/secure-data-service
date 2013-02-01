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
  
class LogDownloader
  
  def initialize(host, user, password)
    @host = host
    @user = user
    @password = password
  end
  
  def downloadLog(remoteLog, localLog)
    Net::SFTP.start(@host, @user, :password => @password) do |sftp|
      puts "Downloading #{remoteLog} to #{localLog}"
      sftp.download!(remoteLog, localLog)
    end
  end
  
end

