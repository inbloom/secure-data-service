require 'rubygems'
require 'mongo'
require 'fileutils'
require 'socket'
require 'net/sftp'

require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

INGESTION_DB_NAME = PropLoader.getProps['ingestion_database_name']
INGESTION_DB = PropLoader.getProps['ingestion_db']
INGESTION_BATCHJOB_DB_NAME = PropLoader.getProps['ingestion_batchjob_database_name']
INGESTION_SERVER_URL = PropLoader.getProps['ingestion_server_url']
INGESTION_MODE = PropLoader.getProps['ingestion_mode']
INGESTION_DESTINATION_DATA_STORE = PropLoader.getProps['ingestion_destination_data_store']
INGESTION_USERNAME = PropLoader.getProps['ingestion_username']
INGESTION_REMOTE_LZ_PATH = PropLoader.getProps['ingestion_remote_lz_path']

############################################################
# STEPS: BEFORE
############################################################

Before do

  @conn = Mongo::Connection.new(INGESTION_DB)
  @conn.drop_database(INGESTION_BATCHJOB_DB_NAME)

  @mdb = @conn.db(INGESTION_DB_NAME)
  @tenantColl = @mdb.collection('tenant')

   
  #remove all tenants other than NY and IL
  @tenantColl.find.each do |row|
    if row['body'] == nil
      puts "removing record"
      @tenantColl.remove(row)
    else
      if row['body']['tenantId'] != 'NY' and row['body']['tenantId'] != 'IL'
        puts "removing record"
        @tenantColl.remove(row)
      end
    end
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

      #in remote trim the path to a relative user path rather than absolute path
      if INGESTION_MODE == 'remote'
        path = path.gsub(INGESTION_REMOTE_LZ_PATH, "")
      end

      identifier = @tenantId + '-' + educationOrganization
      puts identifier + " -> " + path
      @ingestion_lz_identifer_map[identifier] = path
      
      if !File.directory?(path)
        FileUtils.mkdir_p(path)
      end
      
    end
  end

  initializeTenants()
end

def initializeTenants()
  @lzs_to_remove  = Array.new

  defaultLz = @ingestion_lz_identifer_map['IL-Daybreak']
  assert(defaultLz != nil, "Default landing zone not defined (IL-Daybreak)")

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
    else
      createRemoteDirectory(@tenantTopLevelLandingZone)
    end
  end
end

def cleanTenants()
  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.remove("type" => "tenantTest")

  @lzs_to_remove.each do |lz_key|
    tenant = lz_key
    edOrg = lz_key

    # split tenant from edOrg on hyphen
    if lz_key.index('-') > 0
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
end

############################################################
# REMOTE INGESTION FUNCTIONS
############################################################

def remoteLzCopy(srcPath, destPath)
	Net::SFTP.start(INGESTION_SERVER_URL, INGESTION_USERNAME, :password => @password) do |sftp|
		puts "attempting to remote copy " + srcPath + " to " + destPath
		sftp.upload(srcPath, destPath)
    end
end

def clearRemoteLz(landingZone)
	
	puts "clear landing zone " + landingZone
	
	Net::SFTP.start(INGESTION_SERVER_URL, INGESTION_USERNAME, :password => @password) do |sftp|
		sftp.dir.foreach(landingZone) do |entry|
			next if entry.name == '.' or entry.name == '..'
			
			entryPath = File.join(landingZone, entry.name)
			
			if !sftp.stat!(entryPath).directory?
				sftp.remove!(entryPath)
			end
		end
	end
end

def remoteLzContainsFile(pattern, landingZone)
	puts "remoteLzContainsFiles(" + pattern + " , " + landingZone + ")"

	Net::SFTP.start(INGESTION_SERVER_URL, INGESTION_USERNAME, :password => @password) do |sftp|
		sftp.dir.glob(landingZone, pattern) do |entry|
			return true
		end
	end
	return false
end

