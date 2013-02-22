=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0-

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
require 'rbconfig'

require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../odin/step_definitions/data_generation_steps'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

INGESTION_DB_NAME = PropLoader.getProps['ingestion_database_name']
CONFIG_DB_NAME = "config"
INGESTION_DB = PropLoader.getProps['ingestion_db']
INGESTION_DB_PORT = PropLoader.getProps['ingestion_db_port']
INGESTION_BATCHJOB_DB_NAME = PropLoader.getProps['ingestion_batchjob_database_name']
INGESTION_BATCHJOB_DB = PropLoader.getProps['ingestion_batchjob_db']
INGESTION_BATCHJOB_DB_PORT = PropLoader.getProps['ingestion_batchjob_db_port']
LZ_SERVER_URL = PropLoader.getProps['lz_server_url']
LZ_SFTP_PORT = PropLoader.getProps['lz_sftp_port']
INGESTION_SERVER_URL = PropLoader.getProps['ingestion_server_url']
INGESTION_MODE = PropLoader.getProps['ingestion_mode']
INGESTION_DESTINATION_DATA_STORE = PropLoader.getProps['ingestion_destination_data_store']
INGESTION_USERNAME = PropLoader.getProps['ingestion_username']
INGESTION_PASSWORD = PropLoader.getProps['ingestion_password']
INGESTION_REMOTE_LZ_PATH = PropLoader.getProps['ingestion_remote_lz_path']
INGESTION_HEALTHCHECK_URL = PropLoader.getProps['ingestion_healthcheck_url']
INGESTION_PROPERTIES_FILE = PropLoader.getProps['ingestion_properties_file']
INGESTION_RC_TENANT = PropLoader.getProps['ingestion_rc_tenant']
INGESTION_RC_EDORG = PropLoader.getProps['ingestion_rc_edorg']
INGESTION_TIMEOUT_OVERRIDE = PropLoader.getProps['ingestion_timeout_override_seconds']

ACTIVEMQ_HOST = PropLoader.getProps['activemq_host']

TENANT_COLLECTION = ["Midgar", "Hyrule", "Security", "Other", "", "TENANT", INGESTION_RC_TENANT]

INGESTION_LOGS_DIRECTORY = PropLoader.getProps['ingestion_log_directory']

UPLOAD_FILE_SCRIPT = File.expand_path("../opstools/ingestion_trigger/publish_file_uploaded.rb")

ERROR_REPORT_MISSING_STRING_PREFIX = "#?"
ERROR_REPORT_MISSING_STRING_SUFFIX = "?#"

############################################################
# STEPS: BEFORE
############################################################

Before do
  @ingestion_db_name = convertTenantIdToDbName('Midgar')
  @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
  @batchConn = Mongo::Connection.new(INGESTION_BATCHJOB_DB, INGESTION_BATCHJOB_DB_PORT)

  if (INGESTION_MODE != 'remote')
    @batchConn.drop_database(INGESTION_BATCHJOB_DB_NAME)
    ensureBatchJobIndexes(@batchConn)
    puts "Dropped " + INGESTION_BATCHJOB_DB_NAME + " database"
  else
    @tenant_conn = @conn.db(convertTenantIdToDbName(PropLoader.getProps['tenant']))
    @recordHash = @tenant_conn.collection('recordHash')
    @recordHash.remove()
    @tenant_conn = @conn.db(convertTenantIdToDbName(PropLoader.getProps['sandbox_tenant']))
    @recordHash = @tenant_conn.collection('recordHash')
    @recordHash.remove()
    puts "Dropped recordHash for remote testing tenants"
  end

  @tenant_conn = @conn.db(convertTenantIdToDbName('Midgar'))
  @recordHash = @tenant_conn.collection('recordHash')
  @recordHash.remove()
  @tenant_conn = @conn.db(convertTenantIdToDbName('Hyrule'))
  @recordHash = @tenant_conn.collection('recordHash')
  @recordHash.remove()

  @mdb = @conn.db(INGESTION_DB_NAME)
  @tenantColl = @mdb.collection('tenant')

  if (((INGESTION_RC_TENANT == "") || (INGESTION_RC_TENANT == nil)) && ((INGESTION_RC_EDORG == "") || (INGESTION_RC_EDORG == nil)))
    @ingestion_lz_key_override = nil
  else
    @ingestion_lz_key_override = INGESTION_RC_TENANT + "-" + INGESTION_RC_EDORG
  end

  if (INGESTION_MODE != 'remote')
    #remove all tenants other than Midgar and Hyrule
    @tenantColl.find.each do |row|
      if row['body'] == nil
        puts "removing record"
        @tenantColl.remove(row)
      else
        if row['body']['tenantId'] != 'Midgar' and row['body']['tenantId'] != 'Hyrule' and row['body']['tenantId'] != PropLoader.getProps['tenant'] and row['body']['tenantId'] != PropLoader.getProps['sandbox_tenant']
          puts "removing record"
          @tenantColl.remove(row)
        end
      end
    end
  else
    puts "Refusing to remove tenants from remote (possibly RC) db"
  end

  @ingestion_lz_identifer_map = {}
  @tenantColl.find.each do |row|
    @body = row['body']
    @tenantId = @body['tenantId']
    @landingZones = @body['landingZone'].to_a
    @landingZones.each do |lz|
      if lz['educationOrganization'] == nil
        puts 'No educationOrganization for landing zone, skipping. Tenant id = ' + @tenantId
        next
      end
      if lz['path'] == nil
        puts 'No path for landing zone, skipping. Tenant id = ' + @tenantId
        next
      end

      educationOrganization = lz['educationOrganization']
      path = lz['path']

      if path.rindex('/') != (path.length - 1)
        path = path+ '/'
      end

      identifier = @tenantId + '-' + educationOrganization

      #in remote trim the path to a relative user path rather than absolute path
      if INGESTION_MODE == 'remote'
        # if running against RC tenant and edorg, path will be root directory on sftp login
        if identifier == INGESTION_RC_TENANT + '-' + INGESTION_RC_EDORG
          path = "./"
        else
          path = path.gsub(INGESTION_REMOTE_LZ_PATH, "")
        end
      end


      puts identifier + " -> " + path
      @ingestion_lz_identifer_map[identifier] = path

      if !File.directory?(path) && INGESTION_MODE != 'remote'
        FileUtils.mkdir_p(path)
      end

    end
  end

  initializeTenants()
end

def ensureBatchJobIndexes(db_connection)

  @db = db_connection[INGESTION_BATCHJOB_DB_NAME]

  @collection = @db["error"]
  @collection.save({ 'batchJobId' => " " })
  @collection.ensure_index([['batchJobId', 1]])
  @collection.ensure_index([['resourceId', 1]])
  @collection.ensure_index([['batchJobId', 1], ['severity', 1]])
  @collection.remove({ 'batchJobId' => " "  })

  @collection = @db["newBatchJob"]
  @collection.save({ '_id' => " " })
  @collection.ensure_index([['_id', 1]])
  @collection.ensure_index([['_id', 1], ['stages.$.chunks.stageName', 1]])
  @collection.remove({ '_id' => " " })

  @collection = @db["batchJobStage"]
  @collection.save({ 'jobId' => " ", 'stageName' => " " })
  @collection.ensure_index([['jobId', 1], ['stageName', 1]])
  @collection.remove({ 'jobId' => " ", 'stageName' => " "  })

  @collection = @db["transformationLatch"]
  @collection.save({ '_id' => " " })
  @collection.ensure_index([['syncStage', 1], ['jobId', 1], ['recordType' , 1]] , :unique => true)
  @collection.remove({ '_id' => " " })

  @collection = @db["persistenceLatch"]
  @collection.save({ '_id' => " " })
  @collection.ensure_index([['syncStage', 1], ['jobId', 1], ['entities' , 1]] , :unique => true)
  @collection.remove({ '_id' => " " })

  @collection = @db["stagedEntities"]
  @collection.save({ '_id' => " " })
  @collection.ensure_index([['jobId', 1]] , :unique => true)
  @collection.remove({ '_id' => " " })

  @collection = @db["recordHash"]
  @collection.save({ '_id' => " " })
  @collection.ensure_index([['t', 1]])
  @collection.remove({ '_id' => " " })

  @collection = @db["fileEntryLatch"]
  @collection.save({'_id' => " "})
  @collection.ensure_index([['batchJobId', 1]], :unique => true)
  @collection.remove({'_id' => " "})
end

def initializeTenants()
  @lzs_to_remove  = Array.new

  defaultLz = @ingestion_lz_identifer_map['Midgar-Daybreak']
  if defaultLz == nil then
    puts "Default landing zone not defined"
    return
  end

  if defaultLz.rindex('/') == (defaultLz.length - 1)
    # remove last character (/)
    defaultLz = defaultLz[0, defaultLz.length - 1]
  end

  # remove last directory
  if INGESTION_MODE == 'remote'
    if defaultLz.rindex('/') != nil
      @topLevelLandingZone = defaultLz[0, defaultLz.rindex('/')] + '/'
    else
      @topLevelLandingZone = ""
    end
    @tenantTopLevelLandingZone = @topLevelLandingZone + "tenant/"

  elsif defaultLz.rindex('/') != nil
    @topLevelLandingZone = defaultLz[0, defaultLz.rindex('/')] + '/'
    @tenantTopLevelLandingZone = @topLevelLandingZone + "tenant/"

  elsif defaultLz.rindex('\\') != nil
    @topLevelLandingZone = defaultLz[0, defaultLz.rindex('\\')] + '\\'
    @tenantTopLevelLandingZone = @topLevelLandingZone + "tenant\\"

  end

  puts "Top level LZ is -> " + @topLevelLandingZone

  cleanTenants()

  if !File.directory?(@tenantTopLevelLandingZone)
    if INGESTION_MODE != 'remote'
      FileUtils.mkdir_p(@tenantTopLevelLandingZone)
      #else
      # createRemoteDirectory(@tenantTopLevelLandingZone)
    end
  end
end

def cleanTenants()
  disable_NOTABLESCAN()

  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.remove("type" => "tenantTest")

  @lzs_to_remove.each do |lz_key|
    tenant = lz_key
    edOrg = lz_key

    # split tenant from edOrg on hyphen
    if lz_key.index('-') != nil
      tenant = lz_key[0, lz_key.index('-')]
      edOrg = lz_key[lz_key.index('-') + 1, lz_key.length]
    end

    @tenantColl.find("body.tenantId" => tenant, "body.landingZone.educationOrganization" => edOrg).each do |row|
      @body = row['body']
      @landingZones = @body['landingZone'].to_a
      @landingZones.each do |lz|
        if lz['educationOrganization'] == edOrg
          @landingZones.delete(lz)
        end
      end
      @tenantColl.save(row)
    end
  end

  enable_NOTABLESCAN()
end

############################################################
# REMOTE INGESTION FUNCTIONS
############################################################

def remoteLzCopy(srcPath, destPath, lz_url = LZ_SERVER_URL, username = INGESTION_USERNAME, port = LZ_SFTP_PORT, password = INGESTION_PASSWORD)
  Net::SFTP.start(lz_url, username, :port => port, :password => password) do |sftp|
    puts "attempting to remote copy " + srcPath + " to " + destPath
    sftp.upload(srcPath, destPath)
  end
end

def clearRemoteLz(landingZone, lz_url = LZ_SERVER_URL, username = INGESTION_USERNAME, port = LZ_SFTP_PORT, password = INGESTION_PASSWORD)

  puts "clear landing zone " + landingZone

  Net::SFTP.start(lz_url, username, :port => port, :password => password) do |sftp|
    sftp.dir.foreach(landingZone) do |entry|
      next if entry.name == '.' or entry.name == '..'

      entryPath = File.join(landingZone, entry.name)

      if !sftp.stat!(entryPath).directory?
        sftp.remove!(entryPath)
      end
    end
  end
end

def remoteDirContainsFile(pattern, dir)
  return remoteLzContainsFile(pattern, dir)
end

