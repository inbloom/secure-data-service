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


require 'mongo'

Transform /school "(.*?)"/ do |arg1|
  id = "92d6d5a0-852c-45f4-907a-912752831772" if arg1 == "Daybreak Central High"
  id = "6756e2b9-aba1-4336-80b8-4a5dde3c63fe" if arg1 == "Sunset Central High"
  id
end

Transform /the realm "([^"]*)"/ do |realm|
  realmId = "45b02cb0-1bad-4606-a936-094331bd47fe" if realm == "Daybreak"
  realmId
end

Then /^I should see only myself$/ do
  begin
    @driver.find_element(:id, 'simple-table')
  rescue Exception => e
    assert(true, "We should not have found a table of entries")
  else
    assert(false, "We should not have found a table of entries")
  end
end

Then /^I get message that I am not authorized$/ do
  assertWithWait("Should have received message that databrowser could not be accessed") { @driver.page_source.index("You are not authorized to use this app." ) != nil}
end

When /^I should navigate to "([^"]*)"$/ do |page|
  @driver.get(PropLoader.getProps['databrowser_server_url'] + page)
end

Then /^I should see that there are "([^"]*)" teachers$/ do |expectedNumTeachers|
  expected = Integer(expectedNumTeachers)
  table = @driver.find_element(:id, "simple-table")
  rows = table.find_elements(:xpath, ".//tr")
  #-1 because of thead
  total = -1
  rows.each do |row|
    total += 1
  end
  assert(total == expected, "Expected #{expectedNumTeachers}, found #{total}")
end

Then /^I should see that there are no teachers$/ do 
  table = @driver.find_element(:id, "simple-table")
  rows = table.find_elements(:xpath, ".//tr")
  message = ""
  rows.each do |row|
    tds = row.find_elements(:xpath, ".//td")
    message = tds[0].text if tds.length == 1 and tds[0] != nil
  end
  assert(message == "No data available in table", "Should not find any teachers available.")
end

When /^I navigate to see the teachers in the (school ".*?")$/ do |arg1|
  page = "/entities/educationOrganizations/#{arg1}/teacherSchoolAssociations/teachers"
  @driver.get(PropLoader.getProps['databrowser_server_url'] + page)
end

Given /^I remove the application authorizations in sunset$/ do
  disable_NOTABLESCAN()
  dissallowDatabrowser("IL-SUNSET", "Midgar")  
  enable_NOTABLESCAN()
end

Given /^I remove the application authorizations in daybreak/ do
  disable_NOTABLESCAN()
  dissallowDatabrowser("IL-DAYBREAK", "Midgar")
  enable_NOTABLESCAN()
end

Given /^I change the isAdminRole flag for role "(.*?)" to in (the realm ".*?") to be "(.*?)"$/ do |role, realm, isAdminRole|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'])
  db = conn.db(convertTenantIdToDbName('Midgar'))
  coll = db.collection('customRole')
  customRoleDoc = coll.find_one({"body.realmId" => realm})
  coll.remove({"body.realmId" => realm})
  customRoleDoc["body"]["roles"].each do |curRole|
    if curRole["groupTitle"] == role
      curRole["isAdminRole"] = isAdminRole == "true" ? true : false
    end
  end
  coll.insert(customRoleDoc)
  conn.close
  enable_NOTABLESCAN()
end

def dissallowDatabrowser(district, tenantName)
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'], PropLoader.getProps['DB_PORT'])
  db = conn[PropLoader.getProps['api_database_name']]
  appColl = db.collection("application")
  databrowserId = appColl.find_one({"body.name" => "inBloom Data Browser"})["_id"]
  puts("The databrowser id is #{databrowserId}") if ENV['DEBUG']
  
  dbTenant = conn[convertTenantIdToDbName(tenantName)]
  appAuthColl = dbTenant.collection("applicationAuthorization")
  edOrgColl = dbTenant.collection("educationOrganization")

  edOrg = edOrgColl.find_one({"body.stateOrganizationId" => district})
  if edOrg != nil
    puts("Found edOrg: #{edOrg.inspect}") if ENV['DEBUG']
    districtId = edOrg["_id"]
    edOrgIds = [districtId]
    edOrgColl.find({"body.parentEducationAgencyReference" => districtId}).each do |edorg|
      edOrgIds.push(edorg["_id"])
    end
    existingAppAuth = appAuthColl.find_one({"body.applicationId" => databrowserId})
    if existingAppAuth != nil
      existingAppAuth["body"]["edorgs"] = existingAppAuth["body"]["edorgs"] - edOrgIds
      puts("About to update #{existingAppAuth.inspect}") if ENV['DEBUG']
      appAuthColl.update({"body.applicationId" => databrowserId}, existingAppAuth)
    else
      puts("District #{edOrgId} already denies all apps") if ENV['DEBUG']
    end
  else
    puts("District #{district} not found") if ENV['DEBUG']
  end
  conn.close if conn != nil
end


