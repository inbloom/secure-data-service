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


require "selenium-webdriver"
require 'json'
require 'time'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require_relative '../../ingestion/features/step_definitions/ingestion_steps.rb'


DATABASE_NAME = PropLoader.getProps['sli_database_name']

Then /^The following edOrgs are authorized for the application "(.*?)" in tenant "(.*?)"$/ do |application, tenant, table|
    disable_NOTABLESCAN()
    @db = @conn['02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a']
    @slidb = @conn[DATABASE_NAME]
    
    @results = "true"
    edOrgsArray ||= []
    
    table.hashes.map do |row|
        @entity_collection = @db[row["edorgs"]]
        
        coll = @db.collection("applicationAuthorization")
        col2 = @db.collection("educationOrganization")
        col3 = @slidb.collection("application")
        applicationEntity = col3.find_one({'body.name' => application})
        applicationId = applicationEntity ['_id']
        eduOrgEntity = col2.find_one({'body.nameOfInstitution' => row["edorgs"]})
        stateOrganizationId = eduOrgEntity['_id']
        
        edOrgsArray.push(stateOrganizationId)
        edOrgsArray.sort
        record = coll.find_one({"$and" => [{'body.applicationId'=> applicationId}, {'body.edorgs' => stateOrganizationId}] })
        recordBody = record['body']
        @recordEdorgs = recordBody['edorgs']
        #record = coll.find_one({"$and" => [{'body.applicationId'=> application}, {'body.edorgs' => row["edorgs"]}] })
        if record != nil
            assert(@results == "true", "applicationAuthorization record is found!")
            else
            @results= "false"
            assert(@results == "false", "applicationAuthorization record is not found!")
        end
        
    end
    @diff = edOrgsArray <=> @recordEdorgs
    if  @diff == 0
        assert(@results =="true", "edorgs match mongo database!")
        else
        assert(@results =="false", "edorgs does not match ")
    end
    enable_NOTABLESCAN()

end




When /^I hit the Admin Application Authorization Tool$/ do
  #XXX - Once the API is ready, remove the ID
  @driver.get(PropLoader.getProps['admintools_server_url']+"/application_authorizations/")
end

Then /^I am redirected to the Admin Application Authorization Tool$/ do
  assertWithWait("Failed to navigate to the Admintools App Registration page")  {@driver.page_source.index("application_authorizations") != nil}
end

Then /^I am redirected to the Admin Application Authorization Edit Page$/ do
  actualUrl = @driver.current_url
  expectedRegex = "/application_authorizations/.*/edit"
  assertWithWait("Failed to navigate to the Admintools App Authorization Edit page: URL '" + actualUrl + "' does match '" + expectedRegex + "'")  {actualUrl.match(expectedRegex) != nil}
end

Then /^I see a label in the middle "([^"]*)"/ do |arg1|
  #We're changing how the ID is referenced, so the label for the time-being isn't going to be accurate
  #assert(@driver.page_source.index(arg1) != nil)
end

Then /^I see the list of all available apps on SLI$/ do
  @appsTable = @driver.find_element(:class, "AuthorizedAppsTable")
  assert(@appsTable != nil  )
end

Then /^the authorized apps for my district are colored green$/ do
  approved = @appsTable.find_elements(:xpath, ".//tbody/tr/td[text()='Approved']")
  approved.each do |currentStatus|
    assert(currentStatus.attribute(:id) == "approvedStatus", "App is not the right color, should be green")
  end
end

Then /^the unauthorized are colored red$/ do
  notApproved = @appsTable.find_elements(:xpath, ".//tbody/tr/td[text()='Not Approved']")
  notApproved.each do |currentStatus|
    assert(currentStatus.attribute(:id) == "notApprovedStatus", "App is not the right color, should be red")
  end
end

 Then /^are sorted by '([^']+)'$/ do |columnName|
  tableHeadings = @appsTable.find_elements(:xpath, ".//thead/tr/th")
  index = -1
  tableHeadings.each do |arg|
    index = tableHeadings.index(arg) + 1 if arg.text == columnName
  end
  assert(index >= 0, "Cannot find column name '" + columnName + "'")
  rows = @appsTable.find_elements(:xpath, ".//tbody/tr")
  inApprovedSection = true
  last_td = nil
  rows.each do |curRow| 
    td = curRow.find_element(:xpath, "//td[#{index}]").text
    if !last_td.nil?
      assert(td.casecmp(last_td) >= 0, "Values in column '" + columnName + "' not sorted as expected: '" + td + "' < '" + last_td + "'")
    end
    last_td = td
  end
end