def remoteLzContainsFile(pattern, landingZone, lz_url = LZ_SERVER_URL, username = INGESTION_USERNAME, port = LZ_SFTP_PORT, password = INGESTION_PASSWORD)
  puts "remoteLzContainsFiles(" + pattern + " , " + landingZone + ")"

  Net::SFTP.start(lz_url, username, :port => port, :password => password) do |sftp|
    sftp.dir.glob(landingZone, pattern) do |entry|
      return true
    end
  end
  return false
end

def remoteLzContainsFiles(pattern, targetNum , landingZone)
  puts "remoteLzContainsFiles(" + pattern + ", " + targetNum.to_s + " , " + landingZone + ")"

  count = 0
  Net::SFTP.start(LZ_SERVER_URL, INGESTION_USERNAME, :port => LZ_SFTP_PORT, :password => INGESTION_PASSWORD) do |sftp|
    sftp.dir.glob(landingZone, pattern) do |entry|
      count += 1
      if count >= targetNum
        return true
      end
    end
  end
  return false
end

def searchRemoteFileForEitherContentAfterTag(content1, content2, logTag, completeFileName)
  puts "searchRemoteFileForEitherContentAfterTag(#{content1}, #{content2}, #{logTag}, #{completeFileName}"
  results = ""

  Net::SSH.start(LZ_SERVER_URL, INGESTION_USERNAME, :password => INGESTION_PASSWORD) do |ssh|
    ssh.exec!("grep -P \"#{logTag}.*#{content1}|#{logTag}.*#{content2}\" #{completeFileName}") do |channel, stream, data|
      results << data
    end
    puts results
  end

  return (results != nil && results != "")
end

def remoteFileContainsMessage(prefix, message, landingZone, lz_url = LZ_SERVER_URL, username = INGESTION_USERNAME, port = LZ_SFTP_PORT, password = INGESTION_PASSWORD)
  found = false;
  puts "remoteFileContainsMessage prefix " + prefix + ", message " + message + ", landingZone " + landingZone
  Net::SFTP.start(lz_url, username, :port => port, :password => password) do |sftp|
    sftp.dir.glob(landingZone, prefix + "*") do |entry|
      next if entry.name == '.' or entry.name == '..'
      entryPath = File.join(landingZone, entry.name)
      puts "found file " + entryPath

      #download file contents to a string
      file_contents = sftp.download!(entryPath)

      #check file contents for message
      if (file_contents.rindex(message) != nil)
        puts "Found message " + message
        found = true
      end
    end
  end
  return found
end

def createRemoteDirectory(dirPath)
  puts "attempting to create dir: " + dirPath

  Net::SFTP.start(LZ_SERVER_URL, INGESTION_USERNAME, :port => LZ_SFTP_PORT, :password => INGESTION_PASSWORD) do |sftp|
    begin
      sftp.mkdir!(dirPath)
    rescue
      puts "directory exists"
    end
  end
end

############################################################
# STEPS: GIVEN
############################################################

Given /^I am using local data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end

Given /^I am using destination-local data store$/ do
  @local_file_store_path = INGESTION_DESTINATION_DATA_STORE
end

Given /^I am using odin data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../../../../../../tools/odin/generated/'
end

def lzFileRmWait(file, wait_time)
  intervalTime = 3 #seconds
  iters = (1.0*wait_time/intervalTime).ceil
  deleted = false
  iters.times do |i|
    puts "Attempting delete of " + file
    FileUtils.rm_rf file
    if File.exists? file
      puts "Retry delete " + file
      sleep(intervalTime)
    else
      puts "Deleted " + file
      deleted = true
      break
    end
  end
  if !deleted
    puts "Failed to delete file " + file
  end
end

Given /^I am using preconfigured Ingestion Landing Zone$/ do
  steps "Given I am using preconfigured Ingestion Landing Zone for \"Midgar-Daybreak\""
end

Given /^I am using preconfigured Ingestion Landing Zone for "([^"]*)"$/ do |lz_key|
  # if the lz_key is overridden from the command line, use the override value
  unless (@ingestion_lz_key_override == nil)
    lz_key = @ingestion_lz_key_override
  end

  lz = @ingestion_lz_identifer_map[lz_key]
  initializeLandingZone(lz)
  initializeTenantDatabase(lz_key)
end

Given /^I am using the tenant "([^"]*)"$/ do |tenantId|
  initializeTenantDatabase(tenantId)
end

def initializeTenantDatabase(lz_key)

  # split tenant from edOrg on hyphen, if necessary
  if lz_key.index('-') != nil
    lz_key = lz_key[0, lz_key.index('-')]
  end

  @ingestion_db_name = convertTenantIdToDbName(lz_key)
end

def initializeLandingZone(lz)
  unless lz.nil?

    if lz.rindex('/') == (lz.length - 1)
      @landing_zone_path = lz
    else
      @landing_zone_path = lz+ '/'
    end
  end

  @landing_zone_path = lz
  puts "Landing Zone = " + @landing_zone_path unless @landing_zone_path.nil?

  # clear out LZ before proceeding
  if (INGESTION_MODE == 'remote')
    clearRemoteLz(@landing_zone_path)
  else
    Dir.foreach(@landing_zone_path) do |file|
      if /.*.log$/.match file
#        FileUtils.rm_rf @landing_zone_path+file
        lzFileRmWait @landing_zone_path+file, 900
      end
      if /.done$/.match file
        FileUtils.rm_rf @landing_zone_path+file
      end
    end
  end
end

def processUnzippedPayloadFile(file_name)
  return file_name[0..-5]
end

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

  return file_name
end

def processZipWithFolder(file_name)
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
      if payload_file == "MissingXmlFile.xml"
        puts "DEBUG: An xml file in control file is missing .."
        new_ctl_file.puts entries.join ","
        next
      end
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

  runShellCommand("cd #{zip_dir} && zip -r #{@local_file_store_path}#{file_name} *")
  FileUtils.rm_r zip_dir

  return file_name
end

Then /^I post "(.*?)" control file for concurent processing$/ do |file_name|
  copyFilesInDir file_name
end


def copyFilesInDir(file_name)

  path_name = file_name[0..-5]
  puts "path_name = " + path_name

  file_name = file_name.split('/')[-1] if file_name.include? '/'
  puts "file_name = " + file_name

  @source_path = @local_file_store_path + path_name + "/" + file_name
  @destination_path = @landing_zone_path + file_name

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  FileUtils.cp @source_path, @destination_path

  src_dir = @local_file_store_path + path_name + "/"

  # copy files specified by each line in the ctl file, to landing zone
  File.open(src_dir + file_name, "r") do |ctl_file|
    ctl_file.each_line do |line|
      if line.chomp.length == 0
        next
      end
      entries = line.chomp.split ","
      if entries.length < 3
        puts "DEBUG:  less than 3 elements on the control file line.  Passing it through untouched: " + line
        next
      end
      payload_file = entries[2]
      puts "controlFileEntry: " + payload_file
      FileUtils.cp src_dir+"/"+payload_file , @landing_zone_path

      if payload_file == "MissingXmlFile.xml"
        puts "DEBUG: An xml file in control file is missing .."
        next
      end
    end
  end
end

#get the max number of errors or warnings to be written to error or warning log
#def getMaxErrorWarnCount
#    maxError = 0
#    maxWarning = 0
#    file=File.open(INGESTION_PROPERTIES_FILE,"r")
#    file.each_line do |line|
#       if (line.rindex('sli.ingestion.errorsCountPerInterchange'))
#          maxError = line[line.rindex('=')+1,line.length-1]
#       end
#       if(line.rindex('sli.ingestion.warningsCountPerInterchange'))
#          maxWarning = line[line.rindex('=')+1, line.length-1]
#       end
#    end
#    return Integer(maxError), Integer(maxWarning)
#end

#get the number of errors actually be written to error log
def getErrorCount
    @error_filename = ""
    @resource = ""
    resourceToErrorCount = Hash.new(0)
    Dir.foreach(@landing_zone_path) do |entry|
      if entry.match(/^error/)
        @error_filename = entry
        puts entry
        @resource = entry[entry.rindex('Interchange'), entry.rindex('.xml')]
        puts @resource
        file = File.open(@landing_zone_path+entry, "r")
        file.each_line do |line|
          if(line.rindex('ERROR'))
            resourceToErrorCount[@resource] += 1
          end
      end
    end
  end
  return resourceToErrorCount
end



#get the number of warnings actually be written to warning log
def getWarnCount
    @warn_filename = ""
    @resource = ""
    resourceToWarnCount = Hash.new(0)
    Dir.foreach(@landing_zone_path) do |entry|
    if entry.match(/^warn/)
      @warn_filename = entry
      puts entry
      @resource = entry[entry.rindex('Interchange'),entry.rindex('.xml')]    
      puts @resource
      file = File.open(@landing_zone_path+entry, "r")
      file.each_line do |line|
        if(line.rindex('WARN'))
          resourceToWarnCount[@resource] += 1
        end
      end
    end
  end
  return resourceToWarnCount
end

#check if the actually error count is less than the max error count
def verifyErrorCount(count)
  maxError = Integer(count)

  puts "maxError = "
  puts maxError
  resourceToErrorCount = Hash.new(0)
  resourceToErrorCount = getErrorCount

  resourceToErrorCount.keys.each do |k,v|
    if maxError >= resourceToErrorCount[k]
      assert(true, "Number of Errors written to error.log file is less than max number of Errors")
    else
      assert(false, "Number of Errors written to error.log file is more than max number of Errors")
    end
  end

end

#check if the actually warn count is less than the max warn count
def verifyWarnCount(count)
  maxWarn = Integer(count)

  puts "maxWarn = "
  puts maxWarn
  resourceToWarnCount = Hash.new(0)
  resourceToWarnCount = getWarnCount

  resourceToWarnCount.keys.each do |k,v|
    if maxWarn >= resourceToWarnCount[k]
      assert(true, "Number of Warnings written to warning.log file is less than max number of Warnings")
    else
      assert(false, "Number of Warnings written to warning.log file is more than max number of Warnings")
    end
  end
end


Given /^I should see the number of errors in error log is no more than the error count limitation (\d+)$/ do |count|
  verifyErrorCount(count)
end

Given /^I should see the number of warnings in warn log is no more than the warning count limitation (\d+)$/ do |count|
  verifyWarnCount(count)
end

Given /^I post "([^"]*)" unzipped file as the payload of the ingestion job$/ do |file_name|
  @source_dir_name = processUnzippedPayloadFile file_name
  @source_file_name = file_name
end

Given /^I post "([^"]*)" file as the payload of the ingestion job$/ do |file_name|
  @source_file_name = processPayloadFile file_name
end

Given /^I post "([^"]*)" zip file with folder as the payload of the ingestion job$/ do |file_name|
  @source_file_name = processZipWithFolder file_name
end

Given /^I post "([^"]*)" file as the payload of the ingestion job for "([^"]*)"$/ do |file_name, lz_key|
  if @file_lz_map == nil
    @file_lz_map = {}
  end
  if @source_file_lz_map == nil
    @source_file_lz_map = {}
  end
  @file_lz_map[lz_key] = file_name
  source_file = processPayloadFile file_name
  @source_file_lz_map[lz_key] = source_file
end

Given /^I post "([^"]*)" and "([^"]*)" files as the payload of two ingestion jobs$/ do |file_name1, file_name2|
  @source_file_name1 = processPayloadFile file_name1
  @source_file_name2 = processPayloadFile file_name2
end

Given /^I want to ingest locally provided data "([^"]*)" file as the payload of the ingestion job$/ do |file_path|
  @source_file_name = file_path
end

Given /^the following collections are empty in sli datastore:$/ do |table|
    disable_NOTABLESCAN()
    @db = @conn[INGESTION_DB_NAME]
    puts "Clearing out collections in db " + INGESTION_DB_NAME + " on " + INGESTION_DB_NAME
    @result = "true"
    table.hashes.map do |row|
            @entity_collection = @db[row["collectionName"]]
            @entity_collection.remove()           
            puts "There are #{@entity_collection.count} records in collection " + row["collectionName"] + "."            
            if @entity_collection.count.to_s != "0"
                @result = "false"
            end
    end
    assert(@result == "true", "Some collections were not cleared successfully.")
    enable_NOTABLESCAN()
