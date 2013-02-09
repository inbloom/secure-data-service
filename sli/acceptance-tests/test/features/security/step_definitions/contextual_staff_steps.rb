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
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'

Transform /^data for "([^"]*)"$/ do |path|
  id = "/v1/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if path == "South Daybreak Elementary"
  id = "/v1/schools/9d970849-0116-499d-b8f3-2255aeb69552" if path == "Dawn Elementary"
  id = "/v1/staff" if path == "Staff"
  id = "/v1/teachers" if path == "My Teachers"
  id = "/v1/teachers/67ed9078-431a-465e-adf7-c720d08ef512" if path == "Linda Kim"
  id = "/v1/teachers/bcfcc33f-f4a6-488f-baee-b92fbd062e8d" if path == "Rebecca Braverman"
  id = "/v1/teachers/edce823c-ee28-4840-ae3d-74d9e9976dc5" if path == "Mark Anthony"
  id = "/v1/teachers/a060273b-3e65-4e5f-b5d1-45226f584c5d" if path == "Dale Reiss"
  id = "/v1/students/92d1a002-2695-4fb8-a0d6-4ef655d29e48_id" if path == "Malcolm Haehn NY"
  id = "/v1/students/5738d251-dd0b-4734-9ea6-417ac9320a15_id" if path == "Matt Sollars"
  id = "/v1/students/85ff53e3-2779-4dc7-bc31-59c405f3a49e_id" if path == "Larissa Marney"
  id = "/v1/students/eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d_id" if path == "Lavern Chaney"
  id = "/v1/students/0cff1537-95e6-440b-ba2f-3003a2ecd7ed_id" if path == "Brandon Suzuki"
  id = "/v1/schools/46c2e439-f800-4aaf-901c-8cf3299658cc/studentSchoolAssociations/students" if path == "Students in Parker Elementary"
  id = "/v1/schools/9d970849-0116-499d-b8f3-2255aeb69552/studentSchoolAssociations/students" if path == "Students in Dawn Elementary"
  id = "/v1/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb/studentSchoolAssociations/students" if path == "Students in South Daybreak Elementary"
  id = "/v1/sections/7295e51e-cd51-4901-ae67-fa33966478c7_id/studentSectionAssociations/students" if path == "Students in AP Calculus Sec 201"
  id = "/v1/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb/teacherSchoolAssociations/teachers" if path == "Teachers in South Daybreak Elementary"
  id = "/v1/schools/9d970849-0116-499d-b8f3-2255aeb69552/teacherSchoolAssociations/teachers" if path == "Teachers in Dawn Elementary"
  id = "/v1/schools/46c2e439-f800-4aaf-901c-8cf3299658cc/teacherSchoolAssociations/teachers" if path == "Teachers in Parker Elementary"
  id = "/v1/schools?parentEducationAgencyReference=bd086bae-ee82-4cf2-baf9-221a9407ea07" if path == "Schools in Daybreak District"
  id = "/v1/schools?parentEducationAgencyReference=29b95c04-3d70-4b3a-8341-27d544a39974" if path == "Schools in Parker District"
  id = "/v1/schools?parentEducationAgencyReference=c72ea8f9-ec17-4db7-9958-65002f45da62" if path == "Schools in Dusk District"
  id
end

