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


require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "706ee3be-0dae-4e98-9525-f564e05aa388_id" if template == "SECTION ID"
  id = "706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id" if template == "STUDENT SECTION ASSOC ID"
  id = "53777181-3519-4111-9210-529350429899" if template == "COURSE ID"
  id = "fef10fc3-9dee-4bd9-ac9b-88bf3e850841" if template == "COURSE OFFERING ID"
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if template == "SCHOOL ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id" if template == "STUDENT_ID"
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^optional field "([^\"]*)"$/ do |field|
  if !defined? @queryParams
    @queryParams = [ "views=#{field}" ]
  else
    @fields = @queryParams[0].split("=")[1];
    @fields = @fields + ",#{field}"
    @queryParams[0] = "views=#{@fields}"
  end
end

#################################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
#################################################################################

When /^I look at the second one$/ do
  @col = @col[1]
end

When /^I go back up one level$/ do
  @col = @colStack.pop
end


When /^I go into the item with the property "([^\"]*)" having the value "([^\"]*)"$/ do |key, value|
  bool = false
  @col.each do |col|
    if col[key] == value
      @col = col
      bool = true
      break
    end
  end
  assert(bool, "No item found with having a #{key} with the value #{value}")
end


#################################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
#################################################################################

Then /^I should receive a collection of "([^"]*)" entities$/ do |number_of_entities|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(number_of_entities), "Expected response of size #{number_of_entities}, received #{@result.length}: #{@result}");
  @col = @result
end

Then /^I should see "([^\"]*)" is "([^\"]*)" in one of them$/ do |key, value|
  found = false
  keys = key.split(".")
  @col.each do |doc|
    if keys.length == 1
      if doc[key].to_s == value.to_s
        found = true
        @col = doc
        break
      end
    elsif keys.length == 2
      if doc[keys[0]][keys[1]].to_s == value.to_s
        found = true
        @col = doc
        break
      end
    else
      raise("Key is too complex!")
    end
  end
  assert(found, "Cannot find a document with #{key}=#{value}")
end

Then /^I should see "([^\"]*)" is "([^\"]*)" in it$/ do |key, value|
  assert(@col[key].to_s == value.to_s, "Expected #{value}, received #{@col[key]}")
end

Then /^I should find "([\d]*)" "([^\"]*)"$/ do |count, collection|
  #if !defined? @col
  #  @col = @result
  #end
  if @result.is_a?(Hash)
    @col = @result[collection]
  elsif @result.is_a?(Array)
    @col = @result
    steps "Then I should find \"#{count}\" \"#{collection}\" in one of them"
  end
  assert(@col != nil, "Response contains no #{collection}")
  assert(@col.length == convert(count), "Expected #{count} #{collection}, received #{@col.length}")
end

Then /^I should find "([\d]*)" "([^\"]*)" in it$/ do |count, collection|
  @col = @col[collection]
  assert(@col != nil, "Response contains no #{collection}")
  assert(@col.length == convert(count), "Expected #{count} #{collection}, received #{@col.length}")
end

Then /^I should find "([\d]*)" "([^\"]*)" in one of them$/ do |count, collection|
  found = false
  @col.each do |doc|
    if doc[collection] != nil && doc[collection].is_a?(Array) && doc[collection].length == convert(count)
      found = true
      @col = doc[collection]
      break
    end
  end
  assert(found, "Cannot find #{collection} with a size of #{count}")
end

Then /^I should find "([^\"]*)" expanded in each of them$/ do |key|
  @col.each do |col|
    assert(col[key] != nil, "Response contains no #{key}")
  end
end

Then /^I should find "([^\"]*)" expanded in it$/ do |key|
  assert(@col[key] != nil, "Element contains no #{key}")
end

Then /^I should find one with the property "([^\"]*)" having the value "([^\"]*)"$/ do |key, value|
  bool = false
  @col.each do |col|
    if col[key] == value
      bool = true
    end
  end
  assert(bool, "No item found with having a #{key} with the value #{value}")
end


Then /^I should find "([^\"]*)" expanded in element "([\d]*)"$/ do |key, index|
  assert(@col[convert(index)][key] != nil, "Response contains no #{key}")
end


Then /^inside "([^\"]*)"$/ do |key|
  if !defined? @col
    @col = @result
  end
  if !defined? @colStack
    @colStack = []
  end
  @colStack.push @col
  @col = @col[key]
end

Then /^I should see the year "([\d]*)" in none of the attendance entries$/ do |year|
  bool = true
  @col.each do |col|
    if col["eventDate"].include? year.to_s
      bool = false
    end
  end
  assert(bool, "Found some attendance data in the year #{year}")
end

Then /^I should see the year "([\d]*)" in some of the attendance entries$/ do |year|
  bool = false
  @col.each do |col|
    if col["eventDate"].include? year.to_s
      bool = true
    end
  end
  assert(bool, "Cannot find attendance data in the year #{year}")
end
