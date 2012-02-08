require 'rubygems'
require 'mongo'
require 'fileutils'

require_relative '../../util/scp.rb'
require_relative '../../../utils/sli_utils.rb'


if (ENV['ingestion_landing_zone'])
  INGESTION_LANDING_ZONE = ENV['ingestion_landing_zone']
else
  INGESTION_LANDING_ZONE = PropLoader.getProps['ingestion_landing_zone']
end

if (ENV['ingestion_db'])
  INGESTION_DB = ENV['ingestion_db']
else
  INGESTION_DB = PropLoader.getProps['ingestion_db']
end

if (ENV['ingestion_server_url'])
  INGESTION_SERVER_URL = ENV['ingestion_server_url']
else
  INGESTION_SERVER_URL = PropLoader.getProps['ingestion_server_url']
end

if (ENV['ingestion_mode'])
  INGESTION_MODE = ENV['ingestion_mode']
else
  INGESTION_MODE = 'remote'
end

Given /^there are none of this type of entity in the DS$/ do
  @conn = Mongo::Connection.new(INGESTION_DB)
  @db   = @conn[@database_name]
  @entity_collection = @db[@entity_type]

  @entity_collection.remove

  puts "There are #{@entity_collection.count} records."
  if @entity_collection.count > 0
    puts "These are the @entity_type records:"
    @entity_collection.find.each { |doc| puts doc.inspect }
  end
end


Given /^I am using preconfigured Ingestion Landing Zone$/ do
  @landing_zone_path = INGESTION_LANDING_ZONE
  puts "Landing Zone = " + @landing_zone_path
end


Given /^I am using local data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end


Given /^I connect to "([^"]*)" database$/ do |db_name|
  @database_name = db_name
end


Given /^I post "([^"]*)" as the payload of the ingestion job$/ do |file_name|
  @source_file_name = file_name
end

Given /^the payload contains entities of type "([^"]*)"$/ do |entity_type|
  @entity_type = entity_type
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

end


When /^"([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end


Then /^I should see "([^"]*)" entries in the corresponding collection$/ do |record_count|
  @conn = Mongo::Connection.new(INGESTION_DB)
  @db   = @conn[@database_name]
  @entity_collection = @db.collection(@entity_type)
  @entity_count = @entity_collection.count().to_i

  puts "There are " + @entity_count.to_s + " in " + @entity_type.to_s + " collection"

  assert((record_count).eql?(@entity_count.to_s), "Record count doesn't match")
end


Then /^I should see "([^"]*)" in the resulting batch job file$/ do |message|
  if (INGESTION_MODE == 'remote')
    #TODO - remote check of file
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