def remoteLzContainsFiles(pattern, targetNum , landingZone)
	puts "remoteLzContainsFiles(" + pattern + ", " + targetNum + " , " + landingZone + ")"

	count = 0
	Net::SFTP.start(INGESTION_SERVER_URL, INGESTION_USERNAME, :password => @password) do |sftp|
		sftp.dir.glob(landingZone, pattern) do |entry|
			count += 1
			if count >= targetNum
				return true
			end
		end
	end
	return false
end

def remoteFileContainsMessage(prefix, message, landingZone)

	puts "remoteFileContainsMessage prefix " + prefix + ", message " + message + ", landingZone " + landingZone
	Net::SFTP.start(INGESTION_SERVER_URL, INGESTION_USERNAME, :password => @password) do |sftp|
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

def createRemoteDirectory(dirPath)
	puts "attempting to create dir: " + dirPath

	Net::SFTP.start(INGESTION_SERVER_URL, INGESTION_USERNAME, :password => @password) do |sftp|
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
  initializeLandingZone(@ingestion_lz_identifer_map['IL-Daybreak'])
end

Given /^I am using preconfigured Ingestion Landing Zone for "([^"]*)"$/ do |lz_key|
  lz = @ingestion_lz_identifer_map[lz_key]
  initializeLandingZone(lz)
end

def initializeLandingZone(lz)
  if lz.rindex('/') == (lz.length - 1)
    @landing_zone_path = lz
  else
    @landing_zone_path = lz+ '/'
  end

  @landing_zone_path = lz
  puts "Landing Zone = " + @landing_zone_path

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

