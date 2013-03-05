=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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


require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = @teacher["id"]                                            if human_readable_id == "teacher id"
  id = @teacher["getSchools"]                                    if human_readable_id == "getSchools"
  id = @teacher["getSections"]                                   if human_readable_id == "getSections"
  id = @teacher["getEducationOrganizations"]                     if human_readable_id == "getEducationOrganizations"
  id = @teacher["getTeacherSchoolAssociations"]                  if human_readable_id == "getTeacherSchoolAssociations"
  id = @teacher["getTeacherSectionAssociations"]                 if human_readable_id == "getTeacherSectionAssociations"
  id = @teacher["getStaffEducationOrgAssignmentAssociations"]    if human_readable_id == "getStaffEducationOrgAssignmentAssociations"
  id = @teacher["sectionId"]                                     if human_readable_id == "teacher section list"
  id = @teacher["sectionId"][0]                                  if human_readable_id == "teacher section"
  id = @teacher["studentAssessments"]                            if human_readable_id == "student assessment list"
  id = @teacher["studentAssessments"][0]                         if human_readable_id == "student assessment"
  id
end

Given /^I am a valid teacher "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I am a valid IT Administrator "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

####################################
# WHEN
####################################
When /^I make a GET request to URI "(.*?)"$/ do |request|  
  uri = request.gsub("@id", @teacher["sectionId"][0])
  step "I navigate to GET \"/v1/#{uri}\""
end

####################################
# THEN
####################################
Then /^I should get and store the link named "(.*?)"$/ do |mylink|
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  puts "\nDEBUG: URI response body is #{@result}\n"
  found = false
  if !@result.nil? && !@result.empty?
    @result["links"].each do |link|
      if link["rel"] == mylink
        found = true
        teacherHashPush(mylink, link["href"])
      end
    end
  end
  assert(found, "Could not find the link #{mylink} in the URI Response: #{@result}")
  puts "\n\nDEBUG: @teacher is set to #{@teacher}"
end

Then /^I should get and store the "(.*?)" from the response body$/ do |field|
  assert(@result != nil, "Response contains no data")
end

Then /^the response body "(.*?)" should match my teacher "(.*?)"$/ do |resKey, teacherKey|
  puts "\n\nDEBUG: @result[#{resKey}]=#{@result[resKey]}\nDEBUG: @teacher[#{teacherKey}]=#{@teacher[teacherKey]}\n"
  assert(@result[resKey] == @teacher[teacherKey], "Expected response not found")
end

Then /^the response field "(.*?)" should be "(.*?)"$/ do |field, value|
  #puts "\n\nDEBUG: @result[#{field}]=#{@result[field]}\n"
  # dig the value for that field out of a potentially
  # dot-delimited response-body structure
  # ex: field=body.name.firstName, @result=[json response body]
  result = fieldExtract(field, @result)
  assert(result == value, "Unexpected response: #{result}")  
end

Then /^I should extract the "(.*?)" id from the "(.*?)" URI$/ do |resource, link|
  value = @teacher[link].match(/#{resource}\/(.*?_id)/)
  puts "\n\nDEBUG: The full match is #{value}"
  teacherHashPush("id", $1)
end

Then /^I should extract the "(.*?)" from the response body to a list$/ do |resource|
  #value = @teacher[link].match(/#{resource}\/(.*?_id)/)
  values = Array.new
  @result.each do |response|
    values << fieldExtract(resource, response)
  end
  teacherHashPush(resource, values)
  puts "\n\nDEBUG: Teacher hash for key #{resource} is now: #{@teacher[resource]}"
  puts "\n\nDEBUG: Teacher hash for FIRST key #{resource} is now: #{@teacher[resource][0]}"
end

Then /^I should extract the "(.*?)" from the response body to a list and save to "(.*?)"$/ do |resource, entity|
  #value = @teacher[link].match(/#{resource}\/(.*?_id)/)
  values = Array.new
  @result.each do |response|
    values << fieldExtract(resource, response)
  end
  teacherHashPush(entity, values)
  puts "\n\nDEBUG: Teacher hash for key #{entity} is now: #{@teacher[resource]}"
end

Then /^I store the studentAssessments$/ do
  puts "\n\nDEBUG: Storing #{@result.length} studentAssessments"
  ids = Array.new

  @result.each do |studentAssessment|
    ids << studentAssessment["id"]
    teacherHashPush(studentAssessment["id"], studentAssessment)
  end
  # Push the list of studentAsessment hash keys to a list in @teacher
  teacherHashPush("studentAssessments", ids)
  puts "\n\nDEBUG: First studentAssessment in @teacher is #{@teacher[@teacher["studentAssessments"][0]]}"
end


def fieldExtract(field, body)
  #split uri 'r' into URI segments
  r = field.split(".")
  #parse the response field value based on how deep that field is embedded
    puts "\n\nDEBUG: field is set to: #{field}"
    puts "\nDEBUG: body is set to: #{body}"
  result = body[r[0]] if r.length == 1
  result = body[r[0]][r[1]] if r.length == 2
  result = body[r[0]][r[1]][r[2]] if r.length == 3
  result = body[r[0]][r[1]][r[2]][r[3]] if r.length == 4
  result = body[r[0]][r[1]][r[2]][r[3]][r[4]] if r.length == 5
  puts "\n\nDEBUG: r is #{r}, result is #{result}"
  return result
end

# Build the teacher hash
def teacherHashPush(key, value)
  @teacher = Hash.new unless defined? @teacher
  @teacher[key] = value
end

def getTeacherSchools()

end

def pushStudentAssessment(studentAssessment)
  @teacher[studentAssessment["id"]] = studentAssessment
end


# Build the section array
def sectionArray(value)
  @sections = Array.new unless defined? @sections  
  if value.is_a?(Array)
    value.each{|section| @sections << section}
  else
    @sections << value
  end
end

# Build the section array
def studentArray(value)
  @students = Array.new unless defined? @students
  # 
  if value.is_a?(Array)
    value.each{|student| @students << student}
  else
    @students << value
  end
end


