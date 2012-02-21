require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<(.+)>$/ do |template|
  id = template
  id = "714c1304-8a04-4e23-b043-4ad80eb60992" if template == "'Alfonso' ID"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2" if template == "'Apple Alternative Elementary School' ID"
  id = "d431ba09-c8ac-4139-beac-be28220633e6" if template == "'Krypton Middle School' ID"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end

Given /^parameter "([^\"]*)" is "([^\"]*)"$/ do |param, value|
  if !defined? @queryParams
    @queryParams = []
  end
  @queryParams << "#{param}=#{value}"
end

Then /^I should receive a collection of student association links$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Expected array of links")
end

Then /^the link at index (\d+) should point to an entity with id "([^\"]*)"$/ do |index, id|
  index = convert(index)
  @result[index].should_not == nil
  @result[index]["id"].should == id
end
