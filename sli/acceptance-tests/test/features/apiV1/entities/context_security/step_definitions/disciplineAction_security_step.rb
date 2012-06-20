require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the disciplineAction "([^"]*)"/ do |arg1|
  id = "0e26de6c-225b-9f67-9201-8113ad50a03b" if arg1 == "DISC-ACT-1"
  id
end

When /^I make an API call to get (the disciplineAction "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/disciplineActions/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

