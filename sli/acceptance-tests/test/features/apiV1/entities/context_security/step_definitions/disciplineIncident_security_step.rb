require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the disciplineIncident "([^"]*)"/ do |arg1|
  id = "0e26de79-222a-5e67-9201-5113ad50a03b" if arg1 == "DISC-INC-2"
  id
end

When /^I make an API call to get (the disciplineIncident "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/disciplineIncidents/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end
