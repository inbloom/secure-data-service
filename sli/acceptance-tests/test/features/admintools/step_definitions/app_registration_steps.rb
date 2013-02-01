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
require 'net/imap'
require 'mongo'

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'
require 'date'

Given /^I am a valid SLI Developer "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

Given /^I am a valid SLC Operator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

When /^I hit the Application Registration Tool URL$/ do
  @driver.get(PropLoader.getProps['admintools_server_url']+"/apps/")
end

Then /^I can navigate to app registration page with that user$/ do
  step "I hit the Application Registration Tool URL"
  step "I submit the credentials \"#{@user_info[:email]}\" \"test1234\" for the \"Simple\" login page"
end

Then /^I am redirected to the Application Approval Tool page$/ do
  assertWithWait("Failed to navigate to the Admintools App Registration Approval page")  {@driver.page_source.index("Authorize Applications") != nil}
end

Then /^I am redirected to the Application Registration Tool page$/ do
  assertWithWait("Failed to navigate to the Admintools App Registration page")  {@driver.page_source.index("New Application") != nil}
end

Then /^I see all of the applications that are registered to SLI$/ do
  assertWithWait("Failed to find applications table") {@driver.find_element(:id, "applications")}
end

Then /^application "([^"]*)" does not have an edit link$/ do |app|
# TODO: canidate for lowering timeout temporarly to improve performance
  appsTable = @driver.find_element(:id, "applications")
  edit = appsTable.find_elements(:xpath, ".//tr/td[text()='#{app}']/../td/a[text()='Edit']")
  assert(edit.length == 0, "Should not see an edit link")
end

Then /^I see all the applications registered on SLI$/ do
  appsTable = @driver.find_element(:id, "applications")
  trs = appsTable.find_elements(:xpath, ".//tr/td[text()='APPROVED']")
  assert(trs.length > 10, "Should see a significant number of approved applications")
end

Then /^I see all the applications pending registration$/ do
  appsTable = @driver.find_element(:id, "applications")
  trs = appsTable.find_elements(:xpath, ".//tr/td[text()='PENDING']")
  assert(trs.length == 1, "Should see a pending application")
end

# for slcoperator
Then /^application "([^"]*)" is pending approval$/ do |app|
  appsTable = @driver.find_element(:id, "applications")
  trs  = appsTable.find_elements(:xpath, ".//tr/td[text()='#{app}']/../td[text()='PENDING']")
  assert(trs.length > 0, "#{app} is pending")
end


Then /^the pending apps are on top$/ do
  appsTable = @driver.find_element(:id, "applications")
  tableHeadings = appsTable.find_elements(:xpath, ".//tr/th")
  index = 0
  tableHeadings.each do |arg|
    index = tableHeadings.index(arg) + 1 if arg.text == "Status"    
  end
  trs = appsTable.find_elements(:xpath, ".//tr/td/form/div/input[@value='Approve']/../../../..")
  assert(trs.length > 10, "Should see many applications")

  last_status = nil
  trs.each do |row|
    td = row.find_element(:xpath, ".//td[#{index}]")
    if last_status == nil
      last_status = td.text
      assert(last_status == 'PENDING', "First element should be PENDING, got #{last_status}")
    end
    if last_status == 'APPROVED'
      assert(td.text != 'PENDING', "Once we find approved apps, we should no longer see any PENDING ones.")
    end
    last_status = td.text
  end
end

# For slcoperator
When /^I click on 'Approve' next to application "([^"]*)"$/ do |app|
  appsTable = @driver.find_element(:id, "applications")
  y_button  = appsTable.find_element(:xpath, ".//tr/td[text()='#{app}']/../td/form/div/input[@value='Approve']")
  assert(y_button != nil, "Did not find the approve button")
  y_button.click
end

# For developer
When /^I click on 'In Progress' next to application "([^"]*)"$/ do |app|
  appsTable = @driver.find_element(:id, "applications")
  y_button  = appsTable.find_elements(:xpath, ".//tr/td[text()='#{app}']/../td/form/div/input[@value='In Progress']")
  assert(y_button != nil, "Did not find the 'In Progress' button")
  y_button.click
end

# For slcoperator
When /^I click on 'Deny' next to application "([^"]*)"$/ do |app|
  appsTable = @driver.find_element(:id, "applications")
  y_button  = appsTable.find_element(:xpath, ".//tr/td[text()='#{app}']/../td/form/div/input[@value='Deny']")
  assert(y_button != nil, "Did not find the deny button")
  y_button.click
