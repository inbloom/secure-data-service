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
require 'mongo'
require 'fileutils'
require 'socket'
require 'net/sftp'
require 'net/http'
require 'rest-client'

require_relative '../../utils/sli_utils.rb'
require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'

ACTIVEMQ_HOST = PropLoader.getProps['activemq_host']
UPLOAD_FILE_SCRIPT = File.expand_path("../opstools/ingestion_trigger/publish_file_uploaded.rb")

module NoLandingZone
  @hasNoLandingZone = false;
end

World(NoLandingZone)

Before do
  RUN_ON_RC = ENV['RUN_ON_RC'] ? true : false
end

############################################################
# TEST SETUP FUNCTIONS
############################################################

############################################################
# REMOTE INGESTION FUNCTIONS
############################################################

def lzCopy(srcPath, destPath, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  puts "srcPath = " + srcPath
  puts "destPath = " + destPath

  if @local_lz
    FileUtils.cp srcPath, destPath
    puts "ruby #{UPLOAD_FILE_SCRIPT} STOR #{destPath} #{ACTIVEMQ_HOST}"
    runShellCommand("ruby #{UPLOAD_FILE_SCRIPT} STOR #{destPath} #{ACTIVEMQ_HOST}")
  else
    begin
      puts "lz_server_url = " + lz_server_url
      puts "lz_username = " + lz_username
      puts "lz_password = " + lz_password 
      puts "lz_port_number = " + lz_port_number.to_s
    
      clearRemoteLz(@landing_zone_path, lz_server_url, lz_username, lz_port_number, lz_password)
      remoteLzCopy(srcPath, destPath, lz_server_url, lz_username, lz_port_number, lz_password)
    rescue Exception => e
      e.backtrace.inspect
    end
  end
end

def lzContainsFile(pattern, landingZone, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  puts "lzContainsFiles(" + pattern + " , " + landingZone + ")"
  puts "pattern = " + pattern
  puts "landingZone = " + landingZone

  if @local_lz
    !Dir["#{landingZone + pattern}"].empty?
  else
    puts "lz_server_url = " + lz_server_url
    puts "lz_username = " + lz_username
    puts "lz_password = " + lz_password
    puts "lz_port_number = " + lz_port_number.to_s
    return remoteLzContainsFile(pattern, landingZone, lz_server_url, lz_username, lz_port_number, lz_password)
  end
end

def fileContainsMessage(prefix, message, landingZone, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  puts "fileContainsMessage prefix " + prefix + ", message " + message + ", landingZone " + landingZone
  puts "prefix = " + prefix
  puts "message = " + message
  puts "landingZone = " + landingZone

  if @local_lz
    Dir["#{landingZone + prefix + "*"}"].each do |file|
      next if File.directory?(file);
      content = File.read(file)
      if content.include?(message)
        return true
      end
    end
    return false
  else
    puts "lz_server_url = " + lz_server_url
    puts "lz_username = " + lz_username
    puts "lz_password = " + lz_password
    puts "lz_port_number = " + lz_port_number.to_s
    return remoteFileContainsMessage(prefix, message, landingZone, lz_server_url, lz_username, lz_port_number, lz_password)
  end
end

def clear_local_lz
  Dir.foreach(@landing_zone_path) do |file|
    if (/.*.log$/.match file) || (/.done$/.match file)
      FileUtils.rm_rf @landing_zone_path+file
    end
  end
end

def clear_remote_lz(sftp)
   sftp.dir.foreach(@landing_zone_path) do |entry|
     next if entry.name == '.' or entry.name == '..'
     entryPath = File.join(@landing_zone_path, entry.name)
     if !sftp.stat!(entryPath).directory?
       sftp.remove!(entryPath)
     end
   end
end


########
# Cucumber steps
########

Given /^I am using default landing zone$/ do
  @landing_zone_path = "/"
end

Given /^I have a local configured landing zone for my tenant$/ do
  @local_lz = true

  host = PropLoader.getProps['ingestion_db']
  port = PropLoader.getProps['ingestion_db_port']
  db_name = PropLoader.getProps['ingestion_database_name']
  conn = Mongo::Connection.new(host, port)
  db = conn.db(db_name)

  if (@mode == "SANDBOX")
    @tenant_name = PropLoader.getProps['sandbox_tenant']
  else
    @tenant_name = PropLoader.getProps['tenant']
  end

  tenants = db.collection("tenant").find("body.tenantId" => @tenant_name).to_a

  if tenants.empty?
    puts "#{@tenant_name} tenantId not found in Mongo - skipping tenant database deletion"
  else
    edorg = PropLoader.getProps['edorg']
    tenants[0]["body"]["landingZone"].each do |lz|
      if lz["educationOrganization"] == edorg
        @landing_zone_path = lz["path"]
        if (@landing_zone_path =~ /\/$/).nil?
          @landing_zone_path += "/"
        end
        break
      end
    end
    @ingestion_db_name = tenants[0]['body']['dbName']
  end
  if @landing_zone_path.nil?
    puts "Could not retrieve landing zone path"
    @hasNoLandingZone = true
  else
    clear_local_lz
  end
end

Given /^I use the landingzone user name "([^"]*)" and password "([^"]*)" on landingzone server "([^"]*)" on port "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  @lz_username = arg1
  @lz_password = arg2
  @lz_url = arg3
  @lz_port_number = arg4.to_i
end

Given /^I drop the file "(.*?)" into the landingzone$/ do |arg1|
  if !@hasNoLandingZone
    path_delim = ""
    source_path = @local_file_store_path + path_delim + processPayloadFile(arg1) 
    dest_path = @landing_zone_path + arg1
    lzCopy(source_path, dest_path, @lz_url, @lz_username, @lz_password, @lz_port_number)
  end
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
  assert fileContainsMessage("job", arg1, @landing_zone_path, @lz_url, @lz_username, @lz_password, @lz_port_number)
end

Given /^a landing zone$/ do
  if RUN_ON_RC
    steps %Q{
        Given I am using local data store
        And I am using default landing zone
    }
    if (@mode == "SANDBOX")
      steps %Q{
          And I am using the tenant "<SANDBOX_TENANT>"
          And I use the landingzone user name "<DEVELOPER_SB_EMAIL>" and password "<DEVELOPER_SB_EMAIL_PASS>" on landingzone server "<LANDINGZONE>" on port "<LANDINGZONE_PORT>"
      }
    else
      steps %Q{
          And I am using the tenant "<TENANT>"
          And I use the landingzone user name "<PRIMARY_EMAIL>" and password "<PRIMARY_EMAIL_PASS>" on landingzone server "<LANDINGZONE>" on port "<LANDINGZONE_PORT>"
      }
    end
  else
    steps %Q{
        Given I am using local data store
        And I have a local configured landing zone for my tenant
    }
  end
end