Then /^I see the Name, Version, Vendor and Status of the apps$/ do
  expectedHeadings = ["Name", "Version", "Vendor", "Approval Status", ""]
  tableHeadings = @appsTable.find_elements(:xpath, ".//tr/th")
  actualHeadings = []
  tableHeadings.each do |heading|
    if (heading.text.index("District") != 0)
      #The first th will contain the district's name
      actualHeadings.push(heading.text)
    end
  end    
  assert(expectedHeadings.sort == actualHeadings.sort, "Headings are different, found #{actualHeadings.inspect} but expected #{expectedHeadings.inspect}")
end

Given /^I am a valid SEA\/LEA user$/ do
end

Then /^I get message that I am not authorized$/ do
  isForbidden = @driver.find_element(:xpath, '//title[text()="Not Authorized (403)"]')
  assert(isForbidden != nil)
end

Then /^I do not get message that I am not authorized$/ do
  isForbidden = nil
  begin
    isForbidden = @driver.find_element(:xpath, '//title[text()="Not Authorized (403)"]')
  rescue Exception => e
    #expected
    assert(isForbidden == nil)
  else
    assert(isForbidden == nil)
  end
end

Then /^I am not logged into the application$/ do
  step "I hit the Admin Application Authorization Tool"
end


Given /^I am logged into the Application Authorization Tool$/ do
end

Given /^I see an application "([^"]*)" in the table$/ do |arg1|
    @appName = arg1
    @appRow = getApp(@appName)
    #t = Time.now.getutc.to_s
    #puts t
    #apps = @driver.find_elements(:xpath, ".//tbody/tr/td[text()='#{arg1}']/..")
    apps = @driver.find_elements(:xpath, './/tbody/tr/td[contains(.,"' + arg1 +'")]')

    apps.each do |cur|
        puts("The app is #{cur.inspect} and #{cur.text}")
    end
    assert(apps != nil)
end

Given /^in Status it says "([^"]*)"$/ do |arg1|
  statusIndex = 4
  
  @appRow = getApp(@appName)
  actualStatus = @appRow.find_element(:xpath, ".//td[#{statusIndex}]").text
  assert(actualStatus == arg1, "Expected status of #{@appName} to be #{arg1} instead it's #{actualStatus.inspect}")
end

# TODO: assert fail if no button is matched and then clicked
Given /^I click on the "([^"]*)" button next to it$/ do |arg1|
  inputs = @appRow.find_elements(:xpath, ".//td/form/input")
  inputs.each do |cur|
    if cur.attribute(:value) == arg1
      cur.click
      break
    end
  end
end

# FIXME: All callers of this assertion should actually match the text in the popup
# by using the improved regex assertion below (and not ignore exceptions)
Given /^I am asked 'Do you really want this application to access the district's data'$/ do
      begin
        @driver.switch_to.alert
      rescue
      end
end

# Match text of expected alert box and switch focus for subsequent "Ok" click to dismiss it
Given /^I switch focus to the popup matching the regex "([^"]*)"$/ do |expectedRegex|
  alertText = @driver.switch_to.alert.text
  assert(alertText.match(expectedRegex))
end

# TODO actually check the app is authed for the specified edorg?!
Then /^the application is authorized to use data of "([^"]*)"$/ do |arg1|
  row = getApp(@appName)
  assert(row != nil)
end

Then /^is put on the top of the table$/ do
  rows = @driver.find_elements(:xpath, ".//tbody/tr/td/..")
  rows.each do |curRow|
    if curRow.displayed?
      @row = curRow
      break
    end
  end
  assert(@row.find_element(:xpath, ".//td[1]").text == @appName, "The approved application should have moved to the top")
end

Then /^the app "([^"]*)" Status becomes "([^"]*)"$/ do |app, arg1|
  @row = getApp(app)
  assert(@row.displayed?, "#{app} should be present and visible")
  assertWithWait("Status should have switched to #{arg1}"){  @row.find_element(:xpath, ".//td[4]").text == arg1} 
end

Then /^the app "([^"]*)" Status matches "([^"]*)"$/ do |app, regex|
  @row = @appRow = getApp(app)
  assert(@row.displayed?, "#{app} should be present and visible")
  text = @row.find_element(:xpath, ".//td[4]").text
  assertWithWait("Status text '" + text + "' should match regex '#{regex}'"){  text.match(regex)} 
end

Then /^it is colored "([^"]*)"$/ do |arg1|
  status = @row.find_element(:xpath, ".//td[4]")
  if arg1 == "green"
    assert(status.attribute(:id) == "approvedStatus", "Should be colored green, instead ID is #{status.attribute(:id)}")
  elsif arg1 == "red"
    assert(status.attribute(:id) == "notApprovedStatus", "Should be colored red, instead ID is #{status.attribute(:id)}")
  end
end

