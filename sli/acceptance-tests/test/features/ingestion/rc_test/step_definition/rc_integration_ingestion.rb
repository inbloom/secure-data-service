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


require 'rubygems'
require 'mongo'
require 'fileutils'
require 'socket'
require 'net/sftp'
require 'net/http'
require 'rest-client'

require_relative '../../../utils/sli_utils.rb'


############################################################
# REMOTE INGESTION FUNCTIONS
############################################################

def lzCopy(srcPath, destPath, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  if @local_lz
    FileUtils.cp srcPath, destPath
  else
    Net::SFTP.start(lz_server_url, lz_username, {:password => lz_password, :port => lz_port_number}) do |sftp|
        puts "attempting to remote copy " + srcPath + " to " + destPath
        sftp.upload!(srcPath, destPath)
    end
  end
end

def lzContainsFile(pattern, landingZone, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  puts "lzContainsFiles(" + pattern + " , " + landingZone + ")"

  if @local_lz
    !Dir["#{landingZone + pattern}"].empty?
  else
    Net::SFTP.start(lz_server_url, lz_username, {:password => lz_password, :port => lz_port_number}) do |sftp|
        sftp.dir.glob(landingZone, pattern) do |entry|
            return true
        end
    end
    return false
  end
end

def fileContainsMessage(prefix, message, landingZone, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  puts "fileContainsMessage prefix " + prefix + ", message " + message + ", landingZone " + landingZone

  if @local_lz
    Dir["#{landingZone + prefix + "*"}"].each do |file|
      content = File.read(file)
      if content.include?(message)
        return true
      end
    end
    return false
  else
    Net::SFTP.start(lz_server_url, lz_username, {:password => lz_password, :port => lz_port_number}) do |sftp|
        sftp.dir.glob(landingZone, prefix + "*") do |entry|
            entryPath = File.join(landingZone, entry.name)
            puts "found file " + entryPath

            #download file contents to a string
            file_contents = sftp.download!(entryPath)

            #check file contents for message
            if (file_contents.rindex(message) != nil)
                puts "Found message " + message
                return true
            end
        end
    end
    return false
  end
end

def clear_local_lz
  Dir.foreach(@landing_zone_path) do |file|
    if (/.*.log$/.match file) || (/.done$/.match file)
      FileUtils.rm_rf @landing_zone_path+file
    end
  end
end

########
# Cucumber steps
########

Given /^I am using local data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end

Given /^I am using default landing zone$/ do 
  @landing_zone_path = "/"
end

Given /^I have a local configured landing zone for my tenant$/ do
  @local_lz = true

  host = PropLoader.getProps['ingestion_db']
  db_name = PropLoader.getProps['ingestion_database_name']
  conn = Mongo::Connection.new(host)
  db = conn.db(db_name)
  tenants = db.collection("tenant").find("body.tenantId" => "RCTestTenant").to_a
  assert(!tenants.empty?, "Cannot find the tenant \"RCTestTenant\" in mongo")

  tenants[0]["body"]["landingZone"].each do |lz|
    if lz["educationOrganization"] == "RCTestEdOrg"
      @landing_zone_path = lz["path"]
      if (@landing_zone_path =~ /\/$/).nil?
        @landing_zone_path += "/"
      end
      break
    end
  end
  assert(!@landing_zone_path.nil?, "Cannot find configured lz path")

  clear_local_lz
end

Given /^I use the landingzone user name "(.*?)" and password "(.*?)" on landingzone server "(.*?)" on port "(.*?)"$/ do |arg1, arg2, arg3, arg4|
  @lz_username = arg1
  @lz_password = arg2
  @lz_url = arg3
  @lz_port_number = arg4.to_i
end

Given /^I drop the file "(.*?)" into the landingzone$/ do |arg1|
  source_path = @local_file_store_path + arg1
  dest_path = @landing_zone_path + arg1
  lzCopy(source_path, dest_path, @lz_url, @lz_username, @lz_password, @lz_port_number)
end

Given /^I check for the file "(.*?)" every "(.*?)" seconds for "(.*?)" seconds$/ do |arg1, arg2, arg3|
  waited = 0
  result = false
  target = arg1
  checkInterval = Integer(arg2)
  total = Integer(arg3)
  until ((waited > total) || result)
    # todo: what doesn't this 'puts' flush??
    puts "checking for file " + target.to_s + " waited " + waited.to_s
    result = lzContainsFile(target, @landing_zone_path, @lz_url, @lz_username, @lz_password, @lz_port_number)
    sleep checkInterval
    waited += checkInterval
  end
  assert (waited < total)
end

Then /^the landing zone should contain a file with the message "(.*?)"$/ do |arg1|
  result = fileContainsMessage("", arg1, @landing_zone_path, @lz_url, @lz_username, @lz_password, @lz_port_number)
  assert result
end
