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

Transform /^district "([^"]*)"$/ do |district|
  id = "4726e42f-b265-372a-3c17-dc8d5d5fb263" if district == "IL-SUNSET"
  id = "b2c6e292-37b0-4148-bf75-c98a2fcc905f" if district == "IL-SUNSET's ID"
  id = "xd086bae-ee82-6ce2-bcf9-321a8407ba13" if district == "IL-LONGWOOD's ID"
  id
end

When /^I POST a new admin delegation$/ do
  dataObj = DataProvider.getValidAdminDelegationData()
  data = prepareData("application/json", dataObj)
  @format = "application/json"


  restHttpPost("/adminDelegation", data)

  assert(@res != nil, "Response from POST operation was null")
  
end


When /^I do not have access to app authorizations for district "([^"]*)"$/ do |arg1|
  #No code necessary, should be handled already
end


Given /^I have a valid admin delegation entity$/ do
  @adminDelegation = DataProvider.getValidAdminDelegationData()
end

Given /^I have a valid admin delegation entity for longwood$/ do
  @adminDelegation = DataProvider.getValidAdminDelegationDataLongwood()
end

Given /^I change "([^"]*)" to true$/ do |field|
  @adminDelegation[field] = true
end

When /^I PUT to admin delegation$/ do
  @format = "application/json"
  data = prepareData(@format, @adminDelegation)
  puts data.inspect
  restHttpPut("/adminDelegation/myEdOrg", data)
  assert(@res != nil, "Response from PUT operation was nil")
end

When /^I have access to app authorizations for district "([^"]*)"$/ do |arg1|
  #No code necessary, should be handled already
end

Then /^I should update app authorizations for (district "[^"]*")$/ do |district|
  @format = "application/json"
  dataObj = {"authId" => "b2c6e292-37b0-4148-bf75-c98a2fcc905f", "appIds" => ["78f71c9a-8e37-0f86-8560-7783379d96f7"], "authType" => "EDUCATION_ORGANIZATION"}
  data = prepareData("application/json", dataObj)
  puts("The data is #{data}") if ENV['DEBUG']
  restHttpPut("/applicationAuthorization/" + district, data)
  assert(@res != nil, "Response from PUT operation was nil")
end

Then /^I should update one app authorization for (district "[^"]*")$/ do |district|
  @format = "application/json"
  dataObj = {"authId" => "b2c6e292-37b0-4148-bf75-c98a2fcc905f", "appIds" => ["78f71c9a-8e37-0f86-8560-7783379d96f7"], "authType" => "EDUCATION_ORGANIZATION"}
  data = prepareData("application/json", dataObj)
  puts("The data is #{data}") if ENV['DEBUG']
  restHttpPut("/applicationAuthorization/" + district, data)
  assert(@res != nil, "Response from PUT operation was nil")
end

Then /^I should also update app authorizations for (district "[^"]*")$/ do |district|
  @format = "application/json"
  dataObj = {"authId" => "b2c6e292-37b0-4148-bf75-c98a2fcc905f", "authType" => "EDUCATION_ORGANIZATION", "appIds" => ["78f71c9a-8e37-0f86-8560-7783379d96f7"]}
  data = prepareData("application/json", dataObj)
  puts("The data is #{data}") if ENV['DEBUG']
  restHttpPut("/applicationAuthorization/" + district, data)
  assert(@res != nil, "Response from PUT operation was nil")
end

Then /^I should get my delegations$/ do
  restHttpGet("/adminDelegation")
  assert(@res != nil, "Response from GET operation was nil")
end

Then /^I should see that "([^"]*)" is "([^"]*)" for (district "[^"]*")$/ do |field, value, district|
  list = JSON.parse(@res.body)
  foundIt = false
  puts "brandon"  
  puts district
  list.each do |cur|
    
    if cur["localEdOrgId"] == district  
      foundIt = true
      assert(cur[field].to_s == value, "Expected field #{field} to be #{value} was #{cur[field].to_s}")
    end
  end
  assert(foundIt, "Never found district #{district}")
end

When /^I should save the old app authorizations for ("[^"]*")/ do |district|
  appAuthColl()
  $appAuths = @coll.find_one({"body.authId" => district})
end

Then /^I put back app authorizations/ do
  if $appAuths != nil
    appAuthColl()
    @coll.remove({"body.authId" => $appAuths["body"]["authId"]})
    @coll.insert($appAuths)
  end
end

def appAuthColl
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db(convertTenantIdToDbName('DB_NAME'))
  @coll ||= @db.collection('applicationAuthorization')
  return @coll
end
