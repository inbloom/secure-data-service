=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if template == "MARVIN MILLER STUDENT ID"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15" if template == "MATT SOLLARS STUDENT ID"
  id = "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b" if template == "CARMEN ORTIZ STUDENT ID"
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^selector "(\([^\"]*\))"$/ do |selector|
  steps "Given parameter \"selector\" is \":#{selector}\""
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^in the response body I should see the following fields:$/ do |table|
  check_contains_keys(@result, table)
end

Then /^in the response body I should see the following fields only:$/ do |table|
  check_number_of_fields(@result, table)
  check_contains_keys(@result, table)
end

Then /^in the response body for all entities I should see the following fields only:$/ do |table|
  @result.each do |entity|
    check_number_of_fields(entity, table)
    check_contains_keys(entity, table)
  end
end

Then /^in "([^\"]*)" I should see the following fields:$/ do |key, table|
  assert(@result.has_key?(key), "Response does not contain #{key}")
  @result["#{key}"].each do |entity|
    check_contains_keys(entity, table)
  end
end

Then /^in "([^\"]*)" I should see the following fields only:$/ do |key, table|
  assert(@result.has_key?(key), "Response does not contain #{key}")
  @result["#{key}"].each do |entity|
    check_number_of_fields(entity, table)
    check_contains_keys(entity, table)
  end
end

Then /^I should be informed that the selector is invalid$/ do
  assert(@result.to_s.downcase.include?("invalid selector"), "'Invalid selector' message is not found in response")
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

private

def check_contains_keys(body, table)
  table.cells_rows.each do |field|
    assert(body.has_key?(field.value(0)), "Response does not contain the field #{field.value(0)}\nFields return: #{body.keys}")
  end
end

def check_number_of_fields(body, table)
  num_of_fields = table.cells_rows.size
  assert(body.keys.size == num_of_fields,
         "Number of fields returned doesn't match: received #{body.keys.size}, expected #{num_of_fields}\nFields return: #{body.keys}")
end