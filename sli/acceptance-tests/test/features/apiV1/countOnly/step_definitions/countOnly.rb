require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

When /^I navigate to a countOnly "([^"]*)"$/ do |url|
  @format = 'application/json'
  restHttpGet("#{url}?countOnly=true")
end

Then /^I should get back just a count$/ do
  # if any body is returned, that's an error
  JSON.parse(@res.body).should be_empty

  count_from_header = @res.headers[:totalcount]
  count_from_header.should_not be_nil
  count_from_header.to_i.should be > 0
end