end



Given /^the following collections are empty in datastore:$/ do |table|
  disable_NOTABLESCAN()
  @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)

  @db = @conn[@ingestion_db_name]

  puts "Clearing out collections in db " + @ingestion_db_name + " on " + INGESTION_DB

  @result = "true"

  table.hashes.map do |row|
    parent = subDocParent row["collectionName"]
    if parent
      @entity_collection = @db[parent]
      superDocs = @entity_collection.find()
      cleanupSubDoc(superDocs, row["collectionName"])
    else
      @entity_collection = @db[row["collectionName"]]
      @entity_collection.remove()

      puts "There are #{@entity_collection.count} records in collection " + row["collectionName"] + "."

      if @entity_collection.count.to_s != "0"
        @result = "false"
      end
    end
  end
  assert(@result == "true", "Some collections were not cleared successfully.")
  enable_NOTABLESCAN()
end

Given /^the following collections are empty in batch job datastore:$/ do |table|
  disable_NOTABLESCAN()
  @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db[row["collectionName"]]
    @entity_collection.remove()

    puts "There are #{@entity_collection.count} records in collection " + row["collectionName"] + "."

    if @entity_collection.count.to_s != "0"
      @result = "false"
    end
  end
  #ensureBatchJobIndexes(@batchConn)
  #assert(@result == "true", "Some collections were not cleared successfully.")
  exec 'mongo ingestion_batch_job ../config/indexes/ingestion_batch_job_indexes.js'
  enable_NOTABLESCAN()
end

When /^the tenant with tenantId "(.*?)" is locked$/ do |tenantId|
  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.update({"body.tenantId" => tenantId}, {"$set" => {"body.tenantIsReady" => false}})
end

Then /^the tenant with tenantId "(.*?)" is unlocked$/ do |tenantId|
  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.update({"body.tenantId" => tenantId}, {"$set" => {"body.tenantIsReady" => true}})
end

Then /^the tenantIsReady flag for the tenant "(.*?)" is reset$/ do |tenantId|
  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.update({"body.tenantId" => tenantId}, {"$unset" => {"body.tenantIsReady" => 1}})
end

Given /^I add a new tenant for "([^"]*)"$/ do |lz_key|
  disable_NOTABLESCAN()

  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.remove("body.tenantId" => lz_key)

  path = @tenantTopLevelLandingZone + 'tenant_' + rand(1048576).to_s

  absolutePath = path
  if INGESTION_MODE == 'remote'
    absolutePath = INGESTION_REMOTE_LZ_PATH + absolutePath
  end

  if INGESTION_MODE != 'remote'
    FileUtils.mkdir_p(path)
    FileUtils.chmod(0777, path)
    #else
    # createRemoteDirectory(path)
  end

  puts lz_key + " -> " + path

  ingestionServer = Socket.gethostname
  if INGESTION_MODE == 'remote'
    ingestionServer = INGESTION_SERVER_URL
    if ingestionServer.index('.') != nil
      ingestionServer = ingestionServer[0, ingestionServer.index('.')]
    end
  end


  tenant = lz_key
  edOrg = lz_key

  # split tenant from edOrg on hyphen
  if lz_key.index('-') != nil
    tenant = lz_key[0, lz_key.index('-')]
    edOrg = lz_key[lz_key.index('-') + 1, lz_key.length]
  end

  # set instance var to this value (used for future db connections)
  @ingestion_db_name = convertTenantIdToDbName(tenant)
  puts "setting ingestion_db_name to #{@ingestion_db_name}"

  # index the new tenant db
  dbName = convertTenantIdToDbName('Midgar')

  @body = {
      "tenantId" => tenant,
      "landingZone" => [
          {
              "educationOrganization" => edOrg,
              "ingestionServer" => ingestionServer,
              "path" => absolutePath
          }
      ]
  }

  @metaData = {}

  @newTenant = {
      "_id" => "tenantTest-id",
      "type" => "tenantTest",
      "body" => @body,
      "metaData" => @metaData
  }

  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.save(@newTenant)

  @ingestion_lz_identifer_map[lz_key] = path + '/'
  @lzs_to_remove.push(lz_key)

  enable_NOTABLESCAN()
end

Given /^I add a new landing zone for "([^"]*)"$/ do |lz_key|
  disable_NOTABLESCAN()

  tenant = lz_key
  edOrg = lz_key

  # split tenant from edOrg on hyphen
  if lz_key.index('-') > 0
    tenant = lz_key[0, lz_key.index('-')]
    edOrg = lz_key[lz_key.index('-') + 1, lz_key.length]
  end

  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')

  matches = @tenantColl.find("body.tenantId" => tenant, "body.landingZone.educationOrganization" => edOrg).to_a
  puts "Found " + matches.size.to_s + " existing records for " + lz_key

  assert(matches.size == 0, "Tenant already exists for " + lz_key)

  @existingTenant = @tenantColl.find_one("body.tenantId" => tenant)

  @id = @existingTenant['_id']
  @body = @existingTenant['body']

  @landingZones = @body['landingZone'].to_a

  path = @tenantTopLevelLandingZone + 'tenant_' + rand(1048576).to_s

  absolutePath = path
  if INGESTION_MODE == 'remote'
    absolutePath = INGESTION_REMOTE_LZ_PATH + absolutePath
  end

  if INGESTION_MODE != 'remote'
    FileUtils.mkdir_p(path)
    FileUtils.chmod(0777, path)
    #else
    # createRemoteDirectory(path)
  end

  puts lz_key + " -> " + path

  ingestionServer = Socket.gethostname
  if INGESTION_MODE == 'remote'
    ingestionServer = INGESTION_SERVER_URL
    if ingestionServer.index('.') != nil
      ingestionServer = ingestionServer[0, ingestionServer.index('.')]
    end
  end

  @newLandingZone = {
      "educationOrganization" => edOrg,
      "ingestionServer" => ingestionServer,
      "path" => absolutePath
  }

  @landingZones.push(@newLandingZone)
  @tenantColl.save(@existingTenant)
  @ingestion_lz_identifer_map[lz_key] = path + '/'
  @lzs_to_remove.push(lz_key)

  enable_NOTABLESCAN()
end

Given /^I add a new named landing zone for "([^"]*)"$/ do |lz_key|
  tenant = lz_key
  edOrg = lz_key

  # split tenant from edOrg on hyphen
  if lz_key.index('-') > 0
    tenant = lz_key[0, lz_key.index('-')]
    edOrg = lz_key[lz_key.index('-') + 1, lz_key.length]
  end

  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')

  matches = @tenantColl.find("body.tenantId" => tenant, "body.landingZone.educationOrganization" => edOrg).to_a
  puts "Found " + matches.size.to_s + " existing records for " + lz_key

  assert(matches.size == 0, "Tenant already exists for " + lz_key)

  @existingTenant = @tenantColl.find_one("body.tenantId" => tenant)

  @id = @existingTenant['_id']
  @body = @existingTenant['body']

  @landingZones = @body['landingZone'].to_a

  path = @tenantTopLevelLandingZone + lz_key

  absolutePath = path
  if INGESTION_MODE == 'remote'
    absolutePath = INGESTION_REMOTE_LZ_PATH + absolutePath
  end

  if INGESTION_MODE != 'remote'
    FileUtils.mkdir_p(path)
    FileUtils.chmod(0777, path)
    #else
    #createRemoteDirectory(path)
  end

  puts lz_key + " -> " + path

  ingestionServer = Socket.gethostname
  if INGESTION_MODE == 'remote'
    ingestionServer = INGESTION_SERVER_URL
    if ingestionServer.index('.') != nil
      ingestionServer = ingestionServer[0, ingestionServer.index('.')]
    end
  end

  @newLandingZone = {
      "educationOrganization" => edOrg,
      "ingestionServer" => ingestionServer,
      "path" => absolutePath
  }

  @landingZones.push(@newLandingZone)
  @tenantColl.save(@existingTenant)
  @ingestion_lz_identifer_map[lz_key] = path + '/'
  @lzs_to_remove.push(lz_key)
end

Given /^the tenant database for "([^"]*)" does not exist/ do |tenantToDrop|
  puts "Dropping database for:" + tenantToDrop
  @conn.drop_database(convertTenantIdToDbName(tenantToDrop))
end

Given /^the log directory contains "([^"]*)" file$/ do |logfile|
  if (INGESTION_MODE == 'remote')
    fileExist = remoteDirContainsFile(logfile, INGESTION_LOGS_DIRECTORY)
  else
    completeFileName = INGESTION_LOGS_DIRECTORY + '/' + logfile
    fileExist = File.exist? completeFileName
  end

  assert(fileExist == true, logfile + 'missing')
end

############################################################
# STEPS: WHEN
############################################################

When /^the landing zone is reinitialized$/ do
  initializeLandingZone(@landing_zone_path)
end

When /^"([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end

def completeBatchJob?(file)
  if /^job-#{@source_file_name}.*.log$/.match file
    if isCompleted? file
      return true
    else
      puts "job file still being written to"
    end
  end
  return false
end

def isCompleted?(file)
  lines = IO.readlines(@landing_zone_path + '/' + file)
  return lines.any?{|line| /^INFO  Processed [0-9]+ records.\n$/.match line}
rescue SystemCallError
  return false
end

def dirContainsBatchJobLog?(dir)
  Dir.foreach(dir) do |file|
    if completeBatchJob? file
      return true
    end
  end
  return false
end

def dirContainsBatchJobLogs?(dir, num)
  count = 0
  Dir.foreach(dir) do |file|
    if /^job-.*.log$/.match file
      count += 1
      if count >= num
        return true
      end
    end
  end
  return false
end

When /^I am willing to wait upto (\d+) seconds for ingestion to complete$/ do |limit|
  @maxTimeout = limit.to_i + INGESTION_TIMEOUT_OVERRIDE.to_i
  puts "Waited for #{INGESTION_TIMEOUT_OVERRIDE.to_i} seconds in addition to the timeout"
end

When /^a batch job log (has|has not) been created$/ do |has_or_has_not|
  should_has_log = (has_or_has_not == "has not")? false : true
  checkForBatchJobLog(@landing_zone_path, should_has_log) if !@hasNoLandingZone
end

When /^a batch job has completed successfully in the database$/ do
   disable_NOTABLESCAN()
   old_db = @db
   @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]
   @entity_collection = @db.collection("newBatchJob")
   intervalTime = 1
   @maxTimeout ? @maxTimeout : @maxTimeout = 600
   iters = (1.0*@maxTimeout/intervalTime).ceil
   found = false
     if (INGESTION_MODE == 'remote')
       iters.times do |i|
         @entity_count = @entity_collection.find({"status" => {"$in" => ["CompletedSuccessfully"]}}).count().to_s
         if @entity_count.to_s == "1"
           puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
           found = true
           break
         else
           @entity_count = @entity_collection.find({"status" => {"$in" => ["CompletedWithErrors"]}}).count().to_s
           if @entity_count.to_s == "1"
                assert(false, "Batch Job completed with errors")
           end
           sleep(intervalTime)
         end
       end
     else
       sleep(5) # waiting to check job completion removes race condition (windows-specific)
       iters.times do |i|
         @entity_count = @entity_collection.find({"status" => {"$in" => ["CompletedSuccessfully"]}}).count().to_s
         if @entity_count.to_s == "1"
           puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
           found = true
           break
         else
           @entity_count = @entity_collection.find({"status" => {"$in" => ["CompletedWithErrors"]}}).count().to_s
           if @entity_count.to_s == "1"
                assert(false, "Batch Job completed with errors")
           end
           sleep(intervalTime)
         end
       end
     end
     if found
       assert(true, "")
     else
       assert(false, "Either batch log was never created, or it took more than #{@maxTimeout} seconds")
     end
     @db = old_db
     enable_NOTABLESCAN()
   end