Transform /^data containing "([^"]*)"$/ do |path|
  id = ["a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"] if path == "South Daybreak Elementary"
  id = ["9d970849-0116-499d-b8f3-2255aeb69552"] if path == "Dawn Elementary"
  id = ["67ed9078-431a-465e-adf7-c720d08ef512"] if path == "Linda Kim"
  id = ["bcfcc33f-f4a6-488f-baee-b92fbd062e8d"] if path == "Rebecca Braverman"
  id = ["edce823c-ee28-4840-ae3d-74d9e9976dc5"] if path == "Mark Anthony"
  id = ["a060273b-3e65-4e5f-b5d1-45226f584c5d"] if path == "Dale Reiss"
  id = ["92d1a002-2695-4fb8-a0d6-4ef655d29e48_id"] if path == "Malcolm Haehn NY"
  id = ["5738d251-dd0b-4734-9ea6-417ac9320a15_id"] if path == "Matt Sollars"
  id = ["0cff1537-95e6-440b-ba2f-3003a2ecd7ed_id"] if path == "Brandon Suzuki"
  id = "eb4d7e1b-7bed-890a-ddb4-5d8aa9fbfc2d_id" if path == "Lavern Chaney"
  id = ["1894b083-5e6e-470f-bc5e-01435a899d44_id",
        "439b5c9d-3ebf-49a3-a483-f10943e148ba_id",
        "6944f8d8-1fda-41c3-a2b7-d9d416d07ebc_id",
        "85ff53e3-2779-4dc7-bc31-59c405f3a49e_id"] if path == "Students in Parker Elementary"
  id = ["b14ff449-56c9-4d10-942b-70c9eb721919_id",
        "92d1a002-2695-4fb8-a0d6-4ef655d29e48_id",
        "f098e289-5678-4df0-9447-ca568e45061d_id",
        "eb4d7e1b-7bed-890a-d5f4-5d8aa9fbfc2d_id",
        "eb4d7e1b-7bed-890a-e1f4-5d8aa9fbfc2d_id",
        "eb4d7e1b-7bed-890a-e5f4-5d8aa9fbfc2d_id",
        "eb4d7e1b-7bed-890a-ddf4-5d8aa9fbfc2d_id",
        "c79fb3a9-a0ec-4ffd-a128-124a0d9a7069_id"] if path == "Students in Dawn Elementary"
  id = ["1563ec1d-924d-4c02-8099-3a0e314ef1d4_id",
        "274f4c71-1984-4607-8c6f-0a91db2d240a_id",
        "0cff1537-95e6-440b-ba2f-3003a2ecd7ed_id",
        "41df2791-b33c-4b10-8de6-a24963bbd3dd_id",
        "23ca4b0f-33a6-4826-be55-a819f683b982_id",
        "27fea52e-94ab-462c-b80f-7e868f6919d7_id",
        "d2462231-4f6c-452e-9b29-4a63ad92138e_id",
        "5738d251-dd0b-4734-9ea6-417ac9320a15_id",
        "365906f4-bfe0-453e-8622-29b33bdea405_id",
        "b20bf77d-9f45-493e-a660-085d0e9a7a2b_id",
        "9630059d-e76c-4f47-ba1e-dcd928bd1c38_id",
        "fb71442f-1023-4c55-a675-92ad8c393c82_id",
        "11d13fde-371c-4b58-b0b0-a6e2d955a947_id",
        "98174ba6-7643-4c6b-8745-9683d04f889b_id",
        "00209530-6e1f-4273-a5a7-eb686c79fcd9_id",
        "cab8d70e-7720-447a-a6a3-512bea6dc3c6_id",
        "e0aab7d8-a486-417b-b3b3-1fbf4ee7b3bf_id",
        "d5b69caa-52bb-4d91-a0f4-2940e295a5d0_id",
        "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
        "4de1bb23-c4d7-4d98-98a2-1710181ed015_id",
        "4992906e-8f7a-4ba5-b940-4c2751ca52d0_id",
        "83e21fcd-5472-40cf-b60d-8e7aae0c5f52_id",
        "8f5928fe-d733-4bd3-921a-03e5d0fdf552_id",
        "61fccb27-262c-43dd-a822-7380ac298286_id",
        "26928f4a-2420-4ee1-b88a-72b87c536366_id",
        "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id",
        "b1fd8fb1-6eec-4bfa-9c4b-2dd39a41d769_id"] if path == "Students in South Daybreak Elementary"
  id = ["737dd4c1-86bd-4892-b9e0-0f24f76210be_id"] if path == "Students in AP Calculus Sec 201"
  id = ["bcfcc33f-f4a6-488f-baee-b92fbd062e8d"] if path == "Teachers in South Daybreak Elementary"
  id = ["35ac640e-8f6e-427f-bbab-abbdca50df5b",
        "8ecbbd5e-5d5b-48ba-ab01-cdc0eefc02f7",
        "1406ed76-f6d8-4ce4-a61b-f118d453e373",
        "a312b789-8a4c-41fd-a43d-368240926545"] if path == "Teachers in Dawn Elementary"
  id = ["a060273b-3e65-4e5f-b5d1-45226f584c5d",
        "3c911a1c-ffa8-4a60-b9b3-fdb06570ecd7",
        "dc3adbe0-61c7-4ebd-be54-2d22dd9fd5ce",
        "50f7c43a-25cc-4ddb-bd6e-18e5e7915223"] if path == "Teachers in Parker Elementary"

  id = ["c4491c07-ec3e-440d-bef9-e349763b0fd4",
        "03184a95-eb8f-4038-9d19-17fab835a016",
        "35ac640e-8f6e-427f-bbab-abbdca50df5b",
        "dba058a9-4f0c-4785-bbc1-910a0f356366",
        "78f03de4-3db6-48a9-8bdc-ecec4b5a6022",
        "8ecbbd5e-5d5b-48ba-ab01-cdc0eefc02f7",
        "1406ed76-f6d8-4ce4-a61b-f118d453e373",
        "a312b789-8a4c-41fd-a43d-368240926545"] if path == "Teachers in Dusk District"

  id = ["67ed9078-431a-465e-adf7-c720d08ef512",
        "bcfcc33f-f4a6-488f-baee-b92fbd062e8d",
        "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b",
        "e9ca4497-e1e5-4fc4-ac7b-24badbad998b"] if path == "Teachers in Daybreak District"

  id = ["edce823c-ee28-4840-ae3d-74d9e9976dc5",
        "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b"] if path == "Teachers in Sunset District"

  id = ["0fa7482f-f123-4b41-934a-05af6692159e",
        "d1126e04-2ebd-4644-b0c9-927d1d30a379",
        "a060273b-3e65-4e5f-b5d1-45226f584c5d",
        "3c911a1c-ffa8-4a60-b9b3-fdb06570ecd7",
        "ba5ae1a8-1ca0-459d-add1-eb99847cfba0",
        "dc3adbe0-61c7-4ebd-be54-2d22dd9fd5ce",
        "50f7c43a-25cc-4ddb-bd6e-18e5e7915223",
        "953d0426-cb77-42f7-8471-e01ed896726c"] if path == "Teachers in Parker District"

  id = ["edce823c-ee28-4840-ae3d-74d9e9976dc5",
        "67ed9078-431a-465e-adf7-c720d08ef512",
        "bcfcc33f-f4a6-488f-baee-b92fbd062e8d",
        "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b",
        "e9ca4497-e1e5-4fc4-ac7b-24badbad998b"] if path == "Teachers in Illinois State"

  id = ["c4491c07-ec3e-440d-bef9-e349763b0fd4",
        "03184a95-eb8f-4038-9d19-17fab835a016",
        "0fa7482f-f123-4b41-934a-05af6692159e",
        "d1126e04-2ebd-4644-b0c9-927d1d30a379",
        "a060273b-3e65-4e5f-b5d1-45226f584c5d",
        "3c911a1c-ffa8-4a60-b9b3-fdb06570ecd7",
        "35ac640e-8f6e-427f-bbab-abbdca50df5b",
        "dba058a9-4f0c-4785-bbc1-910a0f356366",
        "ba5ae1a8-1ca0-459d-add1-eb99847cfba0",
        "78f03de4-3db6-48a9-8bdc-ecec4b5a6022",
        "8ecbbd5e-5d5b-48ba-ab01-cdc0eefc02f7",
        "dc3adbe0-61c7-4ebd-be54-2d22dd9fd5ce",
        "50f7c43a-25cc-4ddb-bd6e-18e5e7915223",
        "1406ed76-f6d8-4ce4-a61b-f118d453e373",
        "953d0426-cb77-42f7-8471-e01ed896726c",
        "a312b789-8a4c-41fd-a43d-368240926545"] if path == "Teachers in New York State"

  id = ["a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
        "ec2e4218-6483-4e9c-8954-0aecccfd4731",
        "92d6d5a0-852c-45f4-907a-912752831772"] if path == "Schools in Daybreak District"
  id = ["5b03de1f-9cf0-409b-ae35-edeed11161ab",
        "46c2e439-f800-4aaf-901c-8cf3299658cc"] if path == "Schools in Parker District"
  id = ["c9929e15-f907-4473-a948-6f9aa302647d",
        "9d970849-0116-499d-b8f3-2255aeb69552"] if path == "Schools in Dusk District"
  id = ["84d87dd4-174b-4c02-af95-9f11d45031b6",
        "b7842226-0a86-4306-ab31-f76645b62625",
        "c5b29c29-62e6-4210-8712-d49581a27bf2",
        "05baccbe-5f35-491f-ae9e-0758943c0343",
        "d2d35368-114d-4120-b0bf-616219787677",
        "381da5d9-cb00-4158-a10d-c6438d760e67",
        "4acc9259-a6c4-4a63-9e63-969e39c99a73",
        "56012673-eda3-4d95-bf78-7524b91d8a93",
        "f8e8c093-721d-44c9-8edb-a26fdaaec91f"] if path == "Staff"
  id
