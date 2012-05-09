require 'json'

require_relative '../../utils/sli_utils.rb'

Transform /^realm "([^"]*)"$/ do |arg1|
  id = "4cb03fa0-83ad-46e2-a936-09ab31af377e" if arg1 == "SLI"
  id = "4cfcbe8d-832d-40f2-a9ba-0a6f1daf3741" if arg1 == "Fake Realm"
  id = "45b03fa0-1bad-4606-a936-09ab71af37fe" if arg1 == "Another Fake Realm"
  id
end

When /^I try to access the URI "([^"]*)" with operation "([^"]*)" and "([^"]*)" and "([^"]*)"$/ do |arg1, arg2, arg3, arg4|

  @format = "application/json"
  
  dataObj = {
      "stateOrganizationId" => arg3,
      "tenantId" => arg4,
    }

  data = prepareData("application/json", dataObj)
  
  restHttpPost(arg1, data) if arg2 == "POST"
  restHttpGet(arg1) if arg2 == "GET"
  restHttpPut(arg1, data) if arg2 == "PUT"
  restHttpDelete(arg1) if arg2 == "DELETE"
end

Then /^I should be denied access$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

Then /^I should see a top level ed org is created with "([^"]*)" is "([^"]*)"$/ do |key, value|
  uri="/v1/educationOrganizations"
  uri=uri+"?"+URI.escape(key)+"="+URI.escape(value)
  restHttpGet(uri)
  assert(@res.length>0,"didnt see a top level ed org with #{key} is #{value}")
 @edorgId=JSON.parse(@res.body)[0]["id"]
 
end

Then /^I should see this ed org is Authorized to use Apps "([^"]*)" and "([^"]*)"$/ do |app1, app2|
  uri ="/application"
  restHttpGet(uri)
  dataH=JSON.parse(@res.body)
  foundApp1=false
  foundApp2=false
  dataH.each do |app|
    if app["name"].include? app1 and app["authorized_ed_orgs"].include? @edorgId
      foundApp1=true
    end
    if app["name"].include? app2 and app["authorized_ed_orgs"].include? @edorgId
      foundApp2=true
    end
  end
  assert(foundApp1,"didnt see this ed org is authorized to use #{app1}")
  assert(foundApp2,"didnt see this ed org is authorized to use #{app2}")
end
