require 'rubygems'
require 'mongo'

require_relative '../../util/scp.rb'
require_relative '../../../utils/sli_utils.rb'

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


Given /^I have landing zone configured to "([^"]*)"$/ do |path|
  @landing_zone_path = path
end

Given /^I connect to "([^"]*)" database$/ do |db_name|
  @database_name = db_name
end

Given /^I post "([^"]*)" as the payload of the ingestion job$/ do |file_name|
  @source_file_name = file_name
end


When /^zip file is scp to ingestion landing zone at "([^"]*)"$/ do |file_path|
  @source_path = @landing_zone_path + @source_file_name
  @destination_path = file_path + @source_file_name

  puts "Source = " + @source_path
  puts "Destination = " + @destination_path

  assert(@destination_path != nil, "Destination path was nil")
  assert(@source_path != nil, "Source path was nil")

  # copy file from external path to landing zone
  #scp_upload("http://localhost:8080", "username", @source_path, @destination_path, {}, {})
  
  # copy file from local filesystem to landing zone
  FileUtils.cp @source_path, @destination_path

  #check if file was copied to destination
  sleep(Integer(3))
  aFile = File.new(@destination_path, "r")
  assert(aFile != nil, "File wasn't copied successfully to destination")
  
end


When /^"([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end


Then /^I should see a file called "([^"]*)" at "([^"]*)"$/ do |filename, url|
  @destination_file_url = url
  aFile = File.new(@destination_file_url + filename, "r")
  assert(aFile != nil, "File " + filename + "doesn't exist")
end


Then /^I should see "([^"]*)" in "([^"]*)" file$/ do |message, filename|
  aFile = File.new(@destination_file_url + filename, "r")
  if aFile
    file_contents = IO.readlines(@destination_file_url + filename)
    assert(message != file_contents[0], "File doesn't contain correct processing message")
  else
     raise "File " + filename + "can't be opened"
  end
end