end

When /^I try to access the (data for "[^"]*") in my "[^"]*" from the API$/ do |dataPath|
  @format = "application/json"
  restHttpGet(dataPath)
end

Then /^The response contains an empty array$/ do
  assert(@res.code == 200, "Received a #{@res.code.to_s} response from the request, expected 200")
  result = JSON.parse(@res.body)
  assert(result.length == 0)
end

Then /^I get the (data containing "[^"]*") returned in json format$/ do |idArray|
  assert(@res != nil, "Did not receive a response from the API")
  assert(@res.code == 200, "Received a #{@res.code.to_s} response from the request, expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil)

  if result.class == Array
    numMatches = 0
    result.each {|jsonObj|
      # Find each ID in the JSON
      assert(idArray.include?(jsonObj["id"]),"ID returned in json was not expected: ID="+jsonObj["id"])
      numMatches += 1
    }
    assert(numMatches == idArray.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+idArray.length.to_s+" maches")
  else
    assert(idArray[0] == result["id"], "ID returned in json was not expected: ID="+result["id"])
  end

end

Then /^I should get a response which includes the (data containing "[^"]*") returned in json format$/ do |idArray|
  assert(@res != nil, "Did not receive a response from the API")
  assert(@res.code == 200, "Received a #{@res.code.to_s} response from the request, expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil)

  if result.class == Array
    numMatches = 0
    result.each {|jsonObj|
      numMatches += 1 if idArray.include?(jsonObj["id"])
    }
    assert(numMatches == idArray.length, "Did not find all matches: found "+numMatches.to_s+" but expected "+idArray.length.to_s+" maches")
  else
    assert(idArray[0] == result["id"], "ID returned in json was not expected: ID="+result["id"])
  end
end

When /^I try to access the (data for "[^"]*") in another "[^"]*" from the API$/ do |dataPath|
  @format = "application/json"
  restHttpGet(dataPath)
end

When /^I try to update the data for "([^"]*)" in another "[^"]*" from the API$/ do |dataPath|
  step "I try to update the data for \"#{dataPath}\" in my \"level\" from the API"
end

Then /^the data should be updated$/ do
  restHttpGet(@path)
  current = JSON.parse(@res.body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current["name"]["firstName"] == "Updated" , "Unsuccesful update to #{@path}")
  assert(current["name"]["lastSurname"] == "Name#{@randomKey.to_s}" , "Unsuccesful update to #{@path}")
end

When /^I try to update the (data for "[^"]*") in my "[^"]*" from the API$/ do |dataPath|
  @format = "application/json"
  @path = dataPath
  @randomKey = rand(100)
  @studentObj = {
    "birthData" => {
      "birthDate" => "1994-04-04"
    },
    "sex" => "Male",
    #"studentUniqueStateId" => (0...8).map{65.+(rand(25)).chr}.join, #can't update natural key
    "economicDisadvantaged" => false,
    "name" => {
      "firstName" => "Updated",
      "lastSurname" => "Name#{@randomKey.to_s}"
    }
  }
  restHttpPatch(dataPath, @studentObj.to_json)
end

Given /^my "([^"]*)" is "([^"]*)"$/ do |level, name|
  # No code needed, this step is informational
end
