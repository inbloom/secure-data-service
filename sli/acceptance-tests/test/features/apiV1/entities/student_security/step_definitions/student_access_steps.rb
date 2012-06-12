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
  id = "00000000-abcd-0000-0000-000000000001" if arg1 == "student1"
  id = "00000000-abcd-0000-0000-000000000002" if arg1 == "student2"
  id = "00000000-abcd-0000-0000-000000000003" if arg1 == "student3"
  id = "00000000-abcd-0000-0000-000000000004" if arg1 == "student4"
  id = "00000000-abcd-0000-0000-000000000005" if arg1 == "student5"
  id = "00000000-abcd-0000-0000-000000000006" if arg1 == "student6"
  id = "00000000-abcd-0000-0000-000000000007" if arg1 == "student7"
  id = "00000000-abcd-0000-0000-000000000008" if arg1 == "student8"
  id = "00000000-abcd-0000-0000-000000000009" if arg1 == "student9"
  id = "00000000-abcd-0000-0000-000000000010" if arg1 == "student10"
  id = "00000000-abcd-0000-0000-000000000011" if arg1 == "student11"
  id = "00000000-abcd-0000-0000-000000000012" if arg1 == "student12"
  id = "00000000-abcd-0000-0000-000000000013" if arg1 == "student13"
  id = "00000000-abcd-0000-0000-000000000014" if arg1 == "student14"
  id = "00000000-abcd-0000-0000-000000000015" if arg1 == "student15"
  id = "00000000-abcd-0000-0000-000000000016" if arg1 == "student16"
  id = "00000000-abcd-0000-0000-000000000017" if arg1 == "student17"
  id = "00000000-abcd-0000-0000-000000000018" if arg1 == "student18"
  id = "00000000-abcd-0000-0000-000000000019" if arg1 == "student19"
  id = "00000000-abcd-0000-0000-000000000020" if arg1 == "student20"
  id = "00000000-abcd-0000-0000-000000000021" if arg1 == "student21"
  id = "00000000-abcd-0000-0000-000000000022" if arg1 == "student22"
  id = "00000000-abcd-0000-0000-000000000023" if arg1 == "student23"
  id = "00000000-abcd-0000-0000-000000000024" if arg1 == "student24"
  id = "00000000-abcd-0000-0000-000000000025" if arg1 == "student25"
  id = "00000000-abcd-0000-0000-000000000026" if arg1 == "student26"
  id = "00000000-abcd-0000-0000-000000000027" if arg1 == "student27"
  id = "00000000-abcd-0000-0000-000000000028" if arg1 == "student28"
  id = "00000000-abcd-0000-0000-000000000029" if arg1 == "student29"
  id = "00000000-abcd-0000-0000-000000000030" if arg1 == "student30"
  id = "00000000-abcd-0000-0000-000000000031" if arg1 == "student31"
  id = "00000000-abcd-0000-0000-000000000032" if arg1 == "student32"
  id = "00000000-abcd-0000-0000-000000000033" if arg1 == "student33"
  id = "00000000-abcd-0000-0000-000000000034" if arg1 == "student34"
  id = "00000000-abcd-0000-0000-000000000035" if arg1 == "student35"
  id = "00000000-abcd-0000-0000-000000000036" if arg1 == "student36"
  id = "00000000-abcd-0000-0000-000000000037" if arg1 == "student37"
  id = "00000000-abcd-0000-0000-000000000038" if arg1 == "student38"
  id = "00000000-abcd-0000-0000-000000000039" if arg1 == "student39"
  id = "00000000-abcd-0000-0000-000000000040" if arg1 == "student40"
  id = "00000000-abcd-0000-0000-000000000041" if arg1 == "student41"
  id = "00000000-abcd-0000-0000-000000000042" if arg1 == "student42"
  id = "00000000-abcd-0000-0000-000000000043" if arg1 == "student43"
  id = "00000000-abcd-0000-0000-000000000044" if arg1 == "student44"
  id = "00000000-abcd-0000-0000-000000000045" if arg1 == "student45"
  id = "00000000-abcd-0000-0000-000000000046" if arg1 == "student46"
  id
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
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
  
  if (@res.code == 403) 
    data = $studentHash.to_json 
  else
    dataH = JSON.parse(@res.body)
    
    dataH['address'] = [Hash["streetNumberName" => "arg1",
                           "city" => "Urbania",
                           "stateAbbreviation" => "NC",
                           "postalCode" => "12345"]]
    
    data = dataH.to_json
  end

  restHttpPut(student_uri, data, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")  
end

Then /^I see the response ("[^"]*") restricted data and ("[^"]*") general data$/ do |arg1, arg2|
  expectedGeneral = false
  expectedRestricted = false
  actualGeneral = false
  actualRestricted = false
  
  expectedRestricted = true if arg1 == "includes" 
  expectedGeneral = true if arg2 == "includes"
  
  begin
    dataH = JSON.parse(@res.body)
    actualGeneral = true if dataH['name'] != nil
    actualRestricted = true if dataH['economicDisadvantaged'] != nil
  rescue
  end
  
  assert(exectedGeneral == actualGeneral, "Expected response #{arg2} general data, actual = #{actualGeneral}")
  assert(expectedRestricted == actualGeneral, "Expected response #{arg1} general data, actual = #{actualRestricted}")
end