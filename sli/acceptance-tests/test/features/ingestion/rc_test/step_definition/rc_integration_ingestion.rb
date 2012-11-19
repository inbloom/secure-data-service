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
#require_relative '../../../cross_app_tests/step_definitions/rc_integration_samt.rb'
require_relative '../../features/step_definitions/ingestion_steps.rb'

UPLOAD_FILE_SCRIPT = File.expand_path("../opstools/ingestion_trigger/publish_file_uploaded.rb")

module NoLandingZone
    @hasNoLandingZone = false;
end

World(NoLandingZone)

############################################################
# TEST SETUP FUNCTIONS
############################################################

def processPayloadFile(file_name)
  path_name = file_name[0..-5]
  file_name = file_name.split('/')[-1] if file_name.include? '/'

  # copy everything into a new directory (to avoid touching git tracked files)
  path_delim = ""
  if path_name.include? '/'
    folders = path_name.split '/'
    if folders.size > 0
      folders[0...-1].each { |path| path_delim += path + '/'}
      path_name = folders[-1]
    end
  end
  zip_dir = @local_file_store_path + "temp-" + path_name + "/"
  p zip_dir
  if Dir.exists?(zip_dir)
    FileUtils.rm_r zip_dir
  end

  FileUtils.cp_r @local_file_store_path + path_delim + path_name, zip_dir

  ctl_template = nil
  Dir.foreach(zip_dir) do |file|
    if /.*.ctl$/.match file
    ctl_template = file
    end
  end

  # for each line in the ctl file, recompute the md5 hash
  new_ctl_file = File.open(zip_dir + ctl_template + "-tmp", "w")
  File.open(zip_dir + ctl_template, "r") do |ctl_file|
    ctl_file.each_line do |line|
      if line.chomp.length == 0
      next
      end
      entries = line.chomp.split ","
      if entries.length < 3
        puts "DEBUG:  less than 3 elements on the control file line.  Passing it through untouched: " + line
      new_ctl_file.puts line.chomp
      next
      end
      payload_file = entries[2]
      md5 = Digest::MD5.file(zip_dir + payload_file).hexdigest;
      if entries[3] != md5.to_s
        puts "MD5 mismatch.  Replacing MD5 digest for #{entries[2]} in file #{ctl_template}"
      end
      # swap out the md5 unless we encounter the special all zero md5 used for unhappy path tests
      entries[3] = md5 unless entries[3] == "00000000000000000000000000000000"
      new_ctl_file.puts entries.join ","
    end
  end
  new_ctl_file.close
  FileUtils.mv zip_dir + ctl_template + "-tmp", zip_dir + ctl_template

  runShellCommand("zip -j #{@local_file_store_path}#{file_name} #{zip_dir}/*")
  FileUtils.rm_r zip_dir

  file_name = @local_file_store_path + path_delim + file_name
  return file_name
end

############################################################
# REMOTE INGESTION FUNCTIONS
############################################################

def lzCopy(srcPath, destPath, lz_server_url = nil, lz_username = nil, lz_password = nil, lz_port_number = nil)
  if @local_lz
    FileUtils.cp srcPath, destPath
    puts "ruby #{UPLOAD_FILE_SCRIPT} STOR #{destPath}"
    runShellCommand("ruby #{UPLOAD_FILE_SCRIPT} STOR #{destPath}")
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
      next if File.directory?(file);
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

Given /^I am using default landing zone$/ do
  @landing_zone_path = "/"
end

Given /^I have a local configured landing zone for my tenant$/ do
  @local_lz = true

  host = PropLoader.getProps['ingestion_db']
  db_name = PropLoader.getProps['ingestion_database_name']
  conn = Mongo::Connection.new(host)
  db = conn.db(db_name)
  tenant_name = PropLoader.getProps['tenant']
  tenants = db.collection("tenant").find("body.tenantId" => tenant_name).to_a

  if tenants.empty?
    puts "#{tenant_name} tenantId not found in Mongo - skipping tenant database deletion"
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
    source_path = processPayloadFile arg1
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
  result = fileContainsMessage("", arg1, @landing_zone_path, @lz_url, @lz_username, @lz_password, @lz_port_number)
  assert result
end