When /^a batch job for file "([^"]*)" is completed in database$/ do |batch_file|
  disable_NOTABLESCAN()

  old_db = @db
  @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]
  @entity_collection = @db.collection("newBatchJob")

  #db.newBatchJob.find({"stages" : {$elemMatch : {"chunks.0.stageName" : "JobReportingProcessor" }} }).count()

  intervalTime = 1 #seconds
  #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 600
  iters = (1.0*@maxTimeout/intervalTime).ceil
  found = false
  if (INGESTION_MODE == 'remote')
    iters.times do |i|
      @entity_count = @entity_collection.find({"resourceEntries.0.resourceId" => batch_file, "status" => {"$in" => ["CompletedSuccessfully", "CompletedWithErrors"]}}).count().to_s

      if @entity_count.to_s == "1"
        puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
        found = true
        break
      else
        sleep(intervalTime)
      end
    end
  else
    sleep(5) # waiting to check job completion removes race condition (windows-specific)
    iters.times do |i|

      @entity_count = @entity_collection.find({"resourceEntries.0.resourceId" => batch_file, "status" => {"$in" => ["CompletedSuccessfully", "CompletedWithErrors"]}}).count().to_s

      if @entity_count.to_s == "1"
        puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
        found = true
        break
      else
        sleep(intervalTime)
      end
    end
  end

  if found
    assert(true, "")
  else
    assert(false, "Batch log did not complete either successfully or with errors within #{@maxTimeout} seconds. Test has timed out. Please check ingestion.log for root cause.")
  end

  @db = old_db

  enable_NOTABLESCAN()
end

When /^two batch job logs have been created$/ do
  intervalTime = 3 #seconds
                   #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 240
  iters = (1.0*@maxTimeout/intervalTime).ceil
  found = false
  if (INGESTION_MODE == 'remote') # TODO this needs testing for remote
    iters.times do |i|
      if remoteLzContainsFiles("job-*.log", 2, @landing_zone_path)
        puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
        found = true
        break
      else
        sleep(intervalTime)
      end
    end
  else
    sleep(3) # waiting to poll job file removes race condition (windows-specific)
    iters.times do |i|
      if dirContainsBatchJobLogs? @landing_zone_path, 2
        puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
        found = true
        break
      else
        sleep(intervalTime)
      end
    end
  end

  if found
    sleep(5) # give JobReportingProcessor time to finish the job (increased to 5 for AWS)
    assert(true, "")
  else
    assert(false, "Either batch log was never created, or it took more than #{@maxTimeout} seconds")
  end
end

When /^a batch job log has been created for "([^"]*)"$/ do |lz_key|
  puts "batch job log has been created for"
  lz = @ingestion_lz_identifer_map[lz_key]
  checkForBatchJobLog(lz)
end

def checkForBatchJobLog(landing_zone, should_has_log = true)
  puts "checkForBatchJobLog"
  intervalTime = 3 #seconds
                   #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 600
  sleep(intervalTime)
  iters = (1.0*@maxTimeout/intervalTime).ceil
    found = false
    if (INGESTION_MODE == 'remote')
        iters.times do |i|
            if remoteLzContainsFile("job-#{@source_file_name}*.log", landing_zone)
                puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
                found = true
                break
            else
                sleep(intervalTime)
            end
        end
    else
        iters.times do |i|
            if dirContainsBatchJobLog? landing_zone
                puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
                found = true
                break
            else
                sleep(intervalTime)
            end
        end
    end

  sleep(2)
  if should_has_log
    assert(found == true, "Either batch log was never created, or it took more than #{@maxTimeout} seconds")
  else
    assert(found == false, "Batch log should not be created")
  end
end

def scpUnzippedFilesToLandingZone(filename, dirname)
  @source_path = @local_file_store_path + dirname
  @destination_path = @landing_zone_path

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  file_path_list = Dir[@source_path + '/*.*'].select { |e| File.file?(e) }
  file_list ||=[]
  file_path_list.each { |e| file_list << File.basename(e.gsub("\\","/")) }
  puts "Copying files " + file_list.join(", ") + " to landing zone."
  if (INGESTION_MODE == 'remote')
    Dir.foreach(@landing_zone_path) do |entry|
      remoteLzCopy(@source_path + entry, @destination_path)
    end
  else
    # copy file from local filesystem to landing zone
    FileUtils.cp file_path_list, @destination_path
  end

  lz_file = @destination_path + filename
  puts "ruby #{UPLOAD_FILE_SCRIPT} STOR #{lz_file} #{ACTIVEMQ_HOST}"
  runShellCommand("ruby #{UPLOAD_FILE_SCRIPT} STOR #{lz_file} #{ACTIVEMQ_HOST}")

  assert(true, "File Not Uploaded")
end

def scpFileToLandingZone(filename)
  @source_path = @local_file_store_path + filename
  @destination_path = @landing_zone_path + filename

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  if (INGESTION_MODE == 'remote')
    remoteLzCopy(@source_path, @destination_path)
  else
    # copy file from local filesystem to landing zone
    FileUtils.cp @source_path, @destination_path
  end

  puts "ruby #{UPLOAD_FILE_SCRIPT} STOR #{@destination_path} #{ACTIVEMQ_HOST}"
  runShellCommand("ruby #{UPLOAD_FILE_SCRIPT} STOR #{@destination_path} #{ACTIVEMQ_HOST}")

  assert(true, "File Not Uploaded")
end

def scpFileToLandingZoneWithNewName(filename, dest_file_name)
  @source_path = @local_file_store_path + filename
  @destination_path = @landing_zone_path + dest_file_name

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  @source_file_name=dest_file_name; #this var is used to deternine Job Report file name.
  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  if (INGESTION_MODE == 'remote')
    remoteLzCopy(@source_path, @destination_path)
  else
    # copy file from local filesystem to landing zone
    FileUtils.cp @source_path, @destination_path
  end

  puts "ruby #{UPLOAD_FILE_SCRIPT} STOR #{@destination_path} #{ACTIVEMQ_HOST}"
  runShellCommand("ruby #{UPLOAD_FILE_SCRIPT} STOR #{@destination_path} #{ACTIVEMQ_HOST}")

  assert(true, "File Not Uploaded")
end

def scpFileToParallelLandingZone(lz, filename)
  @source_path = @local_file_store_path + filename
  @destination_path = lz + filename

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  if (INGESTION_MODE == 'remote')
    remoteLzCopy(@source_path, @destination_path)
  else
    # copy file from local filesystem to landing zone
    FileUtils.cp @source_path, @destination_path
  end

  puts "ruby #{UPLOAD_FILE_SCRIPT} STOR #{@destination_path} #{ACTIVEMQ_HOST}"
  runShellCommand("ruby #{UPLOAD_FILE_SCRIPT} STOR #{@destination_path} #{ACTIVEMQ_HOST}")

  assert(true, "File Not Uploaded")
end

When /^ctl file is scp to ingestion landing zone$/ do
  puts "Copying ctl file at #{Time.now}"
  scpUnzippedFilesToLandingZone @source_file_name, @source_dir_name
end

When /^zip file is scp to ingestion landing zone with name "([^"]*)"$/ do |dest_file_name|
  scpFileToLandingZoneWithNewName @source_file_name, dest_file_name
end

When /^zip file is scp to ingestion landing zone$/ do
  puts "Copying zip file at #{Time.now}"
  scpFileToLandingZone @source_file_name
end

When /^zip file is scp to ingestion landing zone for "([^"]*)"$/ do |lz_key|
  lz = @ingestion_lz_identifer_map[lz_key]
  file = @file_lz_map[lz_key]
  scpFileToParallelLandingZone(lz, file)
end

When /^zip files are scped to the ingestion landing zone$/ do
  scpFileToLandingZone @source_file_name1
  scpFileToLandingZone @source_file_name2
end

When /^an activemq instance "([^"]*)" running in "([^"]*)" and on jmx port "([^"]*)" stops$/ do |instance_name, instance_source, port|
  runShellCommand("#{instance_source}/activemq-admin stop  --jmxurl service:jmx:rmi:///jndi/rmi://localhost:#{port}/jmxrmi #{instance_name}" )
end

