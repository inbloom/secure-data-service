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
  case template
    when 'SECTION ID'; '706ee3be-0dae-4e98-9525-f564e05aa388_id'
    when 'STUDENT SECTION ASSOC ID'; '706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id'
    when 'COURSE ID'; '53777181-3519-4111-9210-529350429899'
    when 'COURSE OFFERING ID'; 'fef10fc3-9dee-4bd9-ac9b-88bf3e850841'
    when 'SCHOOL ID'; 'a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb'
    when 'STUDENT_ID'; '74cf790e-84c4-4322-84b8-fca7206f1085_id'
  end
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^optional field "([^\"]*)"$/ do |field|
  unless @queryParams
    @queryParams = [ "views=#{field}" ]
  else
    fields = @queryParams.first.split('=').last
    fields << ",#{field}"
    @queryParams[0] = "views=#{fields}"
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
  @col = @col.detect{|col| col[key] == value}
  @col.should_not be_nil, "No item found with having a #{key} with the value #{value}"
end

#################################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
#################################################################################

Then /^I should receive a collection of "([^"]*)" entities$/ do |number_of_entities|
  @result.should_not be_nil, 'Response contains no data'
  @result.should be_a_kind_of(Array)
  @result.length.should be(number_of_entities.to_i)
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
  @col[key].to_s.should == value
end

Then /^I should find "([\d]*)" "([^\"]*)"$/ do |count, collection|
  if @result.is_a?(Hash)
    @col = @result[collection]
  elsif @result.is_a?(Array)
    @col = @result
    steps "Then I should find \"#{count}\" \"#{collection}\" in one of them"
  end
  @col.should_not be_nil, "Response contains no #{collection}"
  @col.length.should == count.to_i
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
  found.should be_true, "Cannot find #{collection} with a size of #{count}"
end

Then /^I should find "([^\"]*)" expanded in each of them$/ do |key|
  @col.each { |col| col[key].should_not be_nil, "Response contains no #{key}" }
end

Then /^I should find "([^\"]*)" expanded in it$/ do |key|
  @col[key].should_not be_nil, "Element contains no #{key}"
end

Then /^I should find one with the property "([^\"]*)" having the value "([^\"]*)"$/ do |key, value|
  @col.any? {|col| col[key] == value}.should be_true, "No item found with having a #{key} with the value #{value}"
end

Then /^I should find "([^\"]*)" expanded in element "([\d]*)"$/ do |key, index|
  @col[convert(index)][key].should_not be_nil, "Response contains no #{key}"
end

Then /^inside "([^\"]*)"$/ do |key|
  @col ||= @result
  @colStack ||= []
  @colStack << @col
  @col = @col[key]
end

Then /^I should see the year "([\d]*)" in (some|none) of the attendance entries$/ do |year, some_or_none|
  some = (some_or_none == 'some')
  @col.any? {|col| col['eventDate'].include? year}.should be(some)
end