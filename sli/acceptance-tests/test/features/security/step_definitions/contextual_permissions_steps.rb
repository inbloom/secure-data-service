require 'json'
require_relative '../../utils/sli_utils.rb'

Transform /^the school "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-8da22127fd2d" if arg1 == "Fry High School"
  id = "eb4d7e1b-7bed-890a-d974-8da22127fd2d" if arg1 == "Watson Elementary School"
  id = "eb4d7e1b-7bed-890a-d5b4-8da22127fd2d" if arg1 == "Parker-Dust Middle School"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if arg1 == "South Daybreak Elementary"
  id = "9d970849-0116-499d-b8f3-2255aeb69552" if arg1 == "Dawn Elementary"
  id
end

Transform /^the teacher "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-1d729a37fd2d" if arg1 == "John Doe 1"
  id = "eb4d7e1b-7bed-890a-d974-1d729a37fd2d" if arg1 == "Ted Bear"
  id = "eb4d7e1b-7bed-890a-d5b4-1d729a37fd2d" if arg1 == "John Doe 2"
  id = "eb4d7e1b-7bed-890a-d9b4-1d729a37fd2d" if arg1 == "Elizabeth Jane"
  id = "eb4d7e1b-7bed-890a-d5f4-1d729a37fd2d" if arg1 == "John Doe 3"
  id = "eb4d7e1b-7bed-890a-d9f4-1d729a37fd2d" if arg1 == "Emily Jane"
  id = "fd57632c-b54f-4a40-bd85-217c36a2720c" if arg1 == "Keisha Melendez"
  id = "c4491c07-ec3e-440d-bef9-e349763b0fd4" if arg1 == "Darlene Mallard"
  id
end

Transform /^the section "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d" if arg1 == "FHS-Math101"
  id = "eb4d7e1b-7bed-890a-d974-cdb25a29fc2d" if arg1 == "FHS-Science101"
  id = "eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d" if arg1 == "FHS-English101"
  id = "eb4d7e1b-7bed-890a-d5b4-cdb25a29fc2d" if arg1 == "WES-English"
  id = "eb4d7e1b-7bed-890a-d9b4-cdb25a29fc2d" if arg1 == "WES-Math"
  id = "eb4d7e1b-7bed-890a-d5f4-cdb25a29fc2d" if arg1 == "PDMS-Trig"
  id = "eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d" if arg1 == "PDMS-Geometry"
  id = "c90a21d4-54a2-42e4-a4b8-e0710b8ced69" if arg1 == "ARCH221-Sec2"
  id
end

Transform /^the student "([^"]*)"$/ do |arg1|
  id = "eb4d7e1b-7bed-890a-d574-5d8aa9fbfc2d" if arg1 == "Doris Hanes"
  id = "eb4d7e1b-7bed-890a-d974-5d8aa9fbfc2d" if arg1 == "Danny Fields"
  id = "eb4d7e1b-7bed-890a-dd74-5d8aa9fbfc2d" if arg1 == "Gail Newman"
  id = "eb4d7e1b-7bed-890a-e174-5d8aa9fbfc2d" if arg1 == "Mark Moody"
  id = "eb4d7e1b-7bed-890a-e574-5d8aa9fbfc2d" if arg1 == "Irma Atkons"
  id = "eb4d7e1b-7bed-890a-e974-5d8aa9fbfc2d" if arg1 == "Austin Durran"
  id = "eb4d7e1b-7bed-890a-d5b4-5d8aa9fbfc2d" if arg1 == "Kristy Carillo"
  id = "eb4d7e1b-7bed-890a-d9b4-5d8aa9fbfc2d" if arg1 == "Forrest Hopper"
  id = "eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d" if arg1 == "Lavern Chaney"
  id = "eb4d7e1b-7bed-890a-e1b4-5d8aa9fbfc2d" if arg1 == "Emil Oneil"
  id = "eb4d7e1b-7bed-890a-e5b4-5d8aa9fbfc2d" if arg1 == "Kesley Krauss"
  id = "eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d" if arg1 == "Hal Kessler"
  id = "eb4d7e1b-7bed-890a-d9f4-5d8aa9fbfc2d" if arg1 == "Millie Lovel"
  id = "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d" if arg1 == "Brock Ott"
  id = "eb4d7e1b-7bed-890a-e1f4-5d8aa9fbfc2d" if arg1 == "Elnora Fin"
  id = "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d" if arg1 == "Freeman Marcum"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15" if arg1 == "Matt Sollars"
  id = "92d1a002-2695-4fb8-a0d6-4ef655d29e48" if arg1 == "Malcolm Haehn"
  id = "85ff53e3-2779-4dc7-bc31-59c405f3a49e" if arg1 == "Larissa Marney"
  id
end

Transform /list of teachers from school "([^\"]*)"/ do |arg1|
  array = ["eb4d7e1b-7bed-890a-d574-1d729a37fd2d",
           "eb4d7e1b-7bed-890a-d974-1d729a37fd2d"] if arg1 == "Fry High School"
  array = ["eb4d7e1b-7bed-890a-d5b4-1d729a37fd2d",
           "eb4d7e1b-7bed-890a-d9b4-1d729a37fd2d"] if arg1 == "Watson Elementary School"
  array = ["eb4d7e1b-7bed-890a-d5f4-1d729a37fd2d",
           "eb4d7e1b-7bed-890a-d9f4-1d729a37fd2d"] if arg1 == "Parker-Dust Middle School"
  array
end

