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


require "mongo"

require_relative '../../../utils/sli_utils.rb'

PRELOAD_EDORG = "STANDARD-SEA"

Before do
  @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
end

After do |scenario|
begin
tenant_coll = @db['tenant']
tenant_coll.remove({"body.tenantId" => @tenantId})

edorg_coll = @db["educationOrganization"]
edorg_coll.remove({"body.stateOrganizationId" => @edorgId})
rescue
end
end

Given /^There is a Tenant with tenantId "(.*?)" in mongo$/ do |tenantId|
create_tenant(tenantId)
@tenantId = tenantId
end

Given /^There is a EdOrg with stateOrganizationId "(.*?)" in mongo$/ do |edorgId|
  disable_NOTABLESCAN()

  create_edorg(edorgId)
  @edorgId = edorgId

  enable_NOTABLESCAN()
end

Given /^the tenant has a landing zone path for this edorg$/ do
  update_landingzone
end

When /^I navigate to POST "(.*?)"$/ do |url|
preload_url=url.gsub("tenant_UUID", @tenant_uuid)
puts preload_url
 
 @format = "application/json"
  
 # data = prepareData("application/json;charset=utf-8", dataObj)
  restHttpPost(preload_url, "small")
  assert(@res != nil, "Response from POST operation was null")

end

def create_tenant (tenantId)
tenant_entity = {
      "_id" => "2012fy-a82073df-b9ba-11e1-a6ba-68a86d3e1111",
      "type" => "tenant",
      "body" => {
          "tenantId" => tenantId
          }
  }
 tenant_coll = @db['tenant']
 tenant_coll.remove({"body.tenantId" => tenantId})
 tenant_coll.save(tenant_entity)
 @tenant_uuid = "2012fy-a82073df-b9ba-11e1-a6ba-68a86d3e1111"
end

def create_edorg (edorgId)
  edorg_entity = {
      "_id" => "2012fy-a82073df-b9ba-11e1-a6ba-68a86d3e2222",
      "type" => "educationOrganization",
      "body" => {
          "organizationCategories" => [
              "State Education Agency"
          ],
          "address" => [
              {
                  "postalCode" => "27713",
                  "streetNumberName" => "unknown",
                  "stateAbbreviation" => "NC",
                  "city" => "unknown"
              }
          ],
          "stateOrganizationId" => edorgId,
          "nameOfInstitution" => edorgId
      },
      "metaData" => {
          "tenantId" => @tenantId
      }
  }
  edorg_coll = @db["educationOrganization"]
  edorg_coll.remove({"body.stateOrganizationId" => edorgId})
  edorg_coll.save(edorg_entity)
end

def update_landingzone
tenant_coll = @db['tenant']
tenant_coll.update(
{"body.tenantId" => @tenantId}, {"body" => 
{"tenantId" => @tenantId,
"landingZone" =>
 [
 {"educationOrganization" => @edorgId,
 "ingestionServer" => "testserver",
 "path" => "/home/ingestion/lz/inbound/sandboxadministrator@slidev.org",
 "desc" => nil,
 "userNames" => nil}
 ] 
 } })
end






