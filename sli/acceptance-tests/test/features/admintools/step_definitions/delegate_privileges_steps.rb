When /^I hit the delegation url$/ do
  @driver.get(PropLoader.getProps['admintools_server_url'] + "/admin_delegations")
end

When /^I am redirected to the delegation page for my district$/ do
  assertWithWait("Expected to be on district delegation page") do
    @driver.page_source.index("Delegate District Privileges") != nil
  end
end

When /^"([^"]*)" is unchecked$/ do |feature|
  checkbox = getCheckbox(feature)
  assert(!checkbox.attribute("checked"), "Expected #{feature} checkbox to be unchecked")
end

When /^I check the "([^"]*)"$/ do |feature|
  checkbox = getCheckbox(feature)
  checkbox.click
end


Then /^"([^"]*)" is checked$/ do |feature|
  checkbox = getCheckbox(feature)
  assert(checkbox.attribute("checked"), "Expected #{feature} checkbox to be checked")
end

Then /^I get the message "([^"]*)"$/ do |errorMessage|
  assert(@driver.page_source.index(errorMessage) != nil, "Expected error message: '#{errorMessage}'")
end

Then /^I see a dropdown box listing both districts$/ do
  assert(@driver.find_element(:id, "districtSelection") != nil, "Could not find district dropdown")
end

Then /^I select "([^"]*)" in the district dropdown$/ do |district|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, district)
end

Then /^I see the table for "([^"]*)"$/ do |district|
  table = @driver.find_element(:id, "AuthorizedAppsTable_" + district)
  assert(table.displayed?)
end

Then /^I do not see the table for "([^"]*)"$/ do |district|
  table = @driver.find_element(:id, "AuthorizedAppsTable_" + district)
  assert(!table.displayed?)
end


def getCheckbox(feature)
    if feature == "Application Authorization"
    id = "admin_delegation_appApprovalEnabled"
  else
    assert(false, "Could not find the ID for #{feature} checkbox")
  end
  checkbox = @driver.find_element(:id, id)
  return checkbox
end

