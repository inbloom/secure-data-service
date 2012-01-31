require 'json'

require_relative '../../utils/sli_utils.rb'

Given /^I have not yet authenticated$/ do
  @sessionId = nil
end

When /^I make a call to get the list of realms$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I should see a response that contains the list of realms$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I should see a URL for each realm that links to their IDP$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I should not see any data about any realm's role\-mapping$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I try to access the URI "([^"]*)" with operation "([^"]*)"$/ do |arg1, arg2|
  @format = "application/json"
  data = ""

  restHttpPost(arg1, data) if arg2 == "POST"
  restHttpGet(arg1) if arg2 == "GET"
  restHttpPut(arg1, data) if arg2 == "PUT"
  restHttpDelete(arg1) if arg2 == "DELETE"
end

Then /^I should be denied access$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end

When /^I POST a mapping between default role "([^"]*)" and custom role "([^"]*)" for realm "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end

When /^I GET a list of role mappings for realm "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see a valid object returned$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I PUT to change the mapping between default role "([^"]*)" and custom role "([^"]*)" to role "([^"]*)" for realm "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  pending # express the regexp above with the code you wish you had
end

When /^I DELETE a mapping between the default role "([^"]*)" and custom role "([^"]*)" for realm "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end

When /^I duplicate the previous POST request to map the default role "([^"]*)" to custom role "([^"]*)" for realm "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end

When /^I duplicate the previous DELETE request to unmap the default role "([^"]*)" to custom role "([^"]*)" for realm "([^"]*)"$/ do |arg1, arg2, arg3|
  pending # express the regexp above with the code you wish you had
end