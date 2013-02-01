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

require 'yaml'
require 'net/sftp'

# convenience functions

def getProps
  return YAML.load_file(getPropFile)
end

def getPropFile
  return '../resources/config.yml'
end

def getTimestamp
  time = Time.new
  return "#{time.year}#{time.month}#{time.day}_#{time.hour}#{time.min}#{time.sec}_#{time.usec}"
end

# Remote helper functions

def getFile(sourceFile, targetFile, host, user, password)
  Net::SFTP.start(@host, @user, :password => @password) do |sftp|
    puts "Downloading #{sourceFile} to #{targetFile} from #{host}"
    sftp.download!(sourceFile, targetFile)
  end
end

def putFile(sourceFile, targetFile, host, user, password)
  Net::SFTP.start(@host, @user, :password => @password) do |sftp|
    puts "Uploading #{sourceFile} to #{targetFile} on #{host}"
    sftp.upload!(sourceFile, targetFile)
  end
end

def deleteFile(file, host, user, password)
  Net::SFTP.start(@host, @user, :password => @password) do |sftp|
    puts "Deleting #{file} on #{host}"
    sftp.remove!(file)
  end
end