end

Then /^I get a dialog asking if I want to continue$/ do
  @driver.switch_to.alert
end

# For slcoperator
Then /^application "([^"]*)" is registered$/ do |app|
  appsTable = @driver.find_element(:id, "applications")
  assertWithWait("Could not find app #{app} in approved state") {
    appsTable.find_elements(:xpath, ".//tr/td[text()='#{app}']/../td[text()='APPROVED']").length > 0
  }
end

Then /^application "([^"]*)" is not registered$/ do |app|
	# no-op - in next step we verify it was removed from list
end

Then /^application "([^"]*)" is removed from the list$/ do |app|
# TODO: canidate for lowering timeout temporarly to improve performance
  assertWithWait("Shouldn't see a NewApp") {
	@driver.find_element(:id, "applications").find_elements(:xpath, ".//tr/td[text()='#{app}']").length == 0
  }
  #appsTable = @driver.find_element(:id, "applications")
  #tds  = appsTable.find_elements(:xpath, ".//tr/td[text()='#{app}']")
  #assert(tds.length == 0, "#{app} isn't in list")
end

Then /^the 'Approve' button is disabled for application "([^"]*)"$/ do |app|
  appsTable = @driver.find_element(:id, "applications")
  y_button  = appsTable.find_elements(:xpath, ".//tr/td[text()='#{app}']/../td/form/div/input[@value='Approve']")[0]
  assert(y_button.attribute("disabled") == 'true', "Y button is disabled")
end

Then /^those apps are sorted by the Last Update column$/ do
  appsTable = @driver.find_element(:id, "applications")
  tableHeadings = appsTable.find_elements(:xpath, ".//tr/th")
  index = 0
  tableHeadings.each do |arg|
    index = tableHeadings.index(arg) + 1 if arg.text == "Last Update"    
  end
  tableRows = appsTable.find_elements(:xpath, ".//tr/td/a[text()='Edit']/../..")
  lastDate = nil
  tableRows.each do |row|
    td = row.find_element(:xpath, ".//td[#{index}]")
    date = Date.parse(td.text)
    if lastDate == nil
      lastDate = date
    end
    assert(date <= lastDate, "Last Update column should be sorted")
    lastDate = date
  end
end

Given /^I am a valid IT Administrator "([^"]*)" from the "([^"]*)" hosted directory$/ do |arg1, arg2|
  # No code needed, done as configuration
end

Then /^I receive a message that I am not authorized$/ do
  assertWithWait("Failed to find forbidden message")  {@driver.page_source.index("Forbidden") != nil}
end

Then /^I have clicked to the button New$/ do
  @driver.find_element(:xpath, '//a[text()="New Application"]').click
end

When /^I entered the name "([^"]*)" into the field titled "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:id, "app_#{arg2.downcase}").send_keys arg1
end

When /^I select the app display method to "(.*?)"$/ do |arg1|
  displayMethod = @driver.find_element(:id, "app_behavior")
  all_options = displayMethod.find_elements(:tag_name, "option")
  optionFound = false
  all_options.each do |option|
    if option.attribute("text") == arg1
      optionFound = true
      option.click
      break
    end
  end  
  assert(optionFound, "Desired option '" + arg1 + "' was not found")
end

Then /^I am redirected to a new application page$/ do
  assertWithWait("Failed to navigate to the New Applicaation page")  {@driver.page_source.index("New Application") != nil}
end

When /^I have entered data into the other required fields except for the shared secret and the app id which are read\-only$/ do
  @driver.find_element(:name, 'app[description]').send_keys "Blah"
  @driver.find_element(:name, 'app[application_url]').send_keys "https://blah.com"
  @driver.find_element(:name, 'app[administration_url]').send_keys "https://blah.com"
  @driver.find_element(:name, 'app[redirect_uri]').send_keys "https://blah.com"
  @driver.find_element(:name, 'app[version]').send_keys "0.9"
  @driver.find_element(:name, 'app[image_url]').send_keys "http://blah.com"
  @driver.find_element(:css, 'input[id="app_installed"]').click
  list = @driver.find_element(:css, 'input[disabled="disabled"]')
  assert(list, "Should have disabled fields.")
  
end

When /^I click on the button Submit$/ do
  @driver.find_element(:name, 'commit').click
end

