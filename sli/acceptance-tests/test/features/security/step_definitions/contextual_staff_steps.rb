require 'json'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'

Transform /^data for "([^"]*)"$/ do |path|
  id = "/schools/PUT MONGO ID HERE" if path == "South Daybreak Elementary"
  id = "/teachers/PUT MONGO ID HERE" if path == "Linda Kim"
  id = "/teachers/PUT MONGO ID HERE" if path == "Mark Anthony"
  id = "/staff/PUT MONGO ID HERE" if path == "Dale Reiss"
  id = "/students/PUT MONGO ID HERE" if path == "Malcom Haehn"
  id = "/students/PUT MONGO ID HERE" if path == "Matt Sollars"
  id = "/students?schoolId=mongo-id-here" if path == "Students in Parker Elementary"
  id = "/students?schoolId=mongo-id-here" if path == "Students in South Daybreak Elementary"
  id = "/students?sectionId=mongo-id-here" if path == "Students in AP Calculus Sec 201"
  id = "/teachers?schoolId=mongo-id-here" if path == "Teachers in South Daybreak Elementary"
  id = "/teachers?schoolId=mongo-id-here" if path == "Teachers in Dusk Elementary"
  id = "/schools?edOrgId=mongo-id-here" if path == "Schools in Daybreak District"
  id = "/schools?edOrgId=mongo-id-here" if path == "Schools in Parker District"
  id
end


When /^I try to access the (data for "[^"]*") in my "([^"]*)" from the API$/ do |dataPath, level|
  @res = restHttpGet(dataPath)
end

Then /^I get the data returned in json format$/ do
  assert(@res.code == 200, "Received a #{@res.code} response from the request, expected 200")
  assert(@res != nil, "Did not receive a response from the API")
  result = JSON.parse(@res.body)
  assert(result != nil)
end

When /^I try to access the (data for "[^"]*") in another "([^"]*)" from the API$/ do |dataPath, level|
  @res = restHttpGet(dataPath)
end

When /^I try to update the (data for "[^"]*") in another "([^"]*)" from the API$/ do |dataPath, level|
  @path = dataPath
  
  objType = dataPath.split('/')[0]
  field_to_update = case objType
                    when "students" then "studentUniqueStateId"
                    when "staff" then "staffUniqueStateId"
                    when "schools" then "nameOfInstitution"
                    when "sections" then "uniqueSectionCode"
                    when "teachers" then "staffUniqueStateId"
                    end
  
  @originalObj = JSON.parse(restHttpGet(@path).body)
  assert(@originalObj != nil, "Could not get the existing JSON body")
  @updatedObj = @originalObj.clone
  @updatedObj[field_to_update] = "UpdatedData"
  @res = restHttpPost(dataPath, @updatedObj)
end

Then /^the data should be updated$/ do
  current = JSON.parse(restHttpGet(@path).body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current == @updatedObj, "Unsuccesful update to #{@path}")
end

Then /^the data should not have changed$/ do
  current = JSON.parse(restHttpGet(@path).body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current == @originalObj, "The data should not have updated")
end

When /^I try to update the data for "([^"]*)" in my "([^"]*)" from the API$/ do |dataPath, level|
  step "I try to update the data for \"#{dataPath}\" in another \"#{level}\" from the API"
end

Given /^my "([^"]*)" is "([^"]*)"$/ do |level, name|
  # No code needed, this step is informational
end
