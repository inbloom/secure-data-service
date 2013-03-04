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


require_relative '../../utils/sli_utils.rb'
require_relative '../../apiV1/utils/api_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"d0b2ded4-89a9-db4a-8f80-aaece6fda529" if arg2 == "Testing App"
  id = arg1+@newId       if arg2 == "New App ID"
  #id = step_arg if id == nil
  id
end

When /^I have a valid application entity$/ do
  @format = "application/json"
  @fields = DataProvider.getValidAppData()
  @fields.delete "registration"
end

Then /^I should receive the data for the specified application entry$/ do
    parseApplicationResult('developer')
end

Then /^I should receive the data for the specified sandbox application entry$/ do
    parseApplicationResult('sandboxdeveloper')
end

def parseApplicationResult(creator)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  assert(result['metaData']['createdBy'] == creator, "Created By field should be stamped with #{creator} instead of #{result['metaData']['createdBy']}")
  @client_secret = result["client_secret"]
  @client_id = result["client_id"]
  @registration = result["registration"]
end

When /^\(AR\) I navigate to PUT "([^"]*)"$/ do |arg1|
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj["description"] = "New and Improved"
  dataObj["client_secret"] = @client_secret
  dataObj["client_id"] = @client_id
  dataObj["registration"]  = @registration
  data = prepareData("application/json", dataObj)

  restHttpPut(arg1, data)

  assert(@res != nil, "Response from PUT operation was null")
end

When /^an operator approves the "([^"]*)" application$/ do |arg1|
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()    
  dataObj["client_secret"] = @client_secret
  dataObj["client_id"] = @client_id
  dataObj["registration"]  = @registration
  dataObj["registration"]["status"] = "APPROVED"
  data = prepareData("application/json", dataObj)
  restHttpPut(arg1, data, @format, "a8cf184b-9c7e-4253-9f45-ed4e9f4f596c")
  assert(@res != nil, "Response from PUT operation was null")

  #re-get the app so we have updated data, like approval date
  restHttpGet(arg1)
  result = JSON.parse(@res.body)
  @registration = result["registration"]
end


Then /^I should no longer be able to get that application's data$/ do
  @format = "application/json"
  restHttpGet("/apps/#{@newId}")
  
  assert(@res != nil, "Response from PUT operation was null")
  assert(@res.code == 404, "Return code was not expected: "+@res.code.to_s+" but expected 404")
end

When /^I POST an application specifying an invalid field$/ do
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj["foo"] = "A Bar App"
  data = prepareData("application/json", dataObj)

  restHttpPost("/apps/", data)

  assert(@res != nil, "Response from POST operation was null")
end

When /^I POST an application specifying the auto\-generated field "([^"]*)"$/ do |arg1|
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj[arg1] = "fm67sH6vZZ"
  data = prepareData("application/json", dataObj)

  restHttpPost("/apps/", data)

  assert(@res != nil, "Response from POST operation was null")
end

When /^I PUT an application updating the auto\-generated field "([^"]*)"$/ do |arg1|
  @format = "application/json"
  uri = "/apps/d0b2ded4-89a9-db4a-8f80-aaece6fda529"
  restHttpGet(uri)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  
  prev_client_secret = result["client_secret"]
  prev_client_id = result["client_id"] 

  dataObj = DataProvider.getValidAppData()
  dataObj["client_secret"] = prev_client_secret 
  dataObj["client_id"] = prev_client_id 
  dataObj["client_secret"] = "thisIsAReallyBadLongClientSecretYarightorlyyarly" if arg1 == "client_secret"
  dataObj["client_id"] = "badclientId" if arg1 == "client_id"

  data = prepareData("application/json", dataObj)

  restHttpPut(uri, data)

  assert(@res != nil, "Response from PUT operation was null")
end

Then /^it should be "([^"]*)"$/ do |arg1|
  app = JSON.parse(@res.body)
  assert(app["registration"]["status"] === arg1, "Registration field should be #{arg1}, not #{app["registration"]["status"]}")
end

Then /^I should only see "([^"]*)" applications$/ do |arg1|
  apps = JSON.parse(@res.body)
  apps.each do |app|
    assert(app["registration"]["status"] === arg1, "App #{app["name"]} should be #{arg1}")
  end
end

Then /^the "([^"]*)" bootstrap app should exist$/ do |appName|
  apps = JSON.parse(@res.body)
  foundApp = false
  apps.each do |app|
    puts app["name"]
    if app["name"] == appName
        foundApp = true
        assert(app["bootstrap"] == true, "App #{appName} was not marked bootstrap")
    end
  end
  assert(foundApp, "Did not find #{appName}")
end

When /^I navigate to PUT "([^"]*)" to update an application's name$/ do |arg1|
  @format = "application/json"
  uri = "/apps/deb9a9d2-771d-40a1-bb9c-7f93b44e51df"
  restHttpGet(uri)
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  
  prev_client_secret = result["client_secret"]
  prev_client_id = result["client_id"] 

  dataObj = DataProvider.getValidAppData()
  dataObj["name"] = "Waffle App"
  data = prepareData("application/json", dataObj)

  restHttpPut(uri, data)

  assert(@res != nil, "Response from PUT operation was null")
end

Then /^I should only see "([^"]*)" and "([^"]*)" applications$/ do |arg1, arg2|
  apps = JSON.parse(@res.body)
  apps.each do |app|
    assert(app["registration"]["status"] === arg1 || app["registration"]["status"] === arg2, "App #{app["name"]} should be #{arg1}")
  end
end

When /^I navigate to PUT "([^"]*)" to update an application to "([^"]*)"$/ do |arg1, arg2|
  @format = "application/json"
  restHttpGet(arg1)
  app = JSON.parse(@res.body)
  app["registration"]["status"] = arg2
  data = prepareData("application/json", app)
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from PUT operation was null")
end