Then /^the application "([^"]*)" is listed in the table on the top$/ do |app|
  value = @driver.find_element(:id, 'notice').text
  assert(value =~ /successfully created/, "Should have valid flash message")
  assertWithWait("Couldn't locate #{app} at the top of the page") {@driver.find_element(:xpath, "//tbody/tr[1]/td[text()='#{app}']")}
end

Then /^a client ID is created for the new application that can be used to access SLI$/ do
  assertWithWait("Should have located a client id") {@driver.find_element(:xpath, '//tr[3]').find_element(:name, 'app[client_id]')}
end

Then /^the client ID and shared secret fields are Pending$/ do
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_id = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[1]').text
  puts "Client ID: " + client_id
  assert(client_id == 'Pending', "Expected 'Pending', got #{client_id}")
end

Then /^the client ID and shared secret fields are present$/ do
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_id = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[1]').text
  shared_secret = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[2]').text  
  puts "client_id: " + client_id
  puts "Shared Secret ID: " + shared_secret  
  assert(client_id != '', "Expected non empty client Id, got #{client_id}")
  assert(shared_secret != '', "Expected non empty shared secret Id, got #{shared_secret}")
end

Then /^the Registration Status field is Pending$/ do
  td = @driver.find_element(:xpath, "//tbody/tr[1]/td[4]")
  assert(td.text == 'Pending', "Expected 'Pending', got #{td.text}")
end

When /^I click on the row of application named "([^"]*)" in the table$/ do |arg1|
  @driver.find_element(:xpath, "//tr/td[text()='#{arg1}']").click
end

Then /^the row expands$/ do
  #No code needed, this should be tested by later stepdefs when they see that the application details are there
end

Then /^I see the details of "([^"]*)"$/ do |arg1|
  step "the client ID and shared secret fields are Pending"
end

Then /^all the fields are read only$/ do
  #Nothing needed
end

Then /^I clicked on the button Edit for the application "([^"]*)"$/ do |arg1|
  row = @driver.find_element(:xpath, "//tr/td[text()='#{arg1}']/..")
  assert(row)
  @id = row.attribute('id')
  @driver.find_element(:xpath, "//tr/td[text()='#{arg1}']/../td/a[contains(@class, 'btn')]").click
end

Then /^the row of the app "([^"]*)" expanded$/ do |arg1|
 invisible = @driver.find_elements(:css, "tr[display='none']").count
 visible = @driver.find_elements(:css, "tr.odd").count
 assert(invisible == visible - 1)
end

Then /^every field except the shared secret and the app ID became editable$/ do
  @form = @driver.find_element(:id, "edit_app_#{@id}")
  editable = @form.find_elements(:css, "input").count
  not_editable = @form.find_elements(:css, "input[disabled='disabled']").count
  assert(not_editable == 2, "Found #{not_editable} elements")
end

Then /^I have edited the field named "([^"]*)" to say "([^"]*)"$/ do |arg1, arg2|
  @form = @driver.find_element(:css, "form")
  field = @form.find_element(:name, "app[#{arg1.gsub(' ', '_').downcase}]")
  field.clear
  field.send_keys arg2
end

When /^I clicked Save$/ do
  @form.find_element(:name, 'commit').click
end

Then /^the info for "([^"]*)" was updated$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I the field named "([^"]*)" still says "([^"]*)"$/ do |arg1, arg2|
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  data = @driver.find_element(:xpath, "//tbody/tr[2]/td/dl/dt[text()=\"#{arg1}\"]")
  value = data.find_element(:xpath, "following-sibling::*[1]").text
  assertWithWait("#{arg1} should be #{arg2}") {value == arg2}
end

Then /^I have clicked on the button 'Delete' for the application named "([^"]*)"$/ do |arg1|
  list = @driver.find_element(:xpath, "//tr/td[text()='#{arg1}']")
  assert(list)
  @id = list.attribute('id')
  @driver.find_element(:xpath, "//tr/td[text()='#{arg1}']/../td/a[text()='Delete']").click
    
end

Then /^I got warning message saying 'You are trying to remove this application from inBloom\. By doing so, you will prevent any active user to access it\. Do you want to continue\?'$/ do
  @driver.switch_to.alert
end

When /^I click 'Yes'$/ do
  @driver.switch_to.alert.accept
end

Then /^the application named "([^"]*)" is removed from the SLI$/ do |arg1|
  assertWithWait("Shouldn't see the app #{arg1}") {@driver.find_elements(:xpath, "//tr/td[text()='#{arg1}']").size == 0}
end

