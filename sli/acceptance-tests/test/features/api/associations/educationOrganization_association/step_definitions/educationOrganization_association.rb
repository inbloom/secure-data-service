require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<.+>$/ do |template|
  id = "0a922b8a-7a3b-4320-8b34-0f7749b8b062" if template == "<'GALACTICA' ID>"
  id = "9f5cb095-8e99-49a9-b130-bedfa20639d2" if template == "<'CAPRICA' ID>"
  id = "be3a26a2-a0e8-4098-bca3-c6cb2cdee970" if template == "<'PICON' ID>"
  id = "12b099d7-e8f4-4e5e-90a0-64ede16f3254" if template == "<'SAGITTARON' ID>"
  id = "d004116d-9b06-4066-804f-54fd142ff823" if template == "<'VIRGON' ID>"
  id = "676e9614-c9c6-4242-adc8-4494d125989d" if template == "<'GALACTICA-CAPRICA' ID>"
  id = "629fb175-bb41-4c5f-8e70-40617a71b541" if template == "<'GALACTICA-PICON' ID>"
  id = "4cb740aa-280f-4222-94ec-511b0c7dc1db" if template == "<'GALACTICA-SAGITTARON' ID>"
  id = "de12d34c-9595-443a-98cd-4810284e7c35" if template == "<'GALACTICA-VIRGON' ID>"
  id = @newId if template == "<newly created ID>"
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

When /^I navigate to POST "([^\"]+)"$/ do |url|
  data = prepareData(@format, @fields)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

Then /^I should receive a collection of (\d+) links$/ do |size|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(size), "Expected response of size #{size}, received #{@result.length}");

  @ids = Array.new
    @result.each do |link|
      if link["link"]["rel"]=="self"
        @ids.push(link["id"])
      end
    end
end

Then /^I should have a link with ID "([^"]*)"$/ do |linkid|
  found = false
  @ids.each do |crnt_id|
    if crnt_id == linkid
      found = true
    end
  end

  assert(found, "Link ID not found.")
end
