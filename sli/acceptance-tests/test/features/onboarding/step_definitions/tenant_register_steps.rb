require_relative '../../utils/sli_utils.rb'
require 'mongo'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

INGESTION_DB_NAME = PropLoader.getProps['ingestion_database_name']
INGESTION_DB = PropLoader.getProps['ingestion_db']
UNIQUE_TENANT_ID_1 = "694132a09a05"
UNIQUE_TENANT_ID_2 ="e04161f09a09"
UNIQUE_TENANT_ID_3 = "4fa3fe8be4b00b3987bec778"

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+UNIQUE_TENANT_ID_2 if arg2 == "Testing Tenant"
  id = arg1+@newId       if arg2 == "New Tenant ID"
  id
end

############################################################
# STEPS: BEFORE
############################################################

Before do
  @conn = Mongo::Connection.new(INGESTION_DB)
  @mdb = @conn.db(INGESTION_DB_NAME)
  @tenantColl = @mdb.collection('tenant')
  puts INGESTION_DB
  puts INGESTION_DB_NAME
  @tenantColl.remove({"body.tenantId" => UNIQUE_TENANT_ID_1})
  @tenantColl.remove({"body.tenantId" => UNIQUE_TENANT_ID_2})
  @tenantColl.remove({"body.tenantId" => UNIQUE_TENANT_ID_3})
end

When /^I POST a new tenant$/ do
  @format = "application/json"
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
  data = prepareData("application/json", dataObj)
  restHttpPost("/tenants/", data)
  assert(@res != nil, "Response from POST operation was null")
end

When /^I rePOST the new tenant$/ do
  @format = "application/json"
  dataObj = {
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
  data = prepareData("application/json", dataObj)
  restHttpPost("/tenants/", data)
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
  @landing_zone = result["landing_zone"]
  @tenant_id = result["tenant_id"]
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  @format = "application/json"
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
      "tenantId" => UNIQUE_TENANT_ID_1
  }
  data = prepareData("application/json", dataObj)
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from PUT operation was null")
end

Then /^I should no longer be able to get that tenant's data$/ do
  @format = "application/json"
  restHttpGet("/tenants/#{@newId}")
  assert(@res != nil, "Response from PUT operation was null")
  assert(@res.code == 404, "Return code was not expected: "+@res.code.to_s+" but expected 404")
end

When /^I POST a tenant specifying an invalid field$/ do
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj["foo"] = "A Bar Tenant"
  data = prepareData("application/json", dataObj)
  restHttpPost("/tenants/", data)
  assert(@res != nil, "Response from POST operation was null")
end

def postToTenants(dataObj)
  @format = "application/json"
  data = prepareData("application/json", dataObj)
  restHttpPost("/tenants/", data)
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

When /^I POST a basic tenant with "([^"]*)" set to "([^"]*)"$/ do |property,value|
  dataObject = getBaseTenant
  dataObject[property] = value
  postToTenants(dataObject)
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

When /^I POST a basic tenant with missing landingZone "([^"]*)"$/ do |property|
  dataObject = getBaseTenant
  dataObject["landingZone"][0].delete(property)
  postToTenants(dataObject)
end

