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

INGESTION_DB_NAME = PropLoader.getProps['ingestion_database_name']
INGESTION_DB = PropLoader.getProps['ingestion_db']
INGESTION_DB_PORT = PropLoader.getProps['ingestion_db_port']
UNIQUE_TENANT_ID_1 = "694132a09a05"
UNIQUE_TENANT_ID_2 ="e04161f09a09"
UNIQUE_TENANT_ID_3 = "4fa3fe8be4b00b3987bec778"
UNIQUE_ED_ORG_ID = "aabc8798d987s9e8987"

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
  @conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)
  @mdb = @conn.db(INGESTION_DB_NAME)
  @tenantColl = @mdb.collection('tenant')
  @edOrgColl = @mdb.collection('educationOrganization')
  @ingestion_db_name = convertTenantIdToDbName('Midgar')
  
  # 2012-05-10: this is necessary to remove old style data from the tenant collection; 
  # it can go away once there is no lingering bad data anywhere
  disable_NOTABLESCAN()
  @tenantColl.find().each do |row|
    if row['tenantId'] != nil
      @tenantColl.remove(row)
    end
  end
  
  @tenantColl.remove({"body.tenantId" => UNIQUE_TENANT_ID_1})
  @tenantColl.remove({"body.tenantId" => UNIQUE_TENANT_ID_2})
  @tenantColl.remove({"body.tenantId" => UNIQUE_TENANT_ID_3})
  @edOrgColl.remove({"body.stateOrganizationId" => UNIQUE_ED_ORG_ID})
  enable_NOTABLESCAN()
  
end

When /^I POST a new tenant$/ do
  @format = "application/json;charset=utf-8"
  dataObj =  {
      "landingZone" => [ 
        { 
          "educationOrganization" => "Sunset",
          "ingestionServer" => "ingServIL",
          "path" => "/home/ingestion/lz/inbound/IL-STATE-SUNSET",
          "desc" => "Sunset district landing zone",
          "userNames" => [ "jwashington", "jstevenson" ]
        },
        {
          "educationOrganization" => "Daybreak",
          "ingestionServer" => "ingServIL",
          "path" => "/home/ingestion/lz/inbound/IL-STATE-DAYBREAK",
          "desc" => "Daybreak district landing zone",
          "userNames" => [ "jstevenson" ]
        }
      ],
      "tenantId" => UNIQUE_TENANT_ID_1
  }
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/tenants/", data)
  assert(@res != nil, "Response from POST operation was null")
end

When /^I provision a new landing zone$/ do
  @format = "application/json;charset=utf-8"
  dataObj = {
      "stateOrganizationId" => UNIQUE_ED_ORG_ID,
      "tenantId" => UNIQUE_TENANT_ID_1
  }
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/provision/", data)
  assert(@res != nil, "Response from POST operation was null")
end



And /^I should receive the same tenant id$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  newNewId = s[s.rindex('/')+1..-1]
  assert(newNewId != nil, "After POST, tenant ID is nil")
  assert(@newId == newNewId, "POSTing to tenant collection with same tenant Id does not result in the same guid")
end

Then /^I should receive the data for the specified tenant entry$/ do
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  @landing_zone = result[0]["landing_zone"]
  @tenant_id = result[0]["tenant_id"]
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  @format = "application/json;charset=utf-8"
  dataObj = {
      "landingZone" => [ 
        { 
          "educationOrganization" => "Daybreak",
          "ingestionServer" => "ingServIL",
          "path" => "/home/ingestion/lz/inbound/IL-STATE-DAYBREAK",
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

And /^I should see following map of indexes in the corresponding collections:$/ do |table|
  disable_NOTABLESCAN()
  @db   = @conn[@ingestion_db_name]

  @result = "true"

  table.hashes.map do |row|
    @entity_collection = @db.collection(row["collectionName"])
    @indexcollection = @db.collection("system.indexes")
    #puts "ns" + @ingestion_db_name+"student," + "name" + row["index"].to_s
    @indexCount = @indexcollection.find("ns" => @ingestion_db_name + "." + row["collectionName"], "name" => row["index"]).to_a.count()

    #puts "Index Count = " + @indexCount.to_s

    if @indexCount.to_s == "0"
      puts "Index was not created for " + @ingestion_db_name+ "." + row["collectionName"] + " with name = " + row["index"]
      @result = "false"
    end
  end

  assert(@result == "true", "Some indexes were not created successfully.")
  enable_NOTABLESCAN()

end


Then /^I should no longer be able to get that tenant's data$/ do
  @format = "application/json;charset=utf-8"
  restHttpGet("/tenants/#{@newId}")
  assert(@res != nil, "Response from PUT operation was null")
  assert(@res.code == 404, "Return code was not expected: "+@res.code.to_s+" but expected 404")
end

# TODO delete
When /^I POST a tenant specifying an invalid field$/ do
  @format = "application/json;charset=utf-8"
  dataObj = DataProvider.getValidAppData()
  dataObj["foo"] = "A Bar Tenant"
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/tenants/", data)
  assert(@res != nil, "Response from POST operation was null")
end

When /^I PUT a tenant specifying an invalid field$/ do
  @format = "application/json;charset=utf-8"
  dataObj = DataProvider.getValidAppData()
  dataObj["foo"] = "A Bar Tenant"
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPut("/tenants/#{@newId}", data)
  assert(@res != nil, "Response from POST operation was null")
end


def postToTenants(dataObj)
  @format = "application/json;charset=utf-8"
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/tenants/", data)
  assert(@res != nil, "Response from POST operation was null")
end

def postToProvision(dataObj)
  @format = "application/json;charset=utf-8"
  data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost("/provision/", data)
  assert(@res != nil, "Response from POST operation was null")
end

def getBaseTenant
    return {
      "landingZone" => [ 
        { 
          "educationOrganization" => "Twilight",
          "ingestionServer" => "ingServIL",
          "path" => "/home/ingestion/lz/inbound/IL-STATE-TWILIGHT",
          "desc" => "Twilight district landing zone",
          "userNames" => [ "rrogers" ]
        }
      ],
      "tenantId" => UNIQUE_TENANT_ID_1
  }
end

def getBaseProvisionData
    return {
      "stateOrganizationId" => "Twilight",
      "tenantId" => UNIQUE_TENANT_ID_1
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
  assert(@newId != nil, "After GET, UUID is nil")
end


