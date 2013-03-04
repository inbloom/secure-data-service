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

require_relative '../../utils/sli_utils.rb'
require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'

def init_realm_map
  @realm_lookup_map = Hash["IL-Sunset","e5c12cb0-1bad-4606-a936-097b30bd47fe",
                           "Fake Realm","4cfcbe8d-832d-40f2-a9ba-0a6f1daf3741",
                           "Another Fake Realm","45b03fa0-1bad-4606-a936-09ab71af37fe"]
end

Transform /^realm "([^"]*)"$/ do |arg1|
  init_realm_map if @realm_lookup_map == nil 
  if arg1 == "Sandbox"
    disable_NOTABLESCAN
    conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
    db = conn.db('sli')
    coll = db.collection('realm')
    sandboxRealm = coll.find_one({"body.uniqueIdentifier" => "Shared Learning Collaborative"})
    id = sandboxRealm["_id"]
    enable_NOTABLESCAN
  else
    id = @realm_lookup_map[arg1]
  end
  id
end

When /^I try to access the URI "([^"]*)" with operation "([^"]*)"$/ do |arg1, arg2|
  @format = "application/json"

  dataObj = DataProvider.getValidRealmData()
  dataObj["state"] = "URI Access Test State"

  data = prepareData("application/json", dataObj)

  restHttpPost(arg1, data) if arg2 == "POST"
  restHttpGet(arg1) if arg2 == "GET"
  restHttpPut(arg1, data) if arg2 == "PUT"
  restHttpDelete(arg1) if arg2 == "DELETE"
end

Then /^I should be denied access$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

Then /^I should see a valid object returned$/ do
  result = JSON.parse(@res.body).sort()
  assert(result != nil, "Result of JSON parsing is nil")
end

When /^I POST a new realm called "([^"]*)"$/ do |name|
  data = DataProvider.getValidRealmData()
  data["idp"]["id"] = "https://fake.path.org/#{name}"
  data["uniqueIdentifier"] = "#{name}_4shizzle"
  data["name"] = name
  data["edOrg"] = "VIRGON"
  dataFormatted = prepareData("application/json", data)
  restHttpPost("/realm", dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
end

Then /^I should receive a new ID for my new realm of "(.*?)"$/ do |arg1|
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  newId = s[s.rindex('/')+1..-1]
  assert(newId != nil, "No ID was returned for created object")
  
  init_realm_map if @realm_lookup_map == nil
  @realm_lookup_map[arg1] = newId
end

When /^I GET a list of realms$/ do
  restHttpGet("/realm")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should see a list of "([^"]*)" valid realm objects$/ do |number|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result.size == number.to_i)

  result.each do |item|
    assert(item["idp"] != nil, "Realm "+item["tenantId"]+" URL was not found.")
  end
end

Then /^I should see a list of "(.*?)" valid custom role objects$/ do |number|
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result.size == number.to_i)

  result.each do |item|
    assert(item["roles"] != nil, "Custom roles not found for realm "+item["realmId"])
  end
end

Then /^I should only see the (realm ".*?")$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result.size == 1, "Expected only one realm, got #{result.size}")
  assert(result[0]["id"] == arg1, "Realm returned was not expected: got #{result[0]["id"]}, expected #{arg1}")
end

Then /^I should see only one custom role document$/ do
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result.size == 1, "Expected only see one custom role doc, got #{result.size}")
end

