require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "706ee3be-0dae-4e98-9525-f564e05aa388" if template == "SECTION ID"
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^optional field "([^\"]*)"$/ do |field|
  if !defined? @optFields
    @optFields = "";
  end
  if !defined? @queryParams
    @queryParams = [ "optionalParams=#{field}" ]
  else
    @fields = @queryParams[0].split("=")[1];
    @fields = @fields + ",#{field}"
    @queryParams[0] = "optionalFields=#{@fields}"
  end
end

#################################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
#################################################################################

Then /^I should find "([^\"]*)" in "([^\"]*)"$/ do |key, collection|
  assert(@result[0][collection] != nil, "Response contains no #{collection}")
  assert(@result[0][collection][key] != nil, "Response contains no #{key}")
  @col = @result[0][collection][key]
end

Then /^I should see "([^\"]*)" is "([^\"]*)" in it$/ do |key, value|
  assert(@col[key] == convert(value), "Expected #{value}, received #{@col[key]}")
end

Then /^I should find "([\d]*)" "([^\"]*)"$/ do |count, collection|
  assert(@result[0][collection] != nil, "Response contains no #{collection}")
  assert(@result[0][collection].length == convert(count), "Expected #{count} #{collection}, received #{@result[0][collection].length}")
  @arr = @result[0][collection]
end

Then /^I should find "([^\"]*)" in each of them$/ do |key|
  @arr.each do |col|
    assert(col[key] != nil, "Response contains no #{key}")
  end
end

Then /^I look at the first one$/ do
  @col = @arr[0]
end

Then /^inside "([^\"]*)"$/ do |key|
  @col = @col[key]
end