When /^an ingestion service "([^"]*)" running with pid "([^"]*)" stops$/ do |instance_name, pid|
  Process.kill(9, pid.to_i)
end

When /^I navigate to the Ingestion Service HealthCheck page and submit login credentials "([^"]*)" "([^"]*)"$/ do |user, pass|
  #uri = URI(INGESTION_HEALTHCHECK_URL)
  #req = Net::HTTP::Get.new(uri.request_uri)
  #req.basic_auth user, pass
  #res = Net::HTTP.start(uri.hostname, uri.port) {|http|
  #http.request(req)
  #}
  res = RestClient::Request.new(:method => :get, :url => INGESTION_HEALTHCHECK_URL, :user => user, :password => pass).execute
  puts res.body
  $healthCheckResult = res.body
end

When /^I can find a "(.*?)" with "(.*?)" "(.*?)" in tenant db "(.*?)"$/ do |collection, id_type, id, tenantId|
  disable_NOTABLESCAN()
  @db = @conn[convertTenantIdToDbName(tenantId)]
  @coll = @db[collection]
  @id_type = id_type
  @id = id
  # Set the "drilldown document" to the input id_type/id pair
  @dd_doc = @coll.find(id_type => id).to_a[0]
  enable_NOTABLESCAN()  
end
=begin
When /^Examining the studentSchoolAssociation collection references$/ do
  disable_NOTABLESCAN()
  @db = @conn[convertTenantIdToDbName("Midgar")]
  # build up the student_school_association value hash
  @coll = @db["studentSchoolAssociation"]
  #@st_sch_assoc = @coll.find_one({"type" => "studentSchoolAssociation"})
  @st_sch_assoc = @coll.find_one
  # ensure our query returned a document
  assert(true, "studentSchoolAssociation unset") if @st_sch_assoc.nil?
  puts "st_sch_assoc has data in it! Good Job!"
  # body.schoolId
  assert(true, "studentSchoolAssociation.body.schoolId unset") if @st_sch_assoc["body"]["schoolId"].nil?
  puts "studentSchoolAssociation.body.schoolId = #{@st_sch_assoc["body"]["schoolId"]}"
  # body.studentId
  assert(true, "studentSchoolAssociation.body.studentId unset") if @st_sch_assoc["body"]["studentId"].nil?
  puts "studentSchoolAssociation.body.studentId = #{@st_sch_assoc["body"]["studentId"]}"
  # body.entryDate
  assert(true, "studentSchoolAssociation.body.entryDate unset") if @st_sch_assoc["body"]["entryDate"].nil?
  puts "studentSchoolAssociation.body.entryDate = #{@st_sch_assoc["body"]["entryDate"]}"
  # body.entryGradeLevel
  assert(true, "studentSchoolAssociation.body.entryGradeLevel unset") if @st_sch_assoc["body"]["entryGradeLevel"].nil?
  puts "studentSchoolAssociation.body.entryGradeLevel = #{@st_sch_assoc["body"]["entryGradeLevel"]}"
  enable_NOTABLESCAN()
end
=end
When /^Examining the studentSchoolAssociation collection references$/ do
  @db = @conn[convertTenantIdToDbName("Midgar")]
  # build up the student_school_association value hash
  @coll = @db["studentSchoolAssociation"]
  #@st_sch_assoc = @coll.find_one({"type" => "studentSchoolAssociation"})
  @ref_doc = @coll.find_one

  # ensure our query returned a document
  raise "studentSchoolAssociation unset" if @ref_doc.nil?
  puts "ref_doc has data in it! Good Job!"

  # body.schoolId
  raise "studentSchoolAssociation.body.schoolId unset" if @ref_doc["body"]["schoolId"].nil?
  puts "studentSchoolAssociation.body.schoolId = #{@ref_doc["body"]["schoolId"]}"
  @ref_doc.[]="educationOrganization", @ref_doc["body"]["schoolId"]

  # body.studentId
  raise "studentSchoolAssociation.body.studentId unset" if @ref_doc["body"]["studentId"].nil?
  puts "studentSchoolAssociation.body.studentId = #{@ref_doc["body"]["studentId"]}"
  @ref_doc.[]="student", @ref_doc["body"]["studentId"]

  # body.entryDate
  raise "studentSchoolAssociation.body.entryDate unset" if @ref_doc["body"]["entryDate"].nil?
  puts "studentSchoolAssociation.body.entryDate = #{@ref_doc["body"]["entryDate"]}"

  # body.entryGradeLevel
  raise "studentSchoolAssociation.body.entryGradeLevel unset" if @ref_doc["body"]["entryGradeLevel"].nil?
  puts "studentSchoolAssociation.body.entryGradeLevel = #{@ref_doc["body"]["entryGradeLevel"]}"
end

When /^Examining the staffEducationOrganizationAssociation collection references$/ do
  @db = @conn[convertTenantIdToDbName("Midgar")]
  # build up the staffEducationOrganizationAssociation value hash
  @coll = @db["staffEducationOrganizationAssociation"]
  @ref_doc = @coll.find_one

  # ensure our query returned a document
  assert(false, "staffEducationOrganizationAssociation unset") if @ref_doc.nil?
  
  # body.staffReference
  if @ref_doc["body"]["staffReference"].nil?
    raise "staffEducationOrganizationAssociation.body.schoolId unset"
  end
  puts "staffEducationOrganizationAssociation.body.schoolId = #{@ref_doc["body"]["staffReference"]}"
  @ref_doc.[]="staff", @ref_doc["body"]["staffReference"]
 
  # set edOrg _id to body.educationOrganizationReference
  if @ref_doc["body"]["educationOrganizationReference"].nil?
    raise "staffEducationOrganizationAssociation.body.studentId unset"
  end
  puts "staffEducationOrganizationAssociation.body.studentId = #{@ref_doc["body"]["educationOrganizationReference"]}"
  @ref_doc.[]="educationOrganization", @ref_doc["body"]["educationOrganizationReference"]
  

  # body.staffClassification
  if @ref_doc["body"]["staffClassification"].nil?
    raise "staffEducationOrganizationAssociation.body.staffClassification unset"
  end
  puts "staffEducationOrganizationAssociation.body.staffClassification = #{@ref_doc["body"]["staffClassification"]}"

  # body.positionTitle
  if @ref_doc["body"]["positionTitle"].nil?
    raise "staffEducationOrganizationAssociation.body.positionTitle unset"
  end
  puts "staffEducationOrganizationAssociation.body.positionTitle = #{@ref_doc["body"]["positionTitle"]}"
end

When /^Examining the teacherSchoolAssociation collection references$/ do
  @db = @conn[convertTenantIdToDbName("Midgar")]
  # build up the teacherSchoolAssociation value hash
  @coll = @db["teacherSchoolAssociation"]
  @ref_doc = @coll.find_one
  
  # ensure our query returned a document
  raise "teacherSchoolAssociation unset" if @ref_doc.nil?
  
  # body.staffReference
  raise "teacherSchoolAssociation.body.teacherId unset" if @ref_doc["body"]["teacherId"].nil?
  puts "teacherSchoolAssociation.body.teacherId = #{@ref_doc["body"]["teacherId"]}"
  @ref_doc.[]="staff", @ref_doc["body"]["teacherId"]


  # set edOrg _id to body.schoolId
  raise "teacherSchoolAssociation.body.schoolId unset" if @ref_doc["body"]["schoolId"].nil?
  puts "teacherSchoolAssociation.body.schoolId = #{@ref_doc["body"]["schoolId"]}"
  @ref_doc.[]="educationOrganization", @ref_doc["body"]["schoolId"]
end

############################################################
# STEPS: THEN
############################################################

Then /^I should see following map of indexes in the corresponding collections:$/ do |table|
  @db   = @conn[@ingestion_db_name]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])
    @indexcollection = @db.collection("system.indexes")
    #puts "ns" + @ingestion_db_name+"student," + "name" + row["index"].to_s
    @indexCount = @indexcollection.find("ns" => @ingestion_db_name + "." + row["collectionName"], "key" => {row["index"] => 1}).to_a.count()

    #puts "Index Count = " + @indexCount.to_s

    if @indexCount.to_s == "0"
      puts "Index was not created for " + @ingestion_db_name+ "." + row["collectionName"] + " with key = " + row["index"]
      @result = "false"
    end
  end

  assert(@result == "true", "Some indexes were not created successfully.")

end

Then /^I remove the following indexes in the corresponding collections:$/ do |table|
  @db   = @conn[@ingestion_db_name]
  table.hashes.map do |row|
    @indexcollection = @db.collection("system.indexes")
    indexQRS = @indexcollection.find("ns" => @ingestion_db_name + "." + row["collectionName"], "key" => {row["index"] => 1}).to_a
    index = indexQRS.pop()
    indexName = index["name"]
    @entity_collection = @db.collection(row["collectionName"])
    @entity_collection.drop_index(indexName)
  end
end

def cleanupSubDoc(superdocs, subdoc)
  superdocs.each do |superdoc|
    superdoc[subdoc] = nil
    @entity_collection.update({"_id"=>superdoc["_id"]}, superdoc)
  end
end

def subDocParent(collectionName)
  case collectionName
    when "studentSectionAssociation"
      "section"
    when "gradebookEntry"
      "section"
    when "teacherSectionAssociation"
      "section"
    when "studentAssessment"
      "student"
    when "studentProgramAssociation"
      "program"
    when "studentParentAssociation"
      "student"
    when "studentCohortAssociation"
      "cohort"
    when "studentDisciplineIncidentAssociation"
      "student"
    else
      nil
  end
end

def subDocCount(parent, subdoc, opts=nil, key=nil, match_value=nil)
  total = 0
  coll = @db.collection(parent)
  coll.find().each do |doc|
    unless doc[subdoc] == nil
      if key == nil and match_value == nil and opts==nil
        total += doc[subdoc].size
      else
        array = doc[subdoc]
        array.each do |sub|
          @contains = true
          if (key != nil && match_value != nil)
            @contains = false
            subdocMatch(sub, key, match_value)
          end
          @failed = false
          if (@contains and opts != nil)
            opts.each_pair do |opt_key, opt_value|
              #and only now
              @contains = false
              subdocMatch(sub, opt_key, opt_value)
              if not @contains
                @failed = true
              end
            end
            if not @failed
              total += 1
            end
          elsif (@contains)
            total += 1
          end
        end
      end
    end
  end
  total
end

def subdocMatch(subdoc, key, match_value)
  if key.is_a? Array
    keys = key
  else
    keys = key.split('.')
  end
  tmp = subdoc
  for i in 0...keys.length
    path = keys[i]
    if tmp.is_a? Hash
      tmp = tmp[path]
      if tmp.is_a? Integer
        tmp = tmp.to_s()
      end
      if i == keys.length - 1
        if match_value.is_a? Array and tmp.is_a? Array
          @contains = true if (match_value & tmp).size > 0
        elsif match_value.is_a? Array
          @contains = true if match_value.include? tmp
        elsif tmp == match_value
          @contains = true
        end
      end
    elsif tmp.is_a? Array
      newkey = Array.new(keys[i...keys.length])
      tmp.each do |newsubdoc|
        subdocMatch(newsubdoc, newkey, match_value)
      end
    end
  end
end

def runSubDocQuery(subdoc_parent, subdoc, searchType, searchParameter, searchValue, opts=nil)
  subDocCount(subdoc_parent, subdoc, opts, searchParameter, searchValue)

  #  This requires mongo driver 1.7.0, on CI it is still 1.6.4... Shall we upgrade?
  #
  #  coll.aggregate([ {"$match" => {"#{subdoc}.#{searchParameter}" => {"$exists" => true}}},
  #                 {"$project" => {"#{subdoc}" => 1, "_id" => 0, "count" => 1}},
  #                 {"$unwind" => "$#{subdoc}"},
  #                 {"$match" => {"#{subdoc}.#{searchParameter}" => "#{searchValue}"}},
  #              ]).size

  #
  # This does not work as it counts the number of the parents, not number of
  # subdocs
  #
  #-------------------------------------------
  # @entity_collection = @db.collection(subdoc_parent)
  # param = subdoc + "." + searchParameter
  #
  # if searchType == "integer"
  #      @entity_count = @entity_collection.find({"$and" => [{param => searchValue.to_i}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).count().to_s
  # elsif searchType == "double"
  #      @entity_count = @entity_collection.find({"$and" => [{param => searchValue.to_f}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).count().to_s
  # elseif searchType == "boolean"
  #   if searchValue == "false"
  #     @entity_count = @entity_collection.find({"$and" => [{param => false}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).count().to_s
  #   else
  #     @entity_count = @entity_collection.find({"$and" => [{param => true}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).count().to_s
  #   end
  # elsif searchType == "nil"
  #      @entity_count = @entity_collection.find({"$and" => [{param => nil}, {"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).count().to_s
  # else
  #   @entity_count = @entity_collection.find({"$and" => [{param => searchValue},{"metaData.tenantId" => {"$in" => TENANT_COLLECTION}}]}).count().to_s
  # end
  # ----------------------------------------
end

Then /^I should see following map of entry counts in the corresponding collections:$/ do |table|
  disable_NOTABLESCAN()
  @db   = @conn[@ingestion_db_name]
  @result = "true"
  puts "db name #{@db.name} on server #{INGESTION_DB}"

  table.hashes.map do |row|
    parent = subDocParent row["collectionName"]
    if parent
      @entity_count = subDocCount(parent, row["collectionName"])
    else
      @entity_collection = @db.collection(row["collectionName"])
      @entity_count = @entity_collection.count().to_i
    end

    if @entity_count.to_s != row["count"].to_s
      @result = "false"
      red = "\e[31m"
      reset = "\e[0m"
    end

    puts "#{red}There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection. Expected: " + row["count"].to_s+"#{reset}"
  end

  assert(@result == "true", "Some records didn't load successfully.")
  enable_NOTABLESCAN()
end

Then /^I should see following map of entry counts in the corresponding sli db collections:$/ do |table|
disable_NOTABLESCAN()
puts INGESTION_DB_NAME
@db = @conn[INGESTION_DB_NAME]                                                                                                  
@result = "true"

                                                                                                    
table.hashes.map do |row|
  @entity_collection = @db.collection(row["collectionName"])
  @entity_count = @entity_collection.count().to_i
  puts @entity_count
  puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection"
                                                                                                     
  if @entity_count.to_s != row["count"].to_s
    @result = "false"
  end
end

                                                                                                    
assert(@result == "true", "Some records didn't load successfully.")
enable_NOTABLESCAN()
end

Then /^I should see following map of entry counts in the corresponding batch job db collections:$/ do |table|
  disable_NOTABLESCAN()
  @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])
    @entity_count = @entity_collection.count().to_i
    puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection"

    if @entity_count.to_s != row["count"].to_s
      @result = "false"
    end
  end

  assert(@result == "true", "Some records didn't load successfully.")
  enable_NOTABLESCAN()
end

Then /^I should say that we started processing$/ do
  puts "Ingestion Performance Dataset started Ingesting.  Please wait a few hours for it to complete."
  assert(true, "Some records didn't load successfully.")
end

Then /^I check to find if record is in collection:$/ do |table|
   check_records_in_collection(table, @ingestion_db_name)
end

Then /^I check to find if record is in sli db collection:$/ do |table|
   check_records_in_collection(table, INGESTION_DB_NAME)
end

# Deep-document inspection when we are interested in top-level entities (_id, type, etc)
Then /^the "(.*?)" entity "(.*?)" should be "(.*?)"$/ do |coll, doc_key, expected_value|
  disable_NOTABLESCAN()
  # Make sure drilldown_document has been set already
  assert(defined? @dd_doc, "Required mongo document has not been retrieved")
  # Perform a deep document inspection of doc.subdoc.keyvalue
  # Check the body of the returned document array for expected key/value pair
  assert(deepDocumentInspect(coll, doc_key, expected_value), "#{doc_key} not set to #{expected_value}")
  enable_NOTABLESCAN()