Given /^the following collections are empty in datastore:$/ do |table|
  @conn = Mongo::Connection.new(INGESTION_DB)
  
  @db   = @conn[INGESTION_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db[row["collectionName"]]
    @entity_collection.remove

    puts "There are #{@entity_collection.count} records in collection " + row["collectionName"] + "."

    if @entity_collection.count.to_s != "0"
      @result = "false"
    end
  end
  createIndexesOnDb(@conn,INGESTION_DB_NAME)
  assert(@result == "true", "Some collections were not cleared successfully.")
end

Given /^the following collections are empty in batch job datastore:$/ do |table|
  @db   = @conn[INGESTION_BATCHJOB_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db[row["collectionName"]]
    @entity_collection.remove

    puts "There are #{@entity_collection.count} records in collection " + row["collectionName"] + "."

    if @entity_collection.count.to_s != "0"
      @result = "false"
    end
  end
  assert(@result == "true", "Some collections were not cleared successfully.")
end

def createIndexesOnDb(db_connection,db_name)

  @db = db_connection[db_name]
  ensureIndexes(@db)

end

def ensureIndexes(db)

  @collection = @db["assessment"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["attendance"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["course"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["educationOrganization"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["gradebookEntry"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["parent"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["school"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["section"]
  @collection.save( {'metaData' => {'externalId' => " ", 'tenantId' => " "}, 'body' => {'schoolId' => " ", 'courseId' => " "}} )
  @collection.ensure_index([ ['body.schoolId', 1], ['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.ensure_index([ ['body.courseId', 1], ['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove( {'metaData' => {'externalId' => " ", 'tenantId' => " "}, 'body' => {'schoolId' => " ", 'courseId' => " "}} )

  @collection = @db["session"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["staff"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["staffEducationOrganizationAssociation"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["student"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["studentAssessmentAssociation"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["studentParentAssociation"]
  @collection.save( {'metaData' => {'tenantId' => " "}, 'body' => {'parentId' => " ", 'studentId' => " "}} )
  @collection.ensure_index([ ['body.parentId', 1], ['body.studentId', 1], ['metaData.externalId', 1]])
  @collection.remove( {'metaData' => {'tenantId' => " "}, 'body' => {'parentId' => " ", 'studentId' => " "}} )

  @collection = @db["studentSchoolAssociation"]
  @collection.save( {'metaData' => {'externalId' => " ", 'tenantId' => " "}, 'body' => {'schoolId' => " ", 'studentId' => " "}} )
  @collection.ensure_index([ ['body.schoolId', 1], ['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.ensure_index([ ['body.studentId', 1], ['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove( {'metaData' => {'externalId' => " ", 'tenantId' => " "}, 'body' => {'schoolId' => " ", 'studentId' => " "}} )

  @collection = @db["studentSectionAssociation"]
  @collection.save( {'metaData' => {'externalId' => " ", 'tenantId' => " "}, 'body' => {'sectionId' => " ", 'studentId' => " "}} )
  @collection.ensure_index([ ['body.sectionId', 1], ['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.ensure_index([ ['body.studentId', 1], ['metaData.tenantId', 1], ['body.sectionId', 1]])
  @collection.ensure_index([ ['body.studentId', 1], ['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove( {'metaData' => {'externalId' => " ", 'tenantId' => " "}, 'body' => {'sectionId' => " ", 'studentId' => " "}} )

  @collection = @db["studentSectionGradebookEntry"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["studentTranscriptAssociation"]
  @collection.save( {'metaData' => {'tenantId' => " "}, 'body' => {'courseId' => " ", 'studentId' => " "}} )
  @collection.ensure_index([ ['body.studentId', 1], ['metaData.tenantId', 1], ['body.courseId', 1]])
  @collection.remove( {'metaData' => {'tenantId' => " "}, 'body' => {'courseId' => " ", 'studentId' => " "}} )

  @collection = @db["teacher"]
  @collection.save({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })
  @collection.ensure_index([['metaData.tenantId', 1], ['metaData.externalId', 1]])
  @collection.remove({ 'metaData' => {'externalId' => " ", 'tenantId' => " "} })

  @collection = @db["teacherSectionAssociation"]
  @collection.save( {'metaData' => {'tenantId' => " "}, 'body' => {'teacherId' => " ", 'sectionId' => " "}} )
  @collection.ensure_index([ ['body.teacherId', 1], ['metaData.tenantId', 1], ['body.sectionId', 1]])
  @collection.remove( {'metaData' => {'tenantId' => " "}, 'body' => {'teacherId' => " ", 'sectionId' => " "}} )

end

Given /^I add a new tenant for "([^"]*)"$/ do |lz_key|

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
  else
    createRemoteDirectory(path)
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
  if lz_key.index('-') > 0
    tenant = lz_key[0, lz_key.index('-')]
    edOrg = lz_key[lz_key.index('-') + 1, lz_key.length]
  end

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
    "_id" => BSON::Binary.new("HE9cAZldKXq5uZ==", BSON::Binary::SUBTYPE_UUID),
    "type" => "tenantTest",
    "body" => @body,
    "metaData" => @metaData
  }

  @db = @conn[INGESTION_DB_NAME]
  @tenantColl = @db.collection('tenant')
  @tenantColl.save(@newTenant)

  @ingestion_lz_identifer_map[lz_key] = path + '/'
  @lzs_to_remove.push(lz_key)
end

Given /^I add a new landing zone for "([^"]*)"$/ do |lz_key|
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
  else
    createRemoteDirectory(path)
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

############################################################
# STEPS: WHEN
############################################################


When /^"([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end

def dirContainsBatchJobLog?(dir)
  Dir.foreach(dir) do |file|
    if /^job-#{@source_file_name}.*.log$/.match file
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
  @maxTimeout = limit.to_i
end

When /^a batch job log has been created$/ do
  intervalTime = 3 #seconds
  #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 900
  iters = (1.0*@maxTimeout/intervalTime).ceil
  found = false
  if (INGESTION_MODE == 'remote')
    iters.times do |i|
      
      if remoteLzContainsFile("job-#{@source_file_name}*.log", @landing_zone_path)
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
      if dirContainsBatchJobLog? @landing_zone_path
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
    assert(false, "Either batch log was never created, or it took more than #{@maxTimeout} seconds")
  end

end

When /^a batch job for file "([^"]*)" is completed in database$/ do |batch_file|
  
  @db   = @conn[INGESTION_BATCHJOB_DB_NAME]
  @entity_collection = @db.collection("newBatchJob")

  #db.newBatchJob.find({"stages" : {$elemMatch : {"chunks.0.stageName" : "JobReportingProcessor" }} }).count()

  intervalTime = 5 #seconds
  #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 900
  iters = (1.0*@maxTimeout/intervalTime).ceil
  found = false
  if (INGESTION_MODE == 'remote')
    iters.times do |i|
      @entity_count = @entity_collection.find({"resourceEntries.0.resourceId" => batch_file, "stages" => {"$elemMatch" => {"chunks.0.stageName" => "JobReportingProcessor"}}}).count().to_s 

      if @entity_count.to_s == "1"
        puts "Ingestion took approx. #{(i+1)*intervalTime} seconds to complete"
        found = true
        break
      else
        sleep(intervalTime)
      end
    end
  else
    sleep(5) # waiting to poll job file removes race condition (windows-specific)
    iters.times do |i|

      @entity_count = @entity_collection.find({"resourceEntries.0.resourceId" => batch_file, "stages" => {"$elemMatch" => {"chunks.0.stageName" => "JobReportingProcessor"}}}).count().to_s

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
    assert(false, "Either batch log was never created, or it took more than #{@maxTimeout} seconds")
  end
end

When /^two batch job logs have been created$/ do
  intervalTime = 3 #seconds
  #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 900
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
    sleep(2) # give JobReportingProcessor time to finish the job
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

def checkForBatchJobLog(landing_zone)
  puts "checkForBatchJobLog"
  intervalTime = 3 #seconds
  #If @maxTimeout set in previous step def, then use it, otherwise default to 240s
  @maxTimeout ? @maxTimeout : @maxTimeout = 420
  iters = (1.0*@maxTimeout/intervalTime).ceil
  found = false
  if (INGESTION_MODE == 'remote')
    puts "checkForBatchJobLog 2"
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
    sleep(3) # waiting to poll job file removes race condition (windows-specific)
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

  if found
    sleep(2) # give JobReportingProcessor time to finish the job
    assert(true, "")
  else
    assert(false, "Either batch log was never created, or it took more than #{@maxTimeout} seconds")
  end
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

  assert(true, "File Not Uploaded")
end

When /^zip file is scp to ingestion landing zone$/ do
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


When /^local zip file is moved to ingestion landing zone$/ do
  scpFileToLandingZone @source_file_name
  
  assert(true, "File Not Uploaded")
end

############################################################
# STEPS: THEN
############################################################
Then /^I should see following map of indexes in the corresponding collections:$/ do |table|
  @db   = @conn[INGESTION_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])
    @indexcollection = @db.collection("system.indexes")
    #puts "ns" + INGESTION_DB_NAME+"student," + "name" + row["index"].to_s
    @indexCount = @indexcollection.find("ns" => INGESTION_DB_NAME + "." + row["collectionName"], "name" => row["index"]).to_a.count()

    #puts "Index Count = " + @indexCount.to_s

    if @indexCount.to_s == "0"
      puts "Index was not created for " + INGESTION_DB_NAME+ "." + row["collectionName"] + + " with name = " + row["index"]
      @result = "false"
    end
  end

  assert(@result == "true", "Some indexes were not created successfully.")

end

Then /^I should see following map of entry counts in the corresponding collections:$/ do |table|

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])
    @entity_count = @entity_collection.count().to_i

    if @entity_count.to_s != row["count"].to_s
      @result = "false"
      red = "\e[31m"
      reset = "\e[0m"
    end

    puts "#{red}There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection. Expected: " + row["count"].to_s+"#{reset}"
  end

  assert(@result == "true", "Some records didn't load successfully.")
end

Then /^I should see following map of entry counts in the corresponding batch job db collections:$/ do |table|
  @db   = @conn[INGESTION_BATCHJOB_DB_NAME]

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
end

Then /^I should say that we started processing$/ do
  puts "Ingestion Performance Dataset started Ingesting.  Please wait a few hours for it to complete."
  assert(true, "Some records didn't load successfully.")
end

Then /^I check to find if record is in collection:$/ do |table|
  @db   = @conn[INGESTION_DB_NAME]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])

    if row["searchType"] == "integer"
      @entity_count = @entity_collection.find({row["searchParameter"] => row["searchValue"].to_i}).count().to_s
    elsif row["searchType"] == "boolean"
        if row["searchValue"] == "false"
            @entity_count = @entity_collection.find({row["searchParameter"] => false}).count().to_s
        else
            @entity_count = @entity_collection.find({row["searchParameter"] => true}).count().to_s
        end
    else
      @entity_count = @entity_collection.find({row["searchParameter"] => row["searchValue"]}).count().to_s
    end

    puts "There are " + @entity_count.to_s + " in " + row["collectionName"] + " collection for record with " + row["searchParameter"] + " = " + row["searchValue"]

    if @entity_count.to_s != row["expectedRecordCount"].to_s
      @result = "false"
    end
  end

  assert(@result == "true", "Some records are not found in collection.")
end

Then /^I check to find if record is in batch job collection:$/ do |table|
  @db   = @conn[INGESTION_BATCHJOB_DB_NAME]

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
end


Then /^I find a\(n\) "([^"]*)" record where "([^"]*)" is equal to "([^"]*)"$/ do |collection, field, value|
  @db = @conn[INGESTION_DB_NAME]
  @entity_collection = @db.collection(collection)
  @entity =  @entity_collection.find({field => value})
  assert(@entity.count == 1, "Found more than one document with this query (or zero :) )")

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
  @entity.each do |ent|
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

    runShellCommand("chmod 755 " + File.dirname(__FILE__) + "/../../util/ingestionStatus.sh");
    @resultOfIngestion = runShellCommand(File.dirname(__FILE__) + "/../../util/ingestionStatus.sh " + prefix)
    #puts "Showing : <" + @resultOfIngestion + ">"

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
  if (INGESTION_MODE == 'remote') 
    if remoteLzContainsFile("error.*", @landing_zone_path)
      assert(false, "Error files created.")
    else
      assert(true, "No error files created.")
    end

  else
    @error_filename_component = "error."

    @error_status_filename = ""
    Dir.foreach(@landing_zone_path) do |entry|
      if (entry.rindex(@error_filename_component))
        # LAST ENTRY IS OUR FILE
        @error_status_filename = entry
      end
    end

    puts "STATUS FILENAME = " + @landing_zone_path + @error_status_filename
    assert(@error_status_filename == "", "File " + @error_status_filename + " exists")
  end
end

Then /^I should not see an error log file created for "([^\"]*)"$/ do |lz_key|
  lz = @ingestion_lz_identifer_map[lz_key]
  checkForErrorLogFile(lz)
end

def checkForErrorLogFile(landing_zone)
  if (INGESTION_MODE == 'remote')
    if remoteLzContainsFile("error.*", landing_zone)
      assert(true, "No error files created.")
    else
      assert(false, "Error files created.")
    end

  else
    @error_filename_component = "error."

    @error_status_filename = ""
    Dir.foreach(landing_zone) do |entry|
      if (entry.rindex(@error_filename_component))
        # LAST ENTRY IS OUR FILE
        @error_status_filename = entry
      end
    end

    puts "STATUS FILENAME = " + landing_zone + @error_status_filename
    assert(@error_status_filename == "", "File " + @error_status_filename + " exists")
  end
end

Then /^I find a record in "([^\"]*)" with "([^\"]*)" equal to "([^\"]*)"$/ do |collection, searchTerm, value|
  db = @conn[INGESTION_DB_NAME]
  collection = db.collection(collection)

  @record = collection.find_one({searchTerm => value})
  @record.should_not == nil
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
  @db   = @conn[INGESTION_BATCHJOB_DB_NAME]
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

############################################################
# STEPS: BEFORE
############################################################

After do
  cleanTenants()
  @conn.close if @conn != nil
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
