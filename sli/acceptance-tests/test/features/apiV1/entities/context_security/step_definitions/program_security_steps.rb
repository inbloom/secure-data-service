require 'json'
require_relative '../../../../utils/sli_utils.rb'

Transform /the program "([^"]*)"/ do |arg1|
  id = "9b8cafdc-8fd5-11e1-86ec-0021701f543f" if arg1 == "ACC-TEST-PROG-1"
  id = "9b8c3aab-8fd5-11e1-86ec-0021701f543f" if arg1 == "ACC-TEST-PROG-2"
  id
end

When /^I make an API call to get (the program "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/programs/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end
