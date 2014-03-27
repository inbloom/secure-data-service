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
require 'mongo'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

UNIQUE_TENANT_ID_1 = "694132a09a05"
UNIQUE_TENANT_ID_2 ="e04161f09a09"
UNIQUE_TENANT_ID_3 = "4fa3fe8be4b00b3987bec778"
UNIQUE_ED_ORG_ID = "aabc8798d987s9e8987"

INGESTION_ZONE_PATH = Property['ingestion_remote_lz_path']

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+UNIQUE_TENANT_ID_2 if arg2 == "Testing Tenant"
  id = arg1+@newId if arg2 == "New Tenant UUID"
  id = arg1+UNIQUE_TENANT_ID_1 if arg2 == "New Tenant ID"
  id
end

############################################################
# STEPS: BEFORE
############################################################

Before do
  DbClient.new.for_sli.open do |db|
    db.remove(:tenant, {'body.tenantId' => UNIQUE_TENANT_ID_1})
    db.remove(:tenant, {'body.tenantId' => UNIQUE_TENANT_ID_2})
    db.remove(:tenant, {'body.tenantId' => UNIQUE_TENANT_ID_3})
  end
end

When /^I POST a new tenant$/ do
  dataObj =  {
    'landingZone' => [
      {
        'educationOrganization' => 'Sunset',
        'ingestionServer' => 'ingServIL',
        'path' => "#{INGESTION_ZONE_PATH}/lz/inbound/IL-STATE-SUNSET",
        'desc' => 'Sunset district landing zone',
        'userNames' => [ 'jwashington', 'jstevenson' ]
      },
      {
        'educationOrganization' => 'Daybreak',
        'ingestionServer' => 'ingServIL',
        'path' => "#{INGESTION_ZONE_PATH}/lz/inbound/IL-STATE-DAYBREAK",
        'desc' => 'Daybreak district landing zone',
        'userNames' => [ 'jstevenson' ]
      }
    ],
    'tenantId' => UNIQUE_TENANT_ID_1
  }
  data = prepareData('application/json', dataObj)
  restHttpPost '/tenants/', data
  @res.should_not be_nil, 'Response from POST operation was null'
end

When /^I provision a new landing zone$/ do
  dataObj = {
    'stateOrganizationId' => UNIQUE_ED_ORG_ID,
    'tenantId' => UNIQUE_TENANT_ID_1
  }
  data = prepareData('application/json', dataObj)
  restHttpPost '/provision/', data
  @res.should_not be_nil, 'Response from POST operation was null'
end

And /^I should receive the same tenant id$/ do
  headers = @res.raw_headers
  headers['location'].should_not be_nil, 'There is no location link from the previous request'
  s = headers['location'][0]
  newNewId = s[s.rindex('/')+1..-1]
  newNedId.should_not be_empty, 'After POST, tenant ID is blank'
  newNewId.should be(@newId), 'POSTing to tenant collection with same tenant Id does not result in the same guid'
end

Then /^I should receive the data for the specified tenant entry$/ do
  result = JSON.parse(@res.body)
  result.should_not be_nil, 'Result of JSON parsing is nil'
  @landing_zone = result[0]["landing_zone"]
  @tenant_id = result[0]["tenant_id"]
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  dataObj = {
      "landingZone" => [ 
        { 
          "educationOrganization" => "Daybreak",
          "ingestionServer" => "ingServIL",
          "path" => "#{INGESTION_ZONE_PATH}/lz/inbound/IL-STATE-DAYBREAK",
          "desc" => "Daybreak district landing zone",
          "userNames" => [ "rrogers" ]
        }
      ],
      "tenantId" => UNIQUE_TENANT_ID_1,
      "dbName" => UNIQUE_TENANT_ID_1
  }
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from PUT operation was null")
end

Then /^I should no longer be able to get that tenant's data$/ do
  restHttpGet "/tenants/#{@newId}"
  @res.should_not be_nil
  @res.code.should be(404)