Then /^the Approve button next to it is disabled$/ do
  @row = @appRow if @row.nil?
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Approve"
      assert(input.attribute(:disabled) == "true", "Approve button should be disabled")
    end
  end
end

Then /^the Deny button next to it is enabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Deny"
      assert(input.attribute(:disabled) != "true", "Deny button should be enabled")
    end
  end
end

Given /^I am asked 'Do you really want deny access to this application of the district's data'$/ do
    @driver.switch_to.alert
end

Then /^the application is denied to use data of "([^"]*)"$/ do |arg1|
  row = getApp(@appName)
  assertWithWait("wait added to ensure appropriate time in CI for update to occur") {row != nil}
end

Then /^the Approve button next to it is enabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Approve"
      assert(input.attribute(:disabled) != "true", "Approve button should be enabled")
    end
  end
end

Then /^the Deny button next to it is disabled$/ do
  @inputs = @row.find_elements(:xpath, ".//td/form/input")
  @inputs.each do |input|
    if input.attribute(:value) == "Deny"
      assert(input.attribute(:disabled) == "true", "Deny button should be disabled")
    end
  end
end

Then /^I see that it has an alert about bulk extract$/ do
  app = @driver.find_element(:xpath, ".//tbody/tr/td/div")
  assert(!app.nil?, "There should be #{@appName} with a warning about Bulk Extract")
end

Given /^I am a valid District Super Administrator for "([^"]*)"$/ do |arg1|
  #No code needed
end

Given /^I am an authenticated District Super Administrator for "([^"]*)"$/ do |arg1|
  step "I have an open web browser"
  step "I hit the Admin Application Authorization Tool"
  step "I was redirected to the \"Simple\" IDP Login page"
  step "I submit the credentials \"sunsetadmin\" \"sunsetadmin1234\" for the \"Simple\" login page"
  step "I am redirected to the Admin Application Authorization Tool"
end

private
def getApp(name)
  rows = @driver.find_elements(:xpath, ".//tbody/tr")
  results = []
  for row in rows
    appName = row.find_elements(:xpath, 'td')
    fixedName = appName[0].text.sub(/Bulk Extract application request/,"").strip
    if fixedName == name
      return row      
    end
  end
  return nil
end

Then /^there are "(.*?)" edOrgs for the "(.*?)" application in the applicationAuthorization collection for the "(.*?)" tenant$/ do |expected_count, application, tenant|
   disable_NOTABLESCAN()
   db = @conn.db("sli")
   coll = db.collection("application")
   record = coll.find_one("body.name" => application)
   appId = record["_id"]
   db = @conn[convertTenantIdToDbName(tenant)]
   coll = db.collection("applicationAuthorization")
   record = coll.find_one("body.applicationId" => appId.to_s)
   body = record["body"]
   edorgsArray = body["edorgs"]
   edorgsArrayCount = edorgsArray.count
   assert(edorgsArrayCount == expected_count.to_i, "Education organization count mismatch in applicationAuthorization collection. Expected #{expected_count}, actual #{edorgsArrayCount}")
   enable_NOTABLESCAN()
end

When /^I click Update$/ do
  @driver.find_element(:css, 'input:enabled[type="submit"]').click
end

Then /^I authorize the educationalOrganization "(.*?)"$/ do |edOrgName|
  disable_NOTABLESCAN()
  db = @conn[convertTenantIdToDbName("Midgar")]
  coll = db.collection("educationOrganization")
  record = coll.find_one("body.nameOfInstitution" => edOrgName.to_s)
  #puts record.to_s
  edOrgId = record["_id"]
  #puts edOrgId.to_s
  app = @driver.find_element(:id, edOrgId.to_s).click
  enable_NOTABLESCAN()
end

Then /^the checkbox with HTML id "([^"]*?)" is (checked|unchecked)$/ do |id,status|
  elt = @driver.find_element(:css, 'input#' + id + '[type="checkbox"]')
  assert(elt, "Checkbox with id '" + id + "' not found")
  selected = elt.selected?
  assert(status == "checked" && selected || status == "unchecked" && !selected, "Expected checkbox id '" + id + "' to be " + status + ", but WebDriver.isSelected gives '" + selected.to_s() + "'")
end

When /^I (check|uncheck) the checkbox with HTML id "([^"]*?)"$/ do |action,id|
  elt = @driver.find_element(:css, 'input#' + id + '[type="checkbox"]')
  assert(elt, "Checkbox with id '" + id + "' not found")
  assert(action == "check" && !elt.selected? || action == "uncheck" && elt.selected?, "Cannot " + action + " checkbox with id '" + id + "' whose checked status is " + elt.selected?.to_s())
  elt.click()
end