Transform /list of sections that "([^\"]*)" teaches/ do |arg1|
  array = ["eb4d7e1b-7bed-890a-d574-cdb25a29fc2d",
           "eb4d7e1b-7bed-890a-d974-cdb25a29fc2d"] if arg1 == "John Doe 1"
  array = ["eb4d7e1b-7bed-890a-d974-cdb25a29fc2d",
           "eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d"] if arg1 == "Ted Bear"
  array = ["eb4d7e1b-7bed-890a-d5b4-cdb25a29fc2d"] if arg1 == "John Doe 2"
  array = ["eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d"] if arg1 == "Elizabeth Jane"
  array = ["eb4d7e1b-7bed-890a-d5f4-cdb25a29fc2d",
           "eb4d7e1b-7bed-890a-d9f4-cdb25a29fc2d"] if arg1 == "John Doe 3"
  array = ["eb4d7e1b-7bed-890a-d9b4-cdb25a29fc2d"] if arg1 == "Emily Jane"
  array
end

Transform /list of students in section "([^\"]*)"/ do |arg1|
  array = ["eb4d7e1b-7bed-890a-d574-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-d974-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-dd74-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-e174-5d8aa9fbfc2d"] if arg1 == "FHS-Math101"
  array = ["eb4d7e1b-7bed-890a-d974-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-dd74-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-e574-5d8aa9fbfc2d"] if arg1 == "FHS-Science101"
  array = ["eb4d7e1b-7bed-890a-e174-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-e574-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-e974-5d8aa9fbfc2d"] if arg1 == "FHS-English101"
  array = ["eb4d7e1b-7bed-890a-d5b4-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-d9b4-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d"] if arg1 == "WES-English"
  array = ["eb4d7e1b-7bed-890a-e1b4-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-e5b4-5d8aa9fbfc2d"] if arg1 == "WES-Math"
  array = ["eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-d9f4-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d"] if arg1 == "PDMS-Trig"
  array = ["eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d",
           "eb4d7e1b-7bed-890a-e1f4-5d8aa9fbfc2d", 
           "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d"] if arg1 == "PDMS-Geometry"
  array
end

Transform /the staff "[^"]*"/ do |arg1|
  id = nil
  case arg1
  when /Rick Rogers/
    id = "/v1/staff/85585b27-5368-4f10-a331-3abcaf3a3f4c"
  end
  id
end

Given /^I have a Role attribute that equals "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

Given /^my School is "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the school "[^"]*")$/ do |arg1|
  restHttpGet("/v1/schools/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get (the staff "[^"]*")$/ do |arg1|
  puts #{arg1}
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the school "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["nameOfInstitution"] == arg1, "School name returned was "+result["nameOfInstitution"]+" and expected "+arg1)
end

Then /^I should get a message that I am not authorized$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

When /^I make an API call to get (the teacher "[^"]*")$/ do |arg1|
  restHttpGet("/v1/teachers/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the teacher "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  nameArray = arg1.split
  firstName = nameArray[0]
  lastName = nameArray[1]
  assert(result["name"]["firstName"] == firstName, "Teacher first name returned was "+result["name"]["firstName"]+" but expected "+firstName)
  assert(result["name"]["lastSurname"] == lastName, "Teacher last name returned was "+result["name"]["lastSurname"]+" but expected "+lastName)
end

When /^I make an API call to get list of teachers from (the school "[^"]*")$/ do |arg1|
  restHttpGet("/v1/schools/" + arg1 + "/teacherSchoolAssociations/teachers")
#  assert(false, "#{arg1} Response from rest-client GET is nil")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes a (list of teachers from school "[^"]*")$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  numMatches = 0
  result.each {|jsonObj| 
    # Find each ID in the JSON
    assert(arg1.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
    numMatches += 1
  }
  assert(numMatches == arg1.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+arg1.length.to_s+" maches")
end

When /^I make an API call to get the list of sections taught by (the teacher "[^"]*")$/ do |arg1|
  restHttpGet("/v1/teachers/" + arg1 + "/teacherSectionAssociations/sections")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the (list of sections that "[^"]*" teaches)$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  numMatches = 0
  result.each {|jsonObj| 
    # Find each ID in the JSON
    assert(arg1.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
    numMatches += 1
  }
  assert(numMatches == arg1.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+arg1.length.to_s+" maches")
end

Given /^I teach in "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the section "[^"]*")$/ do |arg1|
  restHttpGet("/v1/sections/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the section "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result["uniqueSectionCode"] == arg1, "Section name returned was "+result["uniqueSectionCode"]+" and expected "+arg1)
end

When /^I make an API call to get a list of students in (the section "[^"]*")$/ do |arg1|
  restHttpGet("/v1/sections/" + arg1 + "/studentSectionAssociations/students")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the (list of students in section "[^"]*")$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  numMatches = 0
  result.each {|jsonObj| 
    # Find each ID in the JSON
    assert(arg1.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
    numMatches += 1
  }
  assert(numMatches == arg1.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+arg1.length.to_s+" maches")
end

Given /^I teach the student "([^"]*)"$/ do |arg1|
  #No code needed, this is done as configuration
end

When /^I make an API call to get (the student "[^"]*")$/ do |arg1|
  restHttpGet("/v1/students/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON response that includes the student "([^"]*)" and its attributes$/ do |arg1|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  nameArray = arg1.split
  firstName = nameArray[0]
  lastName = nameArray[1]
  assert(result["name"]["firstName"] == firstName, "Student first name returned was "+result["name"]["firstName"]+" but expected "+firstName)
  assert(result["name"]["lastSurname"] == lastName, "Student last name returned was "+result["name"]["lastSurname"]+" but expected "+lastName)
end
