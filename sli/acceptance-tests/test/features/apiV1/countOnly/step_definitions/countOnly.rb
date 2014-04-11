require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

When /^I navigate to a countOnly "([^"]*)" with "([^"]*)"$/ do |path, id|
  @format = 'application/json'

  separator = path.include?('?') ? '&' : '?'

  # if path includes a "#{id}", then substitute it, otherwise just use path as the url
  url = path.include?("/#\{id\}") ? path.gsub!(/#\{id\}/, id) : path

  # add the global parameter for a countOnly query, and execute it.
  url = "#{url}#{separator}countOnly=true"
  restHttpGet(url)
end

Then /^I should get back just a count of ([^"]*)$/ do |count|
  # if any body is returned, that's an error
  JSON.parse(@res.body).should be_empty

  count_from_header = @res.headers[:totalcount]
  count_from_header.should_not be_nil
  count_from_header.to_i.should == count.to_i
end
