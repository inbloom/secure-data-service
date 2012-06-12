require 'json'
require_relative '../../../../utils/sli_utils.rb'

$studentHash = {
    "birthData" => {
      "birthDate" => "1994-04-04"
    },
    "sex" => "Male",
    "studentUniqueStateId" => "123456",
    "economicDisadvantaged" => false,
    "name" => {
      "firstName" => "Mister",
      "middleName" => "John",
      "lastSurname" => "Doe"
    
    }
  }

Transform /the student "([^"]*)"/ do |arg1|
  base = "00000000-abcd-0000-0000-0000000000"
  base << arg1.match(/(\d+)/)[0]
  base
end

Given /^I am user "([^"]*)" in IDP "([^"]*)"$/ do |arg1, arg2|
  user = arg1
  pass = arg1+"1234"
  realm = arg2
  
  idpRealmLogin(user, pass, realm)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to update (the student "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  student_uri ="/v1/students/"+arg1
  restHttpGet(student_uri)
 
  assert(@res != nil, "Response from rest-client GET is nil")
  
  if (@res.code == 403) 
    data = $studentHash.to_json 
  else
    dataH = JSON.parse(@res.body)
    
    dataH['address'] = [{"streetNumberName" => "arg1",
                           "city" => "Urbania",
                           "stateAbbreviation" => "NC",
                           "postalCode" => "12345"}]
    
    data = dataH.to_json
  end

  restHttpPut(student_uri, data, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")  
end

Then /^I see the response "([^"]*)" restricted data and "([^"]*)" general data$/ do |arg1, arg2|
  expectedGeneral = (arg2 === "includes" ? true : false)
  expectedRestricted = (arg1 === "includes" ? true : false)
  actualGeneral = false
  actualRestricted = false
  
  begin
    dataH = JSON.parse(@res.body)
    actualGeneral = true if dataH.include? 'name'
    actualRestricted = true if dataH.include? 'economicDisadvantaged'
  rescue
  end
  assert(expectedRestricted == actualRestricted, "Expectations for seeing restricted data is incorrect.")
  assert(expectedGeneral == actualGeneral, "Expectations for seeing general data is incorrect. Expected #{expectedGeneral}, Actual #{actualGeneral}")
end

When /^I make an API call to get my student list$/ do
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/students/")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should see a count of (\d+)$/ do |arg1|
  data = JSON.pares(@res.body)
  assert(data.count == arg1, "Count should match")
end