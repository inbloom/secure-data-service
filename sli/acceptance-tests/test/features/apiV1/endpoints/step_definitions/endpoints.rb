
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

When /^I navigate to "([^"]*)" with "([^"]*)"$/ do |arg1, arg2|
  @format = "application/vnd.slc+json"
  url = "/v1" << arg1.gsub!(/#\{id\}/, arg2)
  restHttpGet(url)
end
Then /^I should receive a valid return code$/ do
  assert [403, 200].include?(@res.code)
end
