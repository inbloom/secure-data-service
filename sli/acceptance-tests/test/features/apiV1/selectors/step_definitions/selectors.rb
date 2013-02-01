=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id" if template == "MARVIN MILLER STUDENT ID"
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15_id" if template == "MATT SOLLARS STUDENT ID"
  id = "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id" if template == "CARMEN ORTIZ STUDENT ID"
  id = "e1dd7a73-5000-4293-9b6d-b5f02b7b3b34_id" if template == "LUCRETIA NAGAI STUDENT ID"
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^selector "(\([^\"]*\))"$/ do |selector|
  steps "Given parameter \"selector\" is \":#{selector}\""
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^in the response body I should see the following fields only:$/ do |table|
  check_number_of_fields(@result, table)
  check_contains_fields(@result, table)
end

Then /^in the response body for all entities I should see the following fields only:$/ do |table|
  @result.each do |entity|
    check_number_of_fields(entity, table)
    check_contains_fields(entity, table)
  end
end

Then /^in "([^\"]*)" I should see the following fields only:$/ do |key, table|
  @entities_to_check = []
  get_hash_recursively(@result, key)
  @entities_to_check.flatten.each do |entity|
    check_number_of_fields(entity, table)
    check_contains_fields(entity, table)
  end
end

Then /^I should be informed that the selector is invalid$/ do
  assert(@result.to_s.downcase.include?("valid selector"), "'Invalid selector' message is not found in response")
end
Then /^in "([^\"]*)" I should not see "([^\"]*)"$/ do |key,id| 
  @entities_to_check = []
  get_hash_recursively(@result, key)
  @entities_to_check.flatten.each do |entity|
    assert(entity['id'] != id,"User should not have access to different context using selector")
  end
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

private

def remove_fields_from_body(body, fields)
  fields.each do |field|
    body.delete(field)
  end
end

def check_contains_fields(body, table)
  table.cells_rows.each do |field|
    assert(body.has_key?(field.value(0)), "Response does not contain the field #{field.value(0)}" +
        "\n>>>>Diff=#{get_fields_diffs(table.cells_rows, body.keys)}")
  end
end

def check_number_of_fields(body, table)
  num_of_fields = table.cells_rows.size
  assert(body.keys.size == num_of_fields,
         "Number of fields returned doesn't match: received #{body.keys.size}, expected #{num_of_fields}" +
             "\n>>>>Diff=#{get_fields_diffs(table.cells_rows, body.keys)}")
end

def get_hash_recursively(body, key)
  keys = key.split("=>")
  if body.is_a?(Hash)
    assert(body.has_key?(keys[0]), "Response does not contain #{keys[0]}")
    if keys.size > 1
      get_hash_recursively(body["#{keys[0]}"], keys.drop(1).join("=>"))
    else
      @entities_to_check << body["#{keys[0]}"]
    end
  elsif body.is_a?(Array)
    body.each do |b|
      assert(b.has_key?(keys[0]), "Response does not contain #{keys[0]}")
      if keys.size > 1
        get_hash_recursively(b["#{keys[0]}"], keys.drop(1).join("=>"))
      else
        @entities_to_check << b["#{keys[0]}"]
      end
    end
  end
end

def get_fields_diffs(table_cells, received)
  expected = []
  table_cells.each do |cell|
    expected << cell.value(0)
  end
  if expected.size > received.size
    diff = expected - received
  else
    diff = received - expected
  end
  diff
end
