require 'rubygems'
require 'mongo'
require 'fileutils'

require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

INGESTION_LANDING_ZONE = PropLoader.getProps['ingestion_landing_zone']
INGESTION_DB_NAME = PropLoader.getProps['ingestion_database_name']
INGESTION_DB = PropLoader.getProps['ingestion_db']
INGESTION_SERVER_URL = PropLoader.getProps['ingestion_server_url']
INGESTION_MODE = PropLoader.getProps['ingestion_mode']

############################################################
# STEPS: GIVEN
############################################################

Given /^I am using local data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end

Given /^I am using preconfigured Ingestion Landing Zone$/ do
  @landing_zone_path = INGESTION_LANDING_ZONE
  puts "Landing Zone = " + @landing_zone_path
end

Given /^I post "([^"]*)" file as the payload of the ingestion job$/ do |file_name|
  path_name = file_name[0..-5]
  
  # copy everything into a new directory (to avoid touching git tracked files)
  zip_dir = @local_file_store_path + "temp-" + path_name + "/"
  if Dir.exists?(zip_dir)
    FileUtils.rm_r zip_dir
  end
  FileUtils.cp_r @local_file_store_path + path_name, zip_dir
  
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
  @source_file_name = file_name
  
  FileUtils.rm_r zip_dir
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

  assert(@result == "true", "Some collections were not cleared successfully.")
end

############################################################
# STEPS: WHEN
############################################################

When /^"([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end

When /^zip file is scp to ingestion landing zone$/ do
  @source_path = @local_file_store_path + @source_file_name
  @destination_path = @landing_zone_path + @source_file_name

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  if (INGESTION_MODE == 'remote')
    # copy file from external path to landing zone
    ingestion_server_string = "\"" + ("ingestion@" + INGESTION_SERVER_URL + ":" + @landing_zone_path).to_s  + "\""
    local_source_path = "\"" + @source_path.to_s + "\""

    puts "Will Execute sh: " + "scp #{local_source_path} #{ingestion_server_string}"
    runShellCommand("scp " + local_source_path + " " + ingestion_server_string)
  else
    # copy file from local filesystem to landing zone
    FileUtils.cp @source_path, @destination_path
  end

  assert(true, "File Not Uploaded")
end


############################################################
# STEPS: THEN
############################################################

Then /^I should see following map of entry counts in the corresponding collections:$/ do |table|
  @conn = Mongo::Connection.new(INGESTION_DB)
  @db   = @conn[INGESTION_DB_NAME]
  
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

Then /^I check to find if record is in collection:$/ do |table|
  @conn = Mongo::Connection.new(INGESTION_DB)
  @db   = @conn[INGESTION_DB_NAME]
  
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


Then /^I should see "([^"]*)" in the resulting batch job file$/ do |message|
  if (INGESTION_MODE == 'remote')
    #remote check of file
    @job_status_filename_component = "job-" + @source_file_name + "-"
    
    runShellCommand("chmod 755 " + File.dirname(__FILE__) + "/../../util/ingestionStatus.sh");
    @resultOfIngestion = runShellCommand(File.dirname(__FILE__) + "/../../util/ingestionStatus.sh " + @job_status_filename_component)
    puts "Showing : <" + @resultOfIngestion + ">"
    
    @messageString = message.to_s
    
    if @resultOfIngestion.include? @messageString
      assert(true, "Processed all the records.")
    else
      assert(false, "Did't process all the records.")
    end
    
  else
    @job_status_filename_component = "job-" + @source_file_name + "-"

    @job_status_filename = ""
    Dir.foreach(@landing_zone_path) do |entry|
      if (entry.rindex(@job_status_filename_component))
        # LAST ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(@landing_zone_path + @job_status_filename, "r")
    puts "STATUS FILENAME = " + @landing_zone_path + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(@landing_zone_path + @job_status_filename).join()
      puts "FILE CONTENTS = " + file_contents

      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end

    else
       raise "File " + @job_status_filename + "can't be opened"
    end
  end
end

Then /^I should see "([^"]*)" in the resulting error log file$/ do |message|
  if (INGESTION_MODE == 'remote')
    #remote check of file
    @error_filename_component = "error."
    
    runShellCommand("chmod 755 " + File.dirname(__FILE__) + "/../../util/ingestionStatus.sh");
    @resultOfIngestion = runShellCommand(File.dirname(__FILE__) + "/../../util/ingestionStatus.sh " + @error_filename_component)
    puts "Showing : <" + @resultOfIngestion + ">"
    
    @messageString = message.to_s
    
    if @resultOfIngestion.include? @messageString
      assert(true, "Processed all the records.")
    else
      assert(false, "Did't process all the records.")
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

    aFile = File.new(@landing_zone_path + @error_status_filename, "r")
    puts "STATUS FILENAME = " + @landing_zone_path + @error_status_filename
    assert(aFile != nil, "File " + @error_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(@landing_zone_path + @error_status_filename).join()
      puts "FILE CONTENTS = " + file_contents

      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end

    else
       raise "File " + @error_status_filename + "can't be opened"
    end
  end
end

############################################################
# END
############################################################