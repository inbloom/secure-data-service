require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'

Then /^I should receive an XML document$/ do
  assert(contentType(@res).match "application/xml")
end

Then /^I should see "([^\"]*)" is ([^\"]*)$/ do |key, value|
  ele = @result.elements["#{value}/#{key}"]
  assert(ele != nil, "Cannot find element #{key}")
  assert(ele.text == value, "Value does not match")
end

Then /^I should see each entity's "([^\"]*)" is ([^\"]*)$/ do |key, value|
  @result.elements.each do |element|
    ele = element.elements["#{key}"]
    assert(ele != nil, "Cannot find element #{key}")
    assert(ele.text == value, "Value does not match")
  end
end

Then /^I should receive ([\d]*) entities$/ do |count|
  assert(@result.elements.size == convert(count), "Expected #{count}, received #{@result.elements.size}")
end