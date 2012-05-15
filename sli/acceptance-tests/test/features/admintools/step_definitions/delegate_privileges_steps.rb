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


def getCheckbox(feature)
    if feature == "Application Authorization"
    id = "admin_delegation_appApprovalEnabled"
  else
    assert(false, "Could not find the ID for #{feature} checkbox")
  end
  checkbox = @driver.find_element(:id, id)
  return checkbox
end

