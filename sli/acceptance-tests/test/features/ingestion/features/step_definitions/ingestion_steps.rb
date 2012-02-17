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
INGESTION_MODE = ENV['ingestion_mode']

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
  @source_file_name = file_name
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
    @entity_count = @entity_collection.find({row["searchParameter"] => row["searchValue"]}).count().to_s
    
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

############################################################
# END
############################################################