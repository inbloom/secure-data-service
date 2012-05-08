require 'json'
require_relative '../../utils/sli_utils.rb'

When /^I make an API call to get the support email$/ do
  @format = "application/json"
  restHttpGet("/v1/system/support/email")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive JSON response that includes the address$/ do
  data = JSON.parse(@res.body)
  assert(data["email"] =~ /^(\w)+@(\w)+\.(\w)+$/, "Should have received an email address")
end