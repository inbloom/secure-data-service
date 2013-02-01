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

Transform /^IDs for "([^"]*)"$/ do |idCategory|
    expectedIds = ["e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b", 
      "e9ca4497-e1e5-4fc4-ac7b-24badbad998b", 
      "edce823c-ee28-4840-ae3d-74d9e9976dc5"] if idCategory == "Daybreak and Sunset"
    expectedIds = ["edce823c-ee28-4840-ae3d-74d9e9976dc5",
      "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b"] if idCategory == "Sunset only"
    expectedIds
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

Then /^I should get the (IDs for "[^"]*")$/ do |expectedIds|
  table = @driver.find_element(:id, "simple-table")
  rows = table.find_elements(:xpath, ".//tr")
  headings = rows[0].find_elements(:xpath, ".//th")
  idIndex = 0
  headings.each do |heading| 
    if heading.text == "Id"
      break
    end
    idIndex += 1
  end
   
  actualIds = []
  rows.each do |row|
      tds = row.find_elements(:xpath, ".//td")
      actualIds.push(tds[idIndex].text) if tds.length == headings.length and tds[idIndex] != nil
  end
  assert(actualIds.sort == expectedIds.sort, "Was expecting a different set of IDs, received #{actualIds.inspect}")
end

Given /^I remove the application authorizations in sunset$/ do
  disable_NOTABLESCAN()
  coll()
  @sunset = "b2c6e292-37b0-4148-bf75-c98a2fcc905f"
  $oldSunsetAuth = @coll.find_one({"body.authId" => @sunset})
  newSunsetAuth = @coll.find_one({"body.authId" => @sunset})
  @coll.remove({"body.authId" => @sunset})
  newSunsetAuth["body"]["appIds"] = []
  @coll.insert(newSunsetAuth)
  enable_NOTABLESCAN()
end

Given /^I remove the application authorizations in daybreak/ do
  disable_NOTABLESCAN()
  coll()
  @daybreak = "bd086bae-ee82-4cf2-baf9-221a9407ea07"
  $oldDaybreakAuth = @coll.find_one({"body.authId" => @daybreak})
  newDaybreakAuth = @coll.find_one({"body.authId" => @daybreak})
  @coll.remove({"body.authId" => @daybreak})
  newDaybreakAuth["body"]["appIds"] = []
  @coll.insert(newDaybreakAuth)
  enable_NOTABLESCAN()
end

Then /^I put back the application authorizations in sunset$/ do
  disable_NOTABLESCAN()
  @sunset = "b2c6e292-37b0-4148-bf75-c98a2fcc905f"
  coll()
  @coll.remove({"body.authId" => @sunset})
  @coll.insert($oldSunsetAuth)
  enable_NOTABLESCAN()
end

Then /^I put back the application authorizations in daybreak/ do
  disable_NOTABLESCAN()
  @daybreak = "bd086bae-ee82-4cf2-baf9-221a9407ea07"
  coll()
  @coll.remove({"body.authId" => @daybreak})
  @coll.insert($oldDaybreakAuth)
  enable_NOTABLESCAN()
end

def coll
  disable_NOTABLESCAN()
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db(convertTenantIdToDbName('Midgar'))
  @coll ||= @db.collection('applicationAuthorization')
  return @coll
  enable_NOTABLESCAN()
end

Given /^I change the isAdminRole flag for role "(.*?)" to in (the realm ".*?") to be "(.*?)"$/ do |role, realm, isAdminRole|
  disable_NOTABLESCAN()
  db = Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  coll = db.collection('customRole')
  customRoleDoc = coll.find_one({"body.realmId" => realm})
  coll.remove({"body.realmId" => realm})
  customRoleDoc["body"]["roles"].each do |curRole|
    if curRole["groupTitle"] == role
      curRole["isAdminRole"] = isAdminRole == "true" ? true : false
    end
  end
  coll.insert(customRoleDoc)
  enable_NOTABLESCAN()
end