end

Then /^the studentSchoolAssociation references "(.*?)" "(.*?)" with "(.*?)"$/ do |coll, field, ref|
  disable_NOTABLESCAN()
  result = false
  # cache the referenced collection (student OR educationOrganizatio)
  ref_coll = @db[coll]
  raise "Could not find #{coll} collection" if ref_coll.count == 0

  # This part gets a little hackish because mongo returns nested structs
  # for student school assoc we need to support at most 2 nodes
  # -> Get values from the studentSchoolAssociation doc
  ssa_doc_root_field  = ref.split(".").shift
  ssa_doc_value_field   = ref.split(".").pop
  ssa_value = @st_sch_assoc[ssa_doc_root_field][ssa_doc_value_field]
  # -> Get values from the student OR educationOrganization doc
  ref_doc_root_field = field.split(".").shift
  ref_doc_value_field  = field.split(".").pop
  # -> The fields and values are in a hash appended to the returned array
  # -> so pop the last array element into the results hash

  # find the referenced document and pull out the field value(s)
  # this returns a Mongo::Cursor object (even if miss), so be careful
  # on miss, ref_doc.count will be 0 (not null). on hit, call
  # ref_doc.to_a, and get an array of field match hashes
  id = @st_sch_assoc["body"]["studentId"] if coll == "student"
  id =  @st_sch_assoc["body"]["schoolId"] if coll == "educationOrganization"
  ref_doc = ref_coll.find({"_id" => id}, :fields => field).to_a
  raise "Could not find #{coll} document with _id #{id}" if ref_doc.count == 0
  ref_doc_results_hash = ref_doc.pop

  # -> if this is a top-level document, just check equality
  if ref_doc_results_hash.length == 1
    # -> pass case (equal)
    result = true if ssa_value == ref_doc_results_hash[ref_doc_root_field]
  else
    # -> loop through subdoc hits and check if they match referencer document
    for field_hash in ref_doc_results_hash[ref_doc_root_field]
      result = true if ssa_value == field_hash[ref_doc_value_field]
    end
  end
  assert(result, "Could not find the required value in the referenced document")
  enable_NOTABLESCAN()
end


Then /^the document references "(.*?)" "(.*?)" with "(.*?)"$/ do |coll, src_field, ref_field|
  disable_NOTABLESCAN()
  result = false
  # Nomenclature:
  # -> ref_doc: reference document - tests check these referential values against "real" values
  # --> (example: studentSchollAssociation, staffEdOrgAssociation, etc)
  # -> src_doc : source document (the reference source, tests "target" these as "real" values)
  # --> (example: student, staff, educationOrgainization, etc)
  #
  # cache the target collection
  src_coll = @db[coll]
  raise "Could not find #{coll} collection" if src_coll.count == 0

  # -> Get values from the source document
  src_id = @ref_doc[coll]
  # -> find the referenced document and pull out the field value(s)
  src_doc = src_coll.find({"_id" => src_id}, :fields => src_field).to_a
  raise "Could not find #{coll} document with _id #{src_id}" if src_doc.count == 0
  src_doc_hash = src_doc.pop

  # -> Get values from the source document
  src_value = extractNestedDoc(src_doc_hash, src_field)
  # -> Get values from the reference document
  ref_value = extractNestedDoc(@ref_doc, ref_field)
  
  # if doc is an array, we need to iterate over doc
  # special thanks to the idiot mongo BSON::OrderedHash
  if src_value.kind_of?(Array)
    i = 0
    for value in src_value
      result = true if value[value.keys[i]] == ref_value
      i += 1
    end
  else
    result = true if src_value == ref_value
  end

  assert(result, "Could not find the source value in the referenced document")
  enable_NOTABLESCAN()
end

class BSON::OrderedHash
  include Enumerable
end

def extractNestedDoc(doc, doc_tree)

  # determine the length of the tree
  doc_tree.split(".").collect do |i|
    return doc if doc.kind_of?(Array)
    doc = doc[i]
  end
  # return the value from the final node
  return doc
end

                                                                                                     
=begin
# Deep-document inspection for when we are interested in subdocs (body, metaData, schools, etc)
Then /^the "(.*?)" entity "(.*?)\.(.*?)" should be "(.*?)"$/ do |coll, doc_key, subdoc_key, expected_value|
  # Make sure drilldown_document has been set already
  assert(defined? @dd_doc, "Required mongo document has not been retrieved")
  puts "expected_value is #{expected_value} which is of type #{expected_value.class}"
  # Perform a deep document inspection of doc.subdoc.keyvalue
  # Check the body of the returned document array for expected key/value pair
  assert(deepDocumentInspect(doc_key, subdoc_key, expected_value), "#{doc_key}.#{subdoc_key} not set to #{expected_value}")
end
=end

# Deep-Document Inspection of the Student collection in TENANT_DB
# -> Pass in mongo document switches (doc.subdoc) and expected value,
# -> query mongo for the @db.@coll.doc.subdoc, and check equality
#
# Ex: deepDocumentInspect(body, stateUniqueStaffId, cgray)
#
def deepDocumentInspect(coll, doc_key, expected_value)
  # Split out each document node into an array

  doc_ary = doc_key.split(".")

  # --> This checks whether body.subdoc = expected_value
  if doc_ary[0] == "body"
    body = @dd_doc["body"]
    subdoc = doc_ary[1]
    # Parse the actual value from the student
    # -> This might be an array OR a string, depending on what's in mongo
    # -> so we need to use duck typing 
    # --> if the subdoc is an array, take the first element
    # TODO: this should iterate through all elems of the list, needs refactor
    if subdoc.match    (/race/i)
      real_value = body["race"][0]

    elsif subdoc.match (/highlyQualifiedTeacher/)
      real_value = body["highlyQualifiedTeacher"].to_s

    elsif subdoc.match (/sex/i)
      real_value = body["sex"]

    elsif subdoc.match (/highestLevelOfEducationCompleted/i)
      real_value = body["highestLevelOfEducationCompleted"]

    elsif subdoc.match (/birthDate/i)
      real_value = body["birthDate"]

    elsif doc_ary[1].respond_to?(:to_ary)
      real_value = @dd_doc[doc_ary[0]][doc_ary[1]][0]
    # --> this will cast a boolean to a string, or just pass a string thru
    elsif doc_ary[1].respond_to?(:to_s) 
      real_value = @dd_doc[doc_ary[0]][doc_ary[1]]
    # --> If the mongo entity is not something we can handle, we need
    # --> to fail gracefully and notify the type issue
    else
      puts "This subdoc type (#{doc_ary[1]}) is not supported"
      return false
    end
         
    # Check equality
    puts "Looking for #{doc_ary[0]}.#{doc_ary[1]} to be #{expected_value}, found #{real_value}"
    check_equals(real_value, expected_value)

  # --> This checks whether schools.subdoc = expected_value
  elsif doc_ary[0] == "schools"
    # This checks how many records of the schools.subdoc type exist
    # --> Example: [tenant_db].student.schools.entryGradeLevel has one entity per year
    num_entities = @dd_doc[doc_ary[0]].length
    
    # Recursively check the returned document array for expected key/value pair
    for i in 0..num_entities-1
      real_value = @dd_doc[doc_ary[0]][i][doc_ary[1]]
            
      # Check equality
      return true if real_value == expected_value  
    end
   puts "#{doc_ary[0]}.#{doc_ary[1]} set to #{real_value}, expected #{expected_value}"
   return false
  
  # --> Not interested in a subdoc (cases: _id, type)
  elsif doc_ary[0].match(/type/) or doc_ary[0].match(/_id/)
    real_value = @dd_doc[doc_ary[0]]

    puts "The real_value is set to: #{real_value}, expected #{expected_value}"

    # Check equality
    check_equals(real_value, expected_value) 
  # --> Default case if I don't recognize "doc_ary"
  else
    puts "ERROR: This type of entity (#{doc_ary[0]}) is not covered by deepDocumentInspect in ingestion_steps.rb"
    return false 
  end
end

def studentSchoolAssociationInspect(key_hash, expected_value)
  # look for the _id in studentSchoolAssociation

  # look for the 

  # verify the student_id matches a student._id document

  # verify the school_id matches a student.schools._id document

  # verify the entryDate matches a student.schools.entryDate document

  # verify the entryGradeLevel matches a student.schools.entryGradeLevel document

end

def check_equals(real_value, expected_value)
  return true if real_value == expected_value
  return false
end

Then /^I check _id of stateOrganizationId "([^"]*)" for the tenant "([^"]*)" is in metaData.edOrgs:$/ do |stateOrganizationId, tenantId, table|
  disable_NOTABLESCAN()
  @result = "true"

  @db = @conn[convertTenantIdToDbName(tenantId)]
  @edOrgCollection = @db.collection("educationOrganization")
  @edOrgEntity = @edOrgCollection.find_one({"body.stateOrganizationId" => stateOrganizationId})
  puts "#{@edOrgEntity}"
  @stateOrganizationId = @edOrgEntity['_id']

  table.hashes.map do |row|
    parent = subDocParent row["collectionName"]
    if parent
      @entity_count = subDocCount(parent, row["collectionName"], {"metaData.edOrgs" => [@stateOrganizationId]})
    else
      @entity_collection = @db.collection(row["collectionName"])
      @entity_count = @entity_collection.find({"metaData.edOrgs" => @stateOrganizationId}).count().to_i
    end

    if @entity_count.to_s != row["count"].to_s
      @result = "false"
      red = "\e[31m"
      reset = "\e[0m"
    end

    puts "#{red}There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection. Expected: " + row["count"].to_s+"#{reset}"
  end

  assert(@result == "true", "Some records do not have the correct education organization context.")
  enable_NOTABLESCAN()
end

Then /^I check to find if complex record is in batch job collection:$/ do |table|
  disable_NOTABLESCAN()
  @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])

    @entity_count = @entity_collection.find({row["searchParameter"] => { "$type" => 3 }}).count().to_s

    puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection for record " + row["searchParameter"]

    if @entity_count.to_s != row["expectedRecordCount"].to_s
      @result = "false"
    end
  end

  assert(@result == "true", "Some records are not found in collection.")
  enable_NOTABLESCAN()
end

Then /^I check to find if record is in batch job collection:$/ do |table|
  disable_NOTABLESCAN()
  @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])

    if row["searchType"] == "integer"
      @entity_count = @entity_collection.find({row["searchParameter"] => row["searchValue"].to_i}).count().to_s
    else
      @entity_count = @entity_collection.find({row["searchParameter"] => row["searchValue"]}).count().to_s
    end

    puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection for record with " + row["searchParameter"] + " = " + row["searchValue"]

    if @entity_count.to_s != row["expectedRecordCount"].to_s
      @result = "false"
    end
  end

  assert(@result == "true", "Some records are not found in collection.")
  enable_NOTABLESCAN()
end


Then /^I find a\(n\) "([^"]*)" record where "([^"]*)" is equal to "([^"]*)"$/ do |collection, field, value|
  disable_NOTABLESCAN()

  @db = @conn[@ingestion_db_name]
  @entity_collection = @db.collection(collection)
  @entity =  @entity_collection.find({field => value})

  parent = subDocParent collection
  if parent
    @entity_collection = @db.collection(parent)
    sub_field = collection + "." + field
    @entity =  @entity_collection.find({sub_field => value})
  else
    @entity_collection = @db.collection(collection)
    @entity =  @entity_collection.find({field => value})
  end

  assert(@entity.count == 1, "Found more than one document with this query (or zero :) )")
  enable_NOTABLESCAN()
end

