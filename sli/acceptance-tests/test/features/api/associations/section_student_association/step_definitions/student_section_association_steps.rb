require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../common.rb'

Transform /^(Section|Student) "([^"]*)"$/ do |type, id|
  puts "called with type = #{type} and id = #{id}"
  if type == "Student"
    uri = "TODO" if id == "Jane Doe"
    uri = "TODO" if id == "Albert Wright"
    uri = "TODO" if id == "Kevin Smith"
  elsif type == "Section"
    uri = "TODO" if id == "Biology II - C"
    uri = "TODO" if id == "Foreign Language - A"
    uri = "TODO" if id == "Physics I - B"
    uri = "TODO" if id == "Chemistry I - A"
  end
  uri
end

Transform /^Student Section Association for Student "([^"]*)" and Section "([^"]*)"$/ do |student, section|
  puts "called with student:#{student} and section #{section}"
  uri = "TODO" if student == "Jane Doe" and section == "Biology II - C"
  uri
end

#vnd.slc+json format is not ready for testing
#remove this transform to switch to new format
Transform /^application\/vnd\.slc\+json$/ do |args|
  puts 'transform to application/json was called'
  "application/json"
end

#Givens

Given /^"([^"]*)" is ((Student|Section) "[^"]*")$/ do |key, value, student_or_section|
  puts "key = #{key}"
  puts "student_or_section = #{student_or_section}"
  puts "value = #{value}"
  
  step '"' + key + '" is "' + value + '"'
end

#Whens

When /^I navigate to GET (Student Section Association for Student "[^"]*" and Section "[^"]*")$/ do |uri|
  puts "uri = #{uri}"
  step 'I navigate to GET "/student-section-associations/' + uri + '"'
end