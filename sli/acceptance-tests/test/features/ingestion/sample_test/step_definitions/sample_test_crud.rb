require 'rubygems'
require 'mongo'

require_relative '../../util/scp.rb'
require_relative '../../../utils/sli_utils.rb'


if (ENV['ingestion_landing_zone'])
  INGESTION_LANDING_ZONE = ENV['ingestion_landing_zone']
else
  INGESTION_LANDING_ZONE = PropLoader.getProps['ingestion_landing_zone']
end 


Given /^there are no students in DS$/ do
  @conn = Mongo::Connection.new
  @db   = @conn[@database_name]
  @student_col = @db['student']

  @student_col.remove

  puts "There are #{@student_col.count} records."
  if @student_col.count > 0
    puts "These are the student records:"
    @student_col.find.each { |doc| puts doc.inspect }
  end
end


Given /^I am using preconfigured Ingestion Landing Zone$/ do
  @landing_zone_path = INGESTION_LANDING_ZONE
  puts "Landing Zone = " + @landing_zone_path
end


Given /^I am using local data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../sample_data/'
end


Given /^I connect to "([^"]*)" database$/ do |db_name|
  @database_name = db_name
end


Given /^I post "([^"]*)" as the payload of the ingestion job$/ do |file_name|
  @source_file_name = file_name
end


When /^zip file is scp to ingestion landing zone$/ do
  @source_path = @local_file_store_path + @source_file_name
  @destination_path = @landing_zone_path + @source_file_name

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  # copy file from external path to landing zone
  #scp_upload("http://localhost:8080", "username", @source_path, @destination_path, {}, {})
  
  # copy file from local filesystem to landing zone
  FileUtils.cp @source_path, @destination_path

  #check if file was copied to destination
  #sleep(Integer(3))
  #aFile = File.new(@destination_path, "r")
  #assert(aFile != nil, "File wasn't copied successfully to destination")
  
end


When /^"([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end


Then /^I should see "([^"]*)" entries in the student collection$/ do |record_count|
  @conn = Mongo::Connection.new
  @db   = @conn[@database_name]
  @student_col = @db.collection('student')
  @student_count = @student_col.count().to_i

  puts "There are " + @student_count.to_s + " in student collection"
  
  assert((record_count).eql?(@student_count.to_s), "Record count doesn't match")
end


Then /^I should see "([^"]*)" in the resulting batch job file$/ do |message|
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


