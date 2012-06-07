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
  id = "53777181-3519-4111-9210-529350429899" if template == "COURSE ID"
  id = "fef10fc3-9dee-4bd9-ac9b-88bf3e850841" if template == "COURSE OFFERING ID"
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

When /^I look at the first one$/ do
  @col = @col[0]
end

When /^I look at the second one$/ do
  @col = @col[1]
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
  assert(@col[key].to_s == value.to_s, "Expected #{value}, received #{@col[key]}")
end

Then /^I should find "([\d]*)" "([^\"]*)"$/ do |count, collection|
  if @result.is_a?(Array)
    @col = @result[0][collection]
  elsif @result.is_a?(Hash)
    @col = @result[collection]
  end
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
    puts("  COL    ")
    puts(col)
    puts("\n\n\n")
    assert(col[key] != nil, "Response contains no #{key}")
  end
end

Then /^I should find "([^\"]*)" expanded in element "([\d]*)"$/ do |key, index|
  assert(@col[convert(index)][key] != nil, "Response contains no #{key}")
end


Then /^inside "([^\"]*)"$/ do |key|
  if !defined? @col
    if @result.is_a?(Array)
      @col = @result[0]
    elsif @result.is_a?(Hash)
      @col = @result
    end
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