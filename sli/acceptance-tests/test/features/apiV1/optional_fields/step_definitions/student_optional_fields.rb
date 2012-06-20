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
  id = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if template == "SCHOOL ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if template == "STUDENT_ID"
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

When /^I go back up one level$/ do
  @col = @colStack.pop
end

#################################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
#################################################################################

Then /^I should receive a collection of "([^"]*)" entities$/ do |number_of_entities|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(number_of_entities), "Expected response of size #{number_of_entities}, received #{@result.length}");
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