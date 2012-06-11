require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the cohort "([^"]*)"/ do |arg1|
  id = "b40926af-8fd5-11e1-86ec-0021701f543f" if arg1 == "ACC-TEST-COH-2"
  id
end

When /^I make an API call to get (the cohort "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/cohorts/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end