Then /^I de-authorize the educationalOrganization "(.*?)"$/ do |edOrgName|
  step "I authorize the educationalOrganization \"#{edOrgName}\""
end

Then /^I enable the education Organization "(.*?)"$/ do |edOrgName|
    step "I authorize the educationalOrganization \"#{edOrgName}\""
end

Then /^there are "(.*?)" educationalOrganizations in the targetEdOrgList of securityEvent "(.*?)"$/ do |expected_count, logMessage|
  disable_NOTABLESCAN()
  db = @conn.db("sli")
  coll = db.collection("securityEvent")
  record = coll.find_one({'body.logMessage' => logMessage})
  #puts record.to_s
  body = record["body"]
  #puts body.to_s
  targetEdOrgList = body["targetEdOrgList"]
  #puts targetEdOrgList.to_s
  targetEdOrgListCount = targetEdOrgList.count
  #puts targetEdOrgListCount
  assert(targetEdOrgListCount == expected_count.to_i, "targetEdOrgList count mismatch in securityEvent collection. Expected #{expected_count}, actual #{targetEdOrgListCount}")
  enable_NOTABLESCAN()
end

Then /^there are "(.*?)" educationalOrganizations in the targetEdOrgList$/ do |expected_count|
  disable_NOTABLESCAN()
  db = @conn.db("sli")
  coll = db.collection("securityEvent")
  record = coll.find_one()
  #puts record.to_s
  body = record["body"]
  #puts body.to_s
  targetEdOrgList = body["targetEdOrgList"]
  #puts targetEdOrgList.to_s
  targetEdOrgListCount = targetEdOrgList.count
  #puts targetEdOrgListCount
  assert(targetEdOrgListCount == expected_count.to_i, "targetEdOrgList count mismatch in securityEvent collection. Expected #{expected_count}, actual #{targetEdOrgListCount}")
  enable_NOTABLESCAN()
end

When /^I deselect hierarchical mode$/ do
  app = @driver.find_element(:id, "hierarchical_mode").click
end

When /^I select hierarchical mode$/ do
  app = @driver.find_element(:id, "hierarchical_mode").click
end

When /^I expand all nodes$/ do
  element = @driver.find_element(:id, 'expand_all').click
end

When /^I collapse all nodes$/ do
  element = @driver.find_element(:id, 'collapse_all').click
end

Then /^I see "(.*?)" checkbox for "(.*?)"$/ do |expectedCount, edOrgName|
   disable_NOTABLESCAN()
   db = @conn[convertTenantIdToDbName("Midgar")]
   coll = db.collection("educationOrganization")
   record = coll.find_one("body.nameOfInstitution" => edOrgName.to_s)
   edOrgId = record["_id"]
   actualCount = @driver.find_elements(:id, edOrgId.to_s).count
   assert(expectedCount == actualCount.to_s, "Count of checkboxes mismatched. Expecting #{expectedCount}, actual #{actualCount}")
   enable_NOTABLESCAN()
end

Then /^I see "(.*?)" occurrences of "(.*?)"$/ do |expectedCount, label|
   labels = @driver.find_elements(:xpath, './/span[contains(.,"' + label +'")]')
   actualCount = labels.count
   assert(expectedCount == actualCount.to_s, "Count of labels mismatched. Expecting #{expectedCount}, actual #{actualCount}")
end


Then /^those edOrgs enabled by the developer should be selectable for application "(.*?)" in tenant "(.*?)"$/ do |application, tenant|
  disable_NOTABLESCAN()
  db = @conn["sli"]
  coll = db.collection("application")
  app = coll.find_one("body.name" => application)
  edorgs = app["body"]["authorized_ed_orgs"]
  edorgs.each do |edOrgId|
    element = @driver.find_element(:id, edOrgId.to_s)
    assert_not_nil(element,"#{edOrgId} should be selectable")

    #assert(element!=nil, "#{edOrgId} should be selectable")
  end
end

Then /^the following edOrgs not enabled by the developer are non-selectable for application "(.*?)" in tenant "(.*?)"$/ do |application, tenant, table|
  table.hashes.map do |row|
    edorg_name = row["edorgs"]
    db = @conn[convertTenantIdToDbName(tenant)]
    coll = db.collection("educationOrganization")
    record = coll.find_one("body.nameOfInstitution" => edorg_name.to_s)
    if record
      STDOUT.puts "Checking #{edorg_name}"
      STDOUT.flush
      edOrgId = record["_id"]
      actualCount = @driver.find_elements(:id, edOrgId.to_s).count()
      assert("0" == actualCount.to_s, "#{edorg_name} should not be selectable")
      STDOUT.puts "#{edorg_name} is not selectable"
      STDOUT.flush
    end
  end
end