When /^I GET a specific (realm "[^"]*")$/ do |arg1|
  restHttpGet("/realm/" + arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I PUT to change the (realm "[^"]*") to change field "([^"]*)" to "([^"]*)"$/ do |realmId, fieldName, fieldValue|
  restHttpGet("/realm/" + realmId)
  assert(@res != nil, "Response from rest-client GET is nil")
  data = JSON.parse(@res.body)

  data[fieldName] = fieldValue

  dataFormatted = prepareData("application/json", data)

  restHttpPut("/realm/" + realmId, dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I DELETE the (realm "[^"]*")$/ do |arg1|
  restHttpDelete("/realm/" + arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

When /^I POST a new custom role document for (realm "[^"]*")$/ do |realm|
  data = DataProvider.getValidCustomRoleData()
  data["realmId"] = realm
  dataFormatted = prepareData("application/json", data)
  restHttpPost("/customRoles", dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I POST a new custom role document with for my new realm$/ do
  pending
  data = DataProvider.getValidCustomRoleData()
  data["realmId"] = $new_realm_id
  dataFormatted = prepareData("application/json", data)
  restHttpPost("/customRoles", dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
end

Then /^I should receive a new ID for my new custom role document$/ do
  assert(@res.raw_headers["location"] != nil, "No ID was returned for created object")
end

Given /^I am editing the custom role document for (realm "[^"]*")$/ do |arg1|
  restHttpGet("/customRoles/?realmId=#{arg1}")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result.size == 1, "Expected only see one custom role doc, got #{result.size}")
  @custom_role_doc_under_test = result[0]
end

When /^I add a role "([^"]*)" in group "([^"]*)"$/ do |role, group|
  data = @custom_role_doc_under_test
  groups = data["roles"]
  curGroup = groups.select {|group| group["groupTitle"] == group}
  if curGroup.nil? or curGroup.empty?
    curGroup = {"groupTitle" => group, "names" => [role], "rights" => ["READ_GENERAL"], "isAdminRole" => false}
    groups.push(curGroup)
  else
    groups.delete_if {|group| group["groupTitle"] == group}
    curGroup["names"].push(role)
    groups.push(curGroup)
  end
  data["roles"] = groups
  dataFormatted = prepareData("application/json", data)
  restHttpPut("/customRoles/" + data["id"], dataFormatted, "application/json")
end

When /^I add a right "([^"]*)" in group "([^"]*)"$/ do |right, group|
  data = @custom_role_doc_under_test
  groups = data["roles"]
  curGroup = groups.select {|group| group["groupTitle"] == group}
  if curGroup.nil? or curGroup.empty?
    curGroup = {"groupTitle" => group, "names" => ["FAKE-NAME"], "rights" => [right], "isAdminRole" => false}
    groups.push(curGroup)
  else
    groups.delete_if {|group| group["groupTitle"] == group}
    curGroup["rights"].push(right)
    groups.push(curGroup)
  end
  data["roles"] = groups
  dataFormatted = prepareData("application/json", data)
  restHttpPut("/customRoles/" + data["id"], dataFormatted, "application/json")
end

When /^I DELETE the custom role doc for (realm "[^"]*")$/ do |arg1|
  restHttpGet("/customRoles/?realmId=#{arg1}")
  assert(@res != nil, "Response from custom role request is nil")
  data = JSON.parse(@res.body).sort()[0]
  restHttpDelete("/customRoles/" + data["id"])
end

When /^I GET the custom role doc for (realm "[^"]*")$/ do |arg1|
  restHttpGet("/customRoles/?realmId=" + arg1)
  assert(@res != nil, "Response from custom role request is nil")
end

When /^I GET a list of custom role docs$/ do
  restHttpGet("/customRoles/")
  assert(@res != nil, "Response from custom role request is nil")
end

Then /^I should see one custom roles document for the (realm ".*?")$/ do |arg1|
  result = JSON.parse(@res.body).sort()
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result.size == 1, "Admin users should only ever see one custom roles doc, got #{result.size}")
  assert(result[0]["realmId"] == arg1, "Custom role doc returned was not expected: got #{result[0]["realmId"]}, expected #{arg1}")
end

Then /^I should see that my custom role document is the default with (realm "[^"]*")$/ do |realm|
  data = JSON.parse(@res.body).sort()[0]
  assert(data["realmId"] == realm, "Realm received from custom role document did not match")
  default = DataProvider.getValidCustomRoleData()
  assert(data["roles"] == default["roles"], "Roles info from custom role document did not match")
  assert(data["customRights"] == default["customRights"], "Custom rights info from custom role document did not match")
end

When /^I PUT a new group "(.*?)" with role "(.*?)" and right "(.*?)"$/ do |group, role, right|
  restHttpGet("/customRoles/")
  assert(@res != nil, "Response from custom role request is nil")
  data = JSON.parse(@res.body).sort()[0]
  newGroup = {"groupTitle" => group, "names" => [role], "rights" => [right], "isAdminRole" => false}
  data["roles"].push(newGroup)
  dataFormatted = prepareData("application/json", data)
  restHttpPut("/customRoles/" + data["id"], dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
end