When /^verify that "([^"]*)" is (equal|unequal) to "([^"]*)"$/ do |arg1, equal_or_unequal, arg2|
  @entity.each do |ent|
    if equal_or_unequal == "equal"
      assert(getValueAtIndex(ent,arg1) == getValueAtIndex(ent,arg2), "#{arg1} is not equal to #{arg2}")
    else
      assert(getValueAtIndex(ent,arg1) != getValueAtIndex(ent,arg2), "#{arg1} is not not equal to #{arg2}")
    end
  end
end

def getValueAtIndex(ent, index_string)
  val = ent.clone
  index_string.split('.').each do |part|
    is_num?(part) ? val = val[part.to_i] : val = val[part]
  end
  val
end

Then /^verify the following data in that document:$/ do |table|
  disable_NOTABLESCAN()
  @entity.each do |ent|
    puts "entity #{ent}"
    table.hashes.map do |row|
      curSearchString = row['searchParameter']
      val = ent.clone
      curSearchString.split('.').each do |part|
        is_num?(part) ? val = val[part.to_i] : val = val[part]
      end
      if row["searchType"] == "integer"
        assert(val == row['searchValue'].to_i, "Expected value: #{row['searchValue']}, but received #{val}")
      else
        assert(val == row['searchValue'], "Expected value: #{row['searchValue']}, but received #{val}")
      end
    end
  end
  enable_NOTABLESCAN()
end

Then /^I verify all super doc "(.*?)" entities have correct type field$/ do |entityType|
disable_NOTABLESCAN()
super_coll=@db[entityType]
total_count = super_coll.count
count = super_coll.find({"type" => entityType}).count
assert(total_count == count, "not all super doc #{entityType} have correct type field")
enable_NOTABLESCAN()
end

Then /^verify (\d+) "([^"]*)" record\(s\) where "([^"]*)" equals "([^"]*)" and its field "([^"]*)" references this document$/ do |count,collection,key,value,refField|
  disable_NOTABLESCAN()
  @entity.each do |ent|
    @db = @conn[@ingestion_db_name]
    @entity_collection = @db.collection(collection)
    @refEntity = @entity_collection.find({key => value, refField => ent['_id']})
    assert(@refEntity.count == count.to_i, "Expected #{count} documents but found #{@refEntity.count}")
  end
  enable_NOTABLESCAN()
end

def is_num?(str)
  Integer(str)
rescue ArgumentError
  false
else
  true
end

def checkForContentInFileGivenPrefix(message, prefix)

  if (INGESTION_MODE == 'remote')
    if remoteFileContainsMessage(prefix, message, @landing_zone_path)
      assert(true, "Processed all the records.")
    else
      assert(false, "Didn't process all the records.")
    end

  else
    @job_status_filename = ""
    Dir.foreach(@landing_zone_path) do |entry|
      if (entry.rindex(prefix))
        # LAST ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(@landing_zone_path + @job_status_filename, "r")
    puts "STATUS FILENAME = " + @landing_zone_path + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(@landing_zone_path + @job_status_filename).join()
      #puts "FILE CONTENTS = " + file_contents

      missingStringPrefixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_PREFIX)
      missingStringSuffixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_SUFFIX)
      if (missingStringPrefixIdx != nil && missingStringSuffixIdx != nil)
        assert(false, "Missing error message string for "+(file_contents[missingStringPrefixIdx..missingStringSuffixIdx+2]))
      end
      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message, contents were:\n#{file_contents}")
      end
      aFile.close
    else
      raise "File " + @job_status_filename + "can't be opened"
    end
  end
end

def checkForNullContentInFileGivenPrefix(message, prefix)

  if (INGESTION_MODE == 'remote')
    if remoteFileContainsMessage(prefix, message, @landing_zone_path)
      assert(false, "Processed all the records.")
    else
      assert(false, "Didn't process all the records.")
    end

  else
    @job_status_filename = ""
    Dir.foreach(@landing_zone_path) do |entry|
      if (entry.rindex(prefix))
        # LAST ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(@landing_zone_path + @job_status_filename, "r")
    puts "STATUS FILENAME = " + @landing_zone_path + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(@landing_zone_path + @job_status_filename).join()
      #puts "FILE CONTENTS = " + file_contents

      missingStringPrefixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_PREFIX)
      missingStringSuffixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_SUFFIX)
      if (missingStringPrefixIdx != nil && missingStringSuffixIdx != nil)
        assert(false, "Missing error message string for "+(file_contents[missingStringPrefixIdx..missingStringSuffixIdx+2]))
      end
      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end
      aFile.close
    else
      raise "File " + @job_status_filename + "can't be opened"
    end
  end
end

def checkForContentInFileGivenPrefixAndXMLName(message, prefix, xml_name)

  if (INGESTION_MODE == 'remote')

    @resultOfIngestion = ""
    Net::SSH.start(LZ_SERVER_URL, INGESTION_USERNAME, :password => INGESTION_PASSWORD) do |ssh|
      ssh.exec!("ls -l #{@landing_zone_path} | grep #{prefix}#{xml_name} | tail -1 | awk '{print $NF}' | xargs -I x cat #{@landing_zone_path}/x") do |channel, stream, data|
        @resultOfIngestion << data
      end
    end

    @messageString = message.to_s

    if @resultOfIngestion.include? @messageString
      assert(true, "Processed all the records.")
    else
      puts "Actual message was " + @resultOfIngestion
      assert(false, "Didn't process all the records.")
    end

  else
    @job_status_filename = ""
    Dir.foreach(@landing_zone_path) do |entry|
      if (entry.rindex(prefix) && entry.rindex(xml_name))
        # XML ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(@landing_zone_path + @job_status_filename, "r")
    puts "STATUS FILENAME = " + @landing_zone_path + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")
    if aFile
      file_contents = IO.readlines(@landing_zone_path + @job_status_filename).join()
      #puts "FILE CONTENTS = " + file_contents

      missingStringPrefixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_PREFIX)
      missingStringSuffixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_SUFFIX)
      if (missingStringPrefixIdx != nil && missingStringSuffixIdx != nil)
        assert(false, "Missing error message string for "+(file_contents[missingStringPrefixIdx..missingStringSuffixIdx+2]))
      end
      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end
      aFile.close
    else
      raise "File " + @job_status_filename + "can't be opened"
    end
  end
end

def parallelCheckForContentInFileGivenPrefix(message, prefix, landing_zone)

  if (INGESTION_MODE == 'remote')
    if remoteFileContainsMessage(prefix, message, landing_zone)
      assert(true, "Processed all the records.")
    else
      assert(false, "Didn't process all the records.")
    end

  else
    @job_status_filename = ""
    Dir.foreach(landing_zone) do |entry|
      if (entry.rindex(prefix))
        # LAST ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(landing_zone + @job_status_filename, "r")
    puts "STATUS FILENAME = " + landing_zone + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(landing_zone + @job_status_filename).join()
      #puts "FILE CONTENTS = " + file_contents

      missingStringPrefixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_PREFIX)
      missingStringSuffixIdx = file_contents.rindex(ERROR_REPORT_MISSING_STRING_SUFFIX)
      if (missingStringPrefixIdx != nil && missingStringSuffixIdx != nil)
        assert(false, "Missing error message string for "+(file_contents[missingStringPrefixIdx..missingStringSuffixIdx+2]))
      end
      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end

    else
      raise "File " + @job_status_filename + "can't be opened"
    end
  end
end

Then /^I should see "([^"]*)" in the resulting batch job file$/ do |message|
  prefix = "job-" + @source_file_name + "-"
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should not see "([^"]*)" in the resulting batch job file$/ do |message|
  # Why is this exactly the same as above?
  prefix = "job-" + @source_file_name + "-"
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting batch job error file$/ do |message|
  prefix = "error." + @source_file_name + "-"
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting batch job warning file$/ do |message|
  prefix = "job_warn-" + @source_file_name + "-"
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting batch job file for "([^"]*)"$/ do |message, lz_key|
  lz = @ingestion_lz_identifer_map[lz_key]
  file = @file_lz_map[lz_key]
  prefix = "job-" + file + "-"
  parallelCheckForContentInFileGivenPrefix(message, prefix, lz)
end

Then /^I should see "(.*?)" in the resulting error log file for "([^"]*)"$/ do |message, load_file|
  prefix = "error."+load_file
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting error log file$/ do |message|
  prefix = "error."
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting StudentAssessment warning log file$/ do |message|
  prefix = "warn.InterchangeStudentAssessment"
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting warning log file$/ do |message|
  prefix = "warn."
  checkForContentInFileGivenPrefix(message, prefix)
end

Then /^I should see "([^"]*)" in the resulting warning log file for "([^"]*)"$/ do |message, xml_name|
  prefix = "warn."
  checkForContentInFileGivenPrefixAndXMLName(message, prefix, xml_name)
end

Then /^I should not see an error log file created$/ do
  checkForErrorWarnLogFile(@landing_zone_path, "error") if !@hasNoLandingZone
end

And /^I should not see a warning log file created$/ do
  checkForErrorWarnLogFile(@landing_zone_path, "warn") if !@hasNoLandingZone
end


Then /^I should not see an error log file created for "([^\"]*)"$/ do |lz_key|
  lz = @ingestion_lz_identifer_map[lz_key]
  checkForErrorWarnLogFile(lz, "error")
end

def checkForErrorWarnLogFile(landing_zone, prefix)
  # prefix is either 'error' or 'warn' in lower case
  if (INGESTION_MODE == 'remote')
    if !remoteLzContainsFile("#{prefix}.*", landing_zone)
      assert(true, "No #{prefix} files created.")
    else
      assert(false, "#{prefix} files created.")
    end

  else
    @filename_component = "#{prefix}."

    @status_filename = ""
    Dir.foreach(landing_zone) do |entry|
      if (entry.rindex(@filename_component))
        puts File.open(landing_zone + entry).read
        # LAST ENTRY IS OUR FILE
        @status_filename = entry
      end
    end

    puts "STATUS FILENAME = " + landing_zone + @status_filename
    assert(@status_filename == "", "File " + @status_filename + " exists")
  end
end

Then /^I find a record in "([^\"]*)" with "([^\"]*)" equal to "([^\"]*)"$/ do |collection, searchTerm, value|
  disable_NOTABLESCAN()
  db = @conn[@ingestion_db_name]
  collection = db.collection(collection)

  @record = collection.find_one({searchTerm => value})
  @record.should_not == nil
  enable_NOTABLESCAN()
end

Then /^the field "([^\"]*)" has value "([^\"]*)"$/ do |field, value|
  object = @record
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
  end
  object.to_s.should == value
end

Then /^the field "([^\"]*)" with value "([^\"]*)" is encrypted$/ do |field, value|
  object = @record
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
    endt = object[f]
  end
  object.should_not == value
end

Then /^the jobs ran concurrently$/ do
  @db   = @batchConn[INGESTION_BATCHJOB_DB_NAME]
  @entity_collection = @db.collection("newBatchJob")

  latestStartTime = nil
  earliestStopTime = nil

  @entity_collection.find( {}, :fields => { "_id" => 0, "stages" => 1 } ).each { |stages|
    stages[ "stages" ].each { |stage|

      if stage[ "stageName" ] == "ZipFileProcessor" and ( latestStartTime == nil or stage[ "startTimestamp" ] > latestStartTime )
        latestStartTime = stage[ "startTimestamp" ]
      end
      if stage[ "stageName" ] == "JobReportingProcessor" and ( earliestStopTime == nil or stage[ "stopTimestamp" ] < earliestStopTime )
        earliestStopTime = stage[ "stopTimestamp" ]
      end
    }
  }

  assert(latestStartTime < earliestStopTime, "Expected concurrent job runs, but one finished before another began.")
end

Then /^the database is sharded for the following collections/ do |table|
  @configDb = @conn.db(CONFIG_DB_NAME)

  @shardsCollection = @configDb.collection("shards")
  @result = "true"

  if @shardsCollection.count() > 0
    @chunksCollection = @configDb.collection("chunks")

    table.hashes.map do |row|
      @chunksCount = @chunksCollection.find("ns" => @ingestion_db_name + "." + row["collectionName"]).to_a.count()
      if @chunksCount.to_s == "0"
        puts "Database " + @ingestion_db_name+ " is not sharded for the collection " + row["collectionName"]
        @result = "false"
      end
    end
  else
    puts "Mongo is not sharded"
  end

  assert(@result == "true", "Database was not sharder successfully.")
end


When /^I find a record in "([^"]*)" where "([^"]*)" is "([^"]*)"$/ do |collection, searchTerm, value|
  step "I find a record in \"#{collection}\" with \"#{searchTerm}\" equal to \"#{value}\""