Then /^the previously generated client ID can no longer be used to access SLI$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am a valid App Developer$/ do
  #Nothing
end

Then /^I see the list of my registered applications only$/ do
  appsTable = @driver.find_element(:id, "applications")
  trs = appsTable.find_elements(:xpath, ".//tbody/tr")
  assert(trs.length > 0, "Should see at least one of my apps")
end

Then /^I see the list of registered applications as well$/ do
    step "I see the list of my registered applications only"
end

Then /^the application is registered$/ do
  appsTable = @driver.find_element(:id, "applications")
  trs  = appsTable.find_elements(:xpath, ".//tbody/tr/td[text()='NewApp']")
  trs.each do |tr|
    assert(tr.find_element(:xpath, "../td[4]").text != "Pending", "App should be registered")
  end
  assert(trs.length > 0, "No more pending applications")
end

Then /^I can see the client ID and shared secret$/ do
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_id = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[1]').text
  assert(client_id != 'Pending', "Expected !'Pending', got #{client_id}")
end

Then /^the Registration Status field is Registered$/ do
  #Nothing to show anymore
end

Then /^a notification email is sent to "([^"]*)"$/ do |email|
    sleep 2
    defaultUser = email.split("@")[0]
    defaultPassword = "#{defaultUser}1234"
    imap = Net::IMAP.new(PropLoader.getProps['email_imap_host'], PropLoader.getProps['email_imap_port'], true, nil, false)
    imap.authenticate('LOGIN', defaultUser, defaultPassword)
    imap.examine('INBOX')
    #ids = imap.search(["FROM", "noreply@slidev.org","TO", email])
    ids = imap.search(["TO", email])
    content = imap.fetch(ids[-1], "BODY[TEXT]")[0].attr["BODY[TEXT]"]
    subject = imap.fetch(ids[-1], "BODY[HEADER.FIELDS (SUBJECT)]")[0].attr["BODY[HEADER.FIELDS (SUBJECT)]"]
    found = true if content != nil
    @email_content = content
    @email_subject = subject
    puts subject,content
    imap.disconnect
    assert(found, "Email was not found on SMTP server")
end

When /^I click on the In Progress button$/ do
  @mongo_ids = []
  db = Mongo::Connection.new[convertTenantIdToDbName('developer-email@slidev.org')]['educationOrganization']

  ed_org = build_edorg("Some State", "developer-email@slidev.org")
  ed_org[:body][:organizationCategories] = ["State Education Agency"]
  @mongo_ids << db.insert(ed_org)
  ed_org = build_edorg("Some District", "developer-email@slidev.org", @mongo_ids.first, "WaffleDistrict", true)
  @mongo_ids << db.insert(ed_org)
  ed_org = build_edorg("Some School", "developer-email@slidev.org", @mongo_ids[1], "WaffleSchool", false)
  @mongo_ids << db.insert(ed_org, opts = {:safe => true})
  step 'I clicked on the button Edit for the application "NewApp"'
  db.remove()
end

Then /^I can see the ed\-orgs I want to approve for my application$/ do
  assert(@driver.find_element(:css, 'div.edorgs input[type="checkbox"]') != nil, "We should see the edorgs available for this app")
end

And /^I can update the version to "100"$/ do 
  @driver.find_element(:name, 'app[version]').send_keys "100"
end 

And /^I can delete "(.*?)"$/ do |app_name|
    step "I have clicked on the button 'Delete' for the application named \"#{app_name}\""
    step "I got warning message saying 'You are trying to remove this application from inBloom. By doing so, you will prevent any active user to access it. Do you want to continue?'"
    step "I click 'Yes'"
    step "the application named \"#{app_name}\" is removed from the SLI"
end

private
def build_edorg(name, tenant, parent = nil, stateId = "Waffles", isLea=true)
  @@mongoid ||= 0
  ed_org = {}
  ed_org[:_id] = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb#{@@mongoid}"
  ed_org[:body] = {}
  ed_org[:metaData] = {}
  ed_org[:metaData][:tenantId] = tenant
  ed_org[:body][:address] = [{:stateAbbreviation => 'WA'}]
  ed_org[:body][:nameOfInstitution] = name
  ed_org[:body][:parentEducationAgencyReference] = parent
  ed_org[:body][:stateOrganizationId] = stateId
  ed_org[:body][:organizationCategories] = isLea ? ["Local Education Agency"] : ["School"]
  @@mongoid += 1
  ed_org
end
