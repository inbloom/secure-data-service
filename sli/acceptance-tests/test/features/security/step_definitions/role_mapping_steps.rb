require 'json'

require_relative '../../utils/sli_utils.rb'

Transform /^realm "([^"]*)"$/ do |arg1|
  id = "e5c12cb0-1bad-4606-a936-097b30bd47fe" if arg1 == "IL-Sunset"
  id = "4cfcbe8d-832d-40f2-a9ba-0a6f1daf3741" if arg1 == "Fake Realm"
  id = "45b03fa0-1bad-4606-a936-09ab71af37fe" if arg1 == "Another Fake Realm"
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
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

When /^I POST a new realm$/ do
  data = DataProvider.getValidRealmData()
  dataFormatted = prepareData("application/json", data)
  restHttpPost("/realm", dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client POST is nil")
end

Then /^I should receive a new ID for my new realm$/ do
  assert(@res.raw_headers["location"] != nil, "No ID was returned for created object")
end

When /^I GET a list of realms$/ do
  restHttpGet("/realm")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should see a list of valid realm objects$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")

  result.each do |item|
    assert(item["idp"] != nil, "Realm "+item["tenantId"]+" URL was not found.")
  end
end

When /^I GET a specific (realm "[^"]*")$/ do |arg1|
  restHttpGet("/realm/" + arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I PUT to change the (realm "[^"]*") to add a mapping between default role "([^"]*)" to role "([^"]*)"$/ do |arg1, arg2, arg3|
  restHttpGet("/realm/" + arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
  data = JSON.parse(@res.body)
 
  data["mappings"]["role"].each do |role|
    if role["sliRoleName"] == arg2
      role["clientRoleName"].push arg3
    end
  end
  
  dataFormatted = prepareData("application/json", data)
  
  restHttpPut("/realm/" + arg1, dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I add a mapping between non-existent role "([^"]*)" and custom role "([^"]*)" for (realm "[^"]*")$/ do |arg1, arg2, arg3|
  restHttpGet("/realm/" + arg3) 
  assert(@res != nil, "Response from rest-client GET is nil")
  data = JSON.parse(@res.body)
  print(data.inspect)
  
  data["mappings"]["role"].push({"sliRoleName"=>arg1,"clientRoleName"=>[arg2]})  
        
  dataFormatted = prepareData("application/json", data)
  
  restHttpPut("/realm/" + arg3, dataFormatted, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
end


When /^I DELETE the (realm "[^"]*")$/ do |arg1|
  restHttpDelete("/realm/" + arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

When /^I add a mapping between default role "([^"]*)" and custom role "([^"]*)" for realm "([^"]*)"$/ do |arg1, arg2, arg3|
  step "I PUT to change the realm \"#{arg3}\" to add a mapping between default role \"#{arg1}\" to role \"#{arg2}\""
end
