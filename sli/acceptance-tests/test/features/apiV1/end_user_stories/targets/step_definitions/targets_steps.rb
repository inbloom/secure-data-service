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


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'


Transform /^<([^"]*)>$/ do |human_readable_id|

  id = "2108c0c84ca6998eb157e1efd4d894746e1fdf8b_id"       if human_readable_id == "SAT ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id"    if human_readable_id == "Marvin ID"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15_id"    if human_readable_id == "Matt Sollars ID"
  id = "bcfcc33f-f4a6-488f-baee-b92fbd062e8d"       if human_readable_id == "Braverman ID"
  id = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id"       if human_readable_id == "Homeroom ID"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"       if human_readable_id == "South Daybreak Elementary ID"

  id = "assessments"                                if human_readable_id == "ASSESSMENT URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id = "teachers"                                   if human_readable_id == "TEACHER URI"
  id = "schools"                                    if human_readable_id == "SCHOOL URI"
  id = "sections"                                   if human_readable_id == "SECTION URI"
  id = "studentAssessments"			                if human_readable_id == "STUDENT ASSESSMENT ASSOC URI"
  id = "teacherSectionAssociations"                 if human_readable_id == "TEACHER SECTION ASSOC URI"
  id = "teacherSchoolAssociations"                  if human_readable_id == "TEACHER SCHOOL ASSOC URI"

  id = @newId                                       if human_readable_id == "NEWLY CREATED ENTITY ID"

  #return the translated value
  id
end

Then /^I should have a list of (\d+) "([^"]*)" entities$/ do |size, entityType|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")

  @ids = Array.new
  @result.each do |entity|
    assert(entity["entityType"] == entityType)
    @ids.push(entity["id"])
  end

  assert(@ids.size.to_s == size, "Got " + @ids.size.to_s + " entities, expected " + size.to_s + " in response.")
end

Then /^I should have an entity with ID "([^"]*)"$/ do |entityId|
  found = false
  @ids.each do |id|
    if entityId == id
      found = true
    end
  end
  
  assert(found, "Entity id #{entityId} was not found")
end
