require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../common.rb'

Transform /^(Section|Student) "([^"]*)"$/ do |type, id|
  if type == "Student"
    uri = "/students/"
    uri += "TODO" if id == "Jane Doe"
    uri += "TODO" if id == "Albert Wright"
    uri += "TODO" if id == "Kevin Smith"
  elsif type == "Section"
    uri = "/sections/"
    uri += "TODO" if id == "Biology II - C"
    uri += "TODO" if id == "Foreign Language - A"
    uri += "TODO" if id == "Physics I - B"
    uri += "TODO" if id == "Chemistry I - A"
  end
  uri
end