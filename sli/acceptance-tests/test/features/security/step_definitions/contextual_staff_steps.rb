require 'json'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'

Transform /^data for "([^"]*)"$/ do |path|
  id = "/v1/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if path == "South Daybreak Elementary"
  id = "/v1/teachers/67ed9078-431a-465e-adf7-c720d08ef512" if path == "Linda Kim"
  id = "/v1/teachers/edce823c-ee28-4840-ae3d-74d9e9976dc5" if path == "Mark Anthony"
  id = "/v1/teachers/a060273b-3e65-4e5f-b5d1-45226f584c5d" if path == "Dale Reiss"
  id = "/v1/students/92d1a002-2695-4fb8-a0d6-4ef655d29e48" if path == "Malcolm Haehn NY"
  id = "/v1/students/5738d251-dd0b-4734-9ea6-417ac9320a15" if path == "Matt Sollars"
  id = "/v1/schools/46c2e439-f800-4aaf-901c-8cf3299658cc/studentSchoolAssociations/students" if path == "Students in Parker Elementary"
  id = "/v1/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb/studentSchoolAssociations/students" if path == "Students in South Daybreak Elementary"
  id = "/v1/sections/7295e51e-cd51-4901-ae67-fa33966478c7/studentSectionAssociations/students" if path == "Students in AP Calculus Sec 201"
  id = "/v1/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb/teacherSchoolAssociations/teachers" if path == "Teachers in South Daybreak Elementary"
  id = "/v1/schools/9d970849-0116-499d-b8f3-2255aeb69552/teacherSchoolAssociations/teachers" if path == "Teachers in Dawn Elementary"
  id = "/v1/schools?parentEducationAgencyReference=bd086bae-ee82-4cf2-baf9-221a9407ea07" if path == "Schools in Daybreak District"
  id = "/v1/schools?parentEducationAgencyReference=29b95c04-3d70-4b3a-8341-27d544a39974" if path == "Schools in Parker District"
  id
end


When /^I try to access the (data for "[^"]*") in my "[^"]*" from the API$/ do |dataPath|
  @format = "application/json"
  restHttpGet(dataPath)
end

Then /^I get the data returned in json format$/ do
  assert(@res != nil, "Did not receive a response from the API")
  assert(@res.code == 200, "Received a #{@res.code.to_s} response from the request, expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil)
end

When /^I try to access the (data for "[^"]*") in another "[^"]*" from the API$/ do |dataPath|
  @format = "application/json"
  restHttpGet(dataPath)
end

When /^I try to update the (data for "[^"]*") in another "[^"]*" from the API$/ do |dataPath|
  @format = "application/json"
  @path = dataPath
  
  objType = dataPath.split('/')[0]
  field_to_update = case objType
                    when "students" then "studentUniqueStateId"
                    when "staff" then "staffUniqueStateId"
                    when "schools" then "nameOfInstitution"
                    when "sections" then "uniqueSectionCode"
                    when "teachers" then "staffUniqueStateId"
                    end
  restHttpGet(@path)
  @originalObj = JSON.parse(@res.body)
  assert(@originalObj != nil, "Could not get the existing JSON body")
  @updatedObj = @originalObj.clone
  @updatedObj[field_to_update] = "UpdatedData#{rand(10).to_s}"
  restHttpPost(dataPath, @updatedObj)
end

Then /^the data should be updated$/ do
  restHttpGet(@path)
  current = JSON.parse(@res.body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current == @updatedObj, "Unsuccesful update to #{@path}")
end

Then /^the data should not have changed$/ do
  restHttpGet(@path)
  current = JSON.parse(@res.body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current == @originalObj, "The data should not have updated")
end

When /^I try to update the (data for "[^"]*") in my "([^"]*)" from the API$/ do |dataPath, level|
  @format = "application/json"
  @path = dataPath
  
  objType = dataPath.split('/')[0]
  field_to_update = case objType
                    when "students" then "studentUniqueStateId"
                    when "staff" then "staffUniqueStateId"
                    when "schools" then "nameOfInstitution"
                    when "sections" then "uniqueSectionCode"
                    when "teachers" then "staffUniqueStateId"
                    end
  restHttpGet(@path)
  @originalObj = JSON.parse(@res.body)
  assert(@originalObj != nil, "Could not get the existing JSON body")
  @updatedObj = @originalObj.clone
  @updatedObj[field_to_update] = "UpdatedData#{rand(10).to_s}"
  restHttpPost(dataPath, @updatedObj)
end

Given /^my "([^"]*)" is "([^"]*)"$/ do |level, name|
  # No code needed, this step is informational
end
