
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

When /^I navigate to "([^"]*)" with "([^"]*)"$/ do |arg1, arg2|
  @format = "application/vnd.slc+json"
  url = "/v1" << arg1.gsub!(/#\{id\}/, arg2)
  restHttpGet(url)
end

When /^I navigate to count "(.*?)" and get (\d+) in field "(.*?)"$/ do |arg1,arg2,arg3|
  @format = "application/vnd.slc+json"
  result = restHttpGet(arg1)
  result = JSON.parse result
  puts result["#{arg3}"]
  assert(result["#{arg3}"].to_i == arg2.to_i)
end

Then /^I should receive a valid return code$/ do
  assert [403, 200].include?(@res.code)
end

Then /^I can see all the attendances in a school$/ do
  attendances = JSON.parse @res.body

  # TODO: link to get the next set of students - doesn't work!
  #link = @res.raw_headers['link'][0]
  #p link
  #restHttpGet(link[(link.index('<')+1)..(link.index('>')-1)])
  #p @res
end
