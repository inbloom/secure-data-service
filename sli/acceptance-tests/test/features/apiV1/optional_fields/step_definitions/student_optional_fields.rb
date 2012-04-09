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
  id = "bac890d6-b580-4d9d-a0d4-8bce4e8d351a" if template == "STUDENT SECTION ASSOC ID"
  id = "f917478f-a6f2-4f78-ad9d-bf5972b5567b" if template == "COURSE ID"
  id = "32930275-a9f3-4eaa-866f-7b35efc303ee" if template == "SCHOOL ID"
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^optional field "([^\"]*)"$/ do |field|
  if !defined? @queryParams
    @queryParams = [ "optionalFields=#{field}" ]
  else
    @fields = @queryParams[0].split("=")[1];
    @fields = @fields + ",#{field}"
    @queryParams[0] = "optionalFields=#{@fields}"
  end
end

#################################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
#################################################################################

When /^I look at the first one$/ do
  @col = @col[0]
end

When /^I go back up one level$/ do
  @col = @colStack.pop
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
  @col = @result[0][collection]
  assert(@col != nil, "Response contains no #{collection}")
  assert(@col.length == convert(count), "Expected #{count} #{collection}, received #{@col.length}")
end

Then /^I should find "([\d]*)" "([^\"]*)" in it$/ do |count, collection|
  if !defined? @col
    @col = @result[0][collection]
  else
    @col = @col[collection]
  end
  assert(@col != nil, "Response contains no #{collection}")
  assert(@col.length == convert(count), "Expected #{count} #{collection}, received #{@col.length}")
end

Then /^I should find "([^\"]*)" expanded in each of them$/ do |key|
  @col.each do |col|
    assert(col[key] != nil, "Response contains no #{key}")
  end
end

Then /^inside "([^\"]*)"$/ do |key|
  if !defined? @col
    @col = @result[0]
  end
  if !defined? @colStack
    @colStack = []
  end
  @colStack.push @col
  @col = @col[key]
end