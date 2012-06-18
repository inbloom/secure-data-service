require 'json'
require_relative '../../utils/sli_utils.rb'

When /^I make an API call to get the support email$/ do
  @format = "application/json"
  restHttpGet("/v1/system/support/email")
end

Then /^I receive JSON response that includes the address$/ do
  data = JSON.parse(@res.body)
  assert(data["email"] =~ /^(\w|-)+@(\w)+\.(\w)+$/, "Should have received an email address")
end

When /^I GET the url "([^"]*)" using a blank cookie$/ do |arg1|
  url = PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url, nil) {|response, request, result| response}
end

Then /^I should receive a "([^"]*)" response$/ do |arg1|
  assert("#{@res.code}" ==  arg1, "Expected #{arg1}, but got #{@res.code}")
end