end

When /^I PUT a tenant specifying an invalid field$/ do
  dataObj = DataProvider.getValidAppData()
  dataObj["foo"] = "A Bar Tenant"
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from POST operation was null")
end

def postToTenants(dataObj)
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/tenants/", data)
  assert(@res != nil, "Response from POST operation was null")
end

def postToProvision(dataObj)
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/provision/", data)
  assert(@res != nil, "Response from POST operation was null")
end

def getBaseTenant
  {
    'landingZone' => [ 
      { 
        'educationOrganization' => 'Twilight',
        'ingestionServer' => 'ingServIL',
        'path' => "#{INGESTION_ZONE_PATH}/lz/inbound/IL-STATE-TWILIGHT",
        'desc' => 'Twilight district landing zone',
        'userNames' => [ 'rrogers' ]
      }
    ],
    'tenantId' => UNIQUE_TENANT_ID_1
  }
end

def getBaseProvisionData
  {
    'stateOrganizationId' => 'Twilight',
    'tenantId' => UNIQUE_TENANT_ID_1
  }
end

When /^I POST a basic tenant with "([^"]*)" set to "([^"]*)"$/ do |property,value|
  dataObject = getBaseTenant
  dataObject[property] = value
  postToTenants(dataObject)
end

When /^I POST a provision request with "([^"]*)" set to "([^"]*)"$/ do |property,value|
  disable_NOTABLESCAN()
  dataObject = getBaseProvisionData
  dataObject[property] = value
  postToProvision(dataObject)
  enable_NOTABLESCAN()
end

When /^I POST a basic tenant with landingZone "([^"]*)" set to "([^"]*)"$/ do |property,value|
  dataObject = getBaseTenant
  dataObject["landingZone"][0][property] = value
  postToTenants(dataObject)
end

When /^I POST a basic tenant with userName "([^"]*)"$/ do |value|
  dataObject = getBaseTenant
  dataObject["landingZone"][0]["userNames"][0] = value
  postToTenants(dataObject)
end

When /^I PUT a basic tenant with userName "([^"]*)"$/ do |value|
  @result = @fields if !defined? @result
  @result[0]["landingZone"][0]["userNames"] = [value]
  data = prepareData(@format, @result[0])
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I POST a basic tenant with no userName$/ do
  dataObject = getBaseTenant
  dataObject["landingZone"][0].delete("userNames")
  postToTenants(dataObject)
end

When /^I POST a basic tenant with missing "([^"]*)"$/ do |property|
  dataObject = getBaseTenant
  dataObject.delete(property)
  postToTenants(dataObject)
end

When /^I PUT a basic tenant with missing "([^"]*)"$/ do |property| 
  @result = @fields if !defined? @result
  @result[0].delete(property)
  data = prepareData(@format, @result[0])
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I POST a provision request with missing "([^"]*)"$/ do |property|
  dataObject = getBaseProvisionData
  dataObject.delete(property)
  postToProvision(dataObject)
end

When /^I PUT a basic tenant with missing landingZone "([^"]*)"$/ do |property|
  @result = @fields if !defined? @result
  @result[0]["landingZone"][0].delete(property)
  data = prepareData(@format, @result[0])
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I PUT a basic tenant with "([^"]*)" set to "([^"]*)"$/ do |property,value|
  @result = @fields if !defined? @result
  @result[0][property] = value
  data = prepareData(@format, @result[0])
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from rest-client PUT is nil")  
end

When /^I PUT a basic tenant with landingZone "([^"]*)" set to "([^"]*)"$/ do |property,value|
  @result = @fields if !defined? @result
  @result[0]["landingZone"][0][property] = value
  data = prepareData(@format, @result[0])
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

Then /^I should receive a UUID$/ do 
  @newId = @result[0]['id']
  @newId.should_not be_nil, 'After GET, UUID is nil'
end