end

When /^I find a record in "(.*?)" under "(.*?)" where "(.*?)" is "(.*?)"$/ do |collection, field, searchTerm, value|
  step "I find a record in \"#{collection}\" with \"#{field + "." + searchTerm}\" equal to \"#{value}\""
  @record = findField(@record, field).find_all{|r| findField(r, searchTerm) == value}[0]
end

def findField(object, field)
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
  end
  object
end

Then /^the field "([^"]*)" is an array of size (\d+)$/ do |field, arrayCount|
  object = findField(@record, field)
  assert(object.length==Integer(arrayCount),"the field #{field}, #{object} is not an array of size #{arrayCount}")
  @idsArray
end

Then /^"([^"]*)" contains a reference to a "([^"]*)" where "([^"]*)" is "([^"]*)"$/ do |referenceField, collection, searchTerm, value|
  disable_NOTABLESCAN()

  db = @conn[@ingestion_db_name]
  collection = db.collection(collection)
  referred = collection.find_one({searchTerm => value})
  referred.should_not == nil
  id = referred["_id"]
  references = findField(@record, referenceField)

  assert(references.include?(id), "the record #{@record} does not contain a reference to the #{collection} #{value}")
  enable_NOTABLESCAN()
end

When /^zip file "(.*?)" is scp to ingestion landing zone$/ do |fileName|
  @source_file_name = fileName
  step "zip file is scp to ingestion landing zone"
end

When /^a batch job log for "(.*?)" file "(.*?)" has been created$/ do |landingZone, sourceFile|
  @landing_zone_path=@ingestion_lz_identifer_map[landingZone]
  @source_file_name=sourceFile
  step "a batch job log has been created"
end

Then /^I restart the activemq instance "([^"]*)" running on "([^"]*)"$/ do |instance_name, instance_source|
  Open3.popen2e("#{instance_source}/#{instance_name}/bin/#{instance_name}" )
end

Then /^I receive a JSON response$/ do
  @result = JSON.parse($healthCheckResult)
  assert(@result != nil, "Result of JSON parsing is nil")
end

Then /^the response should include (.*?)$/ do |json_values|
  valueArr = json_values.split(", ")
  for val in valueArr
    assert(@result.has_key?(val), "Values missing")
  end
end

Then /^the value of "(.*?)" should be "(.*?)"$/ do |json_variable, json_value|
  puts @result
  assert(@result[json_variable] == json_value)
end

Given /^I have checked the counts of the following collections:$/ do |table|
  @excludedCollectionHash = {}
  @db = @conn[@ingestion_db_name]
  table.hashes.map do |row|
    @excludedCollectionHash[row["collectionName"]] = @db.collection(row["collectionName"]).count()
  end
end


Then /^the following collections counts are the same:$/ do |table|
  @db = @conn[@ingestion_db_name]
  table.hashes.map do |row|
    if row["collectionName"] == "securityEvent"
      assert(@excludedCollectionHash[row["collectionName"]] <= @db.collection(row["collectionName"]).count(), "Tenant Purge has removed documents it should not have from the following collection: #{row["collectionName"]}")
    else
      assert(@excludedCollectionHash[row["collectionName"]] == @db.collection(row["collectionName"]).count(), "Tenant Purge has removed documents it should not have from the following collection: #{row["collectionName"]}")
    end
  end
end

Then /^application "(.*?)" has "(.*?)" authorized edorgs$/ do |arg1, arg2|
  @db = @conn[INGESTION_DB_NAME]
  appColl = @db.collection("application")

  application = appColl.find({"_id" => arg1})

  assert(application.count == 1, "didn't find an application")

  application.each do |app|
    numEdorg = app['body']['authorized_ed_orgs'].size
    assert(arg2.to_i == numEdorg, "there should be #{arg2} authorized edorgs, but found #{numEdorg}")
  end

end

Given /^I create a tenant set to preload data set "(.*?)" for tenant "(.*?)"$/ do |dataSet, tenant|
  step "I add a new tenant for \"#{tenant}\""
  @newTenant["body"]["landingZone"][0]["preload"]={"files" => [dataSet], "status" => "ready"}
  @landing_zone_path = @newTenant["body"]["landingZone"][0]["path"]
  @tenantColl.save(@newTenant)
end

Given /^the tenant database "(.*?)" does not exist$/ do |tenant|
    disable_NOTABLESCAN()
    @conn.drop_database(convertTenantIdToDbName(tenant))
    enable_NOTABLESCAN()
end

Then /^I should see either "(.*?)" or "(.*?)" following (.*?) in "(.*?)" file$/ do |content1, content2, logTag, logFile|
  found = false
  completeFileName = INGESTION_LOGS_DIRECTORY + '/' + 'ingestion.log'

  if (INGESTION_MODE == 'remote')
    found = searchRemoteFileForEitherContentAfterTag(content1, content2, logTag, completeFileName)
  else
    File.open(completeFileName, "r") do |infile|
      while (line = infile.gets)
        if ((line =~ /#{logTag}.*#{content1}/) or (line =~ /#{logTag}.*#{content2}/)) then
          found = true
          break
        end
      end
    end
  end
  assert(found == true, "content not found")
end

def check_records_in_collection(table, db_name)
  disable_NOTABLESCAN()
  @db   = @conn[db_name]

  @result = "true"

  table.hashes.map do |row|
    subdoc_parent = subDocParent row["collectionName"]
    if subdoc_parent
      @entity_count = runSubDocQuery(subdoc_parent, row["collectionName"], row["searchType"], row["searchParameter"], row["searchValue"])
    else
      @entity_collection = @db.collection(row["collectionName"])

      if row["searchType"] == "integer"
        @entity_count = @entity_collection.find({"$and" => [{row["searchParameter"] => row["searchValue"].to_i}]}).count().to_s
      elsif row["searchType"] == "double"
        @entity_count = @entity_collection.find({"$and" => [{row["searchParameter"] => row["searchValue"].to_f}]}).count().to_s
      elsif row["searchType"] == "boolean"
        if row["searchValue"] == "false"
          @entity_count = @entity_collection.find({"$and" => [{row["searchParameter"] => false}]}).count().to_s
        else
          @entity_count = @entity_collection.find({"$and" => [{row["searchParameter"] => true}]}).count().to_s
        end
      elsif row["searchType"] == "nil"
        @entity_count = @entity_collection.find({"$and" => [{row["searchParameter"] => nil}]}).count().to_s
      else
        @entity_count = @entity_collection.find({"$and" => [{row["searchParameter"] => row["searchValue"]}]}).count().to_s
      end
    end

    puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection for record with " + row["searchParameter"] + " = " + row["searchValue"]


    if @entity_count.to_s != row["expectedRecordCount"].to_s
      puts "Failed #{row["collectionName"]}"
      @result = "false"
      red = "\e[31m"
      reset = "\e[0m"
    end
    puts "#{red}There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection for record with " + row["searchParameter"] + " = " + row["searchValue"] + ". Expected: " + row["expectedRecordCount"].to_s + "#{reset}"

  end

  assert(@result == "true", "Some records are not found in collection.")
  enable_NOTABLESCAN()
end
def verifySubDocDid(subdoc_parent, subdoc, didId, field, value)
  @entity_collection = @db.collection(subdoc_parent)

  id_param = subdoc + "._id"
  field = subdoc + "." + field

  puts "verifySubDocDid #{id_param}, #{didId}, #{field}, #{value}"

  @entity_count = @entity_collection.find({"$and" => [{id_param => didId},{field => value}]}).count().to_s
end

Then /^I check that ids were generated properly:$/ do |table|
  disable_NOTABLESCAN()
  @db = @conn[@ingestion_db_name]
  table.hashes.map do |row|
    subdoc_parent = subDocParent row["collectionName"]

    did = row['deterministicId']
    field = row['field']
    value = row['value']
    collection = row['collectionName']

    if subdoc_parent
      @entity_count = verifySubDocDid(subdoc_parent, row["collectionName"], row['deterministicId'], row['field'], row['value'])
    else
      @entity_collection = @db.collection(collection)
      @entity_count = @entity_collection.find({"$and" => [{"_id" => did},{field => value}]}).count().to_s
    end
    assert(@entity_count == "1", "Expected 1 entity in collection #{collection} where _id = #{did} and #{field} = #{value}, found #{@entity_count}")
  end
  enable_NOTABLESCAN()
end

Then /^I check that multiple educationOrganization ids were generated properly:$/ do |table|
  disable_NOTABLESCAN()
  @db = @conn[@ingestion_db_name]
  table.hashes.map do |row|
    
    did = row['deterministicId']
    field = row['field']
    value = row['value']
    collection = row['collectionName']
    refArray = value.split(',')
  
    @entity_collection = @db.collection(collection)
    @entity_count = @entity_collection.find({"$and" => [{"_id" => did},{field => [refArray[0],refArray[1],refArray[2]]}]}).count().to_s
      
    assert(@entity_count == "1", "Expected 1 entity in collection #{collection} where _id = #{did} and #{field} = #{value}, found #{@entity_count}")
  end
  enable_NOTABLESCAN()
end


def extractField(record, fieldPath, subDocType, subDocId) 
	pathArray = fieldPath.split('.')
	result = record
	
	if subDocType
		result = result[subDocType]
		#if there is an array of subdocs, find the right one
		if result.kind_of?(Array)
			for subDoc in result
				if subDoc["_id"] == subDocId
					result = subDoc
					break
				end
			end
		end
	
	end
	
	for pathPart in pathArray
		result = result[pathPart]
		#handle arrays by always selecting the first element
		while result.kind_of?(Array)
			result = result[0]
		end
	end
	result
end

def getRecord(did, collectionName)
	db = @conn[@ingestion_db_name]
	parentCollectionName = subDocParent(collectionName)
	if parentCollectionName
		idField =  id_param = collectionName + "._id"
		collection = db.collection(parentCollectionName)
		record = collection.find_one({idField => did})
    else
    	collection = db.collection(collectionName)
		record = collection.find_one({"_id" => did})
    end
	
	record
end

Then /^I check that references were resolved correctly:$/ do |table|
	disable_NOTABLESCAN()
	table.hashes.map do |row|
		did = row['entityId']
    	refField = row['referenceField']
    	refCollectionName = row['referenceCollection']
    	collectionName = row['entityCollection']
	
		entity = getRecord(did, collectionName)
		assert(entity != nil, "Failed to find an entity with _id = #{did} in collection #{collectionName}")
	
		parentCollectionName = subDocParent(collectionName)
		if parentCollectionName
			refDid = extractField(entity, refField, collectionName, did)
		else
			refDid = extractField(entity, refField, nil, nil)
		end
		
		referredEntity = getRecord(refDid, refCollectionName)
		assert(referredEntity != nil, "Referenced #{refCollectionName} entity with _id = #{refDid} in #{collectionName} does not exist")
		
	end
	enable_NOTABLESCAN()
end


############################################################
# STEPS: AFTER
############################################################

After do
  if (!@landing_zone_path.nil? && Dir.exists?(@landing_zone_path))
    Dir.foreach(@landing_zone_path) do |entry|
      if (entry.rindex("warn.") || entry.rindex("error."))
        if File.exists?(@landing_zone_path + entry)
          STDOUT.puts "Error\/Warnings File detected = " + @landing_zone_path + entry
          STDOUT.puts "File contents follow:"
          STDOUT.puts File.open(@landing_zone_path + entry).read
        end
      end
    end
  end
  cleanTenants()
  @conn.close if @conn != nil
  @batchConn.close if @batchConn != nil
end

# Set FAILFAST=1 in the acceptance test running environment to exit the tests after
# the first feature failure

# Uncomment the following to exit after the first failing scenario in a feature file.
# Useful for debugging
#After do |scenario|
#  Cucumber.wants_to_quit = true if scenario.failed?
#end
############################################################
# END
############################################################
