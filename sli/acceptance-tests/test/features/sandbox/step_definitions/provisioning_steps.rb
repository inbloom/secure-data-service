require 'json'

require_relative '../../utils/sli_utils.rb'

Transform /^realm "([^"]*)"$/ do |arg1|
  id = "4cb03fa0-83ad-46e2-a936-09ab31af377e" if arg1 == "SLI"
  id = "4cfcbe8d-832d-40f2-a9ba-0a6f1daf3741" if arg1 == "Fake Realm"
  id = "45b03fa0-1bad-4606-a936-09ab71af37fe" if arg1 == "Another Fake Realm"
  id
end

When /^I try to access the URI "([^"]*)" with operation "([^"]*)"$/ do |arg1, arg2|
  @format = "application/json"

  data = "{}"

  restHttpPost(arg1, data) if arg2 == "POST"
  restHttpGet(arg1) if arg2 == "GET"
  restHttpPut(arg1, data) if arg2 == "PUT"
  restHttpDelete(arg1) if arg2 == "DELETE"
end

Then /^I should be denied access$/ do
  assert(@res.code == 403, "Return code was not expected: "+@res.code.to_s+" but expected 403")
end
