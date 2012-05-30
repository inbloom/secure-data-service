require 'mongo'

Transform /^IDs for "([^"]*)"$/ do |idCategory|
    expectedIds = ["edce823c-ee28-4840-ae3d-74d9e9976dc5",
        "67ed9078-431a-465e-adf7-c720d08ef512",
        "bcfcc33f-f4a6-488f-baee-b92fbd062e8d",
        "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b"] if idCategory == "Daybreak and Sunset"
    expectedIds = ["67ed9078-431a-465e-adf7-c720d08ef512",
        "bcfcc33f-f4a6-488f-baee-b92fbd062e8d",
        "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b"] if idCategory == "Daybreak only"
    expectedIds
end

When /^I should navigate to "([^"]*)"$/ do |page|
  @driver.get(PropLoader.getProps['databrowser_server_url'] + "/entities/teachers")
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
  coll()
  @sunset = "IL-SUNSET"
  $oldSunsetAuth = @coll.find_one({"body.authId" => @sunset})
  newSunsetAuth = @coll.find_one({"body.authId" => @sunset})
  @coll.remove({"body.authId" => @sunset})
  newSunsetAuth["body"]["appIds"] = []
  @coll.insert(newSunsetAuth)
end

Then /^I put back the application authorizations in sunset$/ do
  @sunset = "IL-SUNSET"
  coll()
  @coll.remove({"body.authId" => @sunset})
  @coll.insert($oldSunsetAuth)
end

def coll
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  @coll ||= @db.collection('applicationAuthorization')
  return @coll
end


