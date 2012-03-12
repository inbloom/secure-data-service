require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'


Transform /^<([^"]*)>$/ do |human_readable_id|

  id = "6c572483-fe75-421c-9588-d82f1f5f3af5"       if human_readable_id == "Writing Advanced Placement Test ID"
  id = "6a53f63e-deb8-443d-8138-fc5a7368239c"       if human_readable_id == "Writing Achievement Assessment Test ID"

  id = "714c1304-8a04-4e23-b043-4ad80eb60992"       if human_readable_id == "Alfonso ID"
  id = "e1af7127-743a-4437-ab15-5b0dacd1bde0"       if human_readable_id == "Priscilla ID"
  id = "61f13b73-92fa-4a86-aaab-84999c511148"       if human_readable_id == "Alden ID"
  id = "289c933b-ca69-448c-9afd-2c5879b7d221"       if human_readable_id == "Donna ID"
  id = "c7146300-5bb9-4cc6-8b95-9e401ce34a03"       if human_readable_id == "Rachel ID"
  id = "fa45033c-5517-b14b-1d39-c9442ba95782"       if human_readable_id == "Macey ID"
  id = "e24b24aa-2556-994b-d1ed-6e6f71d1be97"       if human_readable_id == "Quemby ID"
  id = "1e1cdb04-2094-46b7-8140-e3e481013480"       if human_readable_id == "Chemistry F11 ID"
  id = "5c4b1a9c-2fcd-4fa0-b21c-f867cf4e7431"       if human_readable_id == "Physics S08 ID"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2"       if human_readable_id == "Apple Alternative Elementary School ID"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"       if human_readable_id == "Biology High School ID"
  id = "244520d2-8c6b-4a1e-b35e-d67819ec0211"       if human_readable_id == "Ms. Jones ID"
  id = "8e5b2d0e-959c-42ef-b3df-9b83cba85a33"       if human_readable_id == "Mr. Smith ID"
  id = "a249d5d9-f149-d348-9b10-b26d68e7cb9c"       if human_readable_id == "Mrs. Solis ID"


  id = "assessments"                                if human_readable_id == "ASSESSMENT URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id = "teachers"                                   if human_readable_id == "TEACHER URI"
  id = "schools"                                    if human_readable_id == "SCHOOL URI"
  id = "sections"                                   if human_readable_id == "SECTION URI"
  id = "studentAssessmentAssociations"              if human_readable_id == "STUDENT ASSESSMENT ASSOC URI"
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
