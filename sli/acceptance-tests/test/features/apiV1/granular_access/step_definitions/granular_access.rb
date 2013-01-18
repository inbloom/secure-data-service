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

require_relative '../../entities/crud/step_definitions/crud_step.rb'
require_relative '../../querying/step_defs/no_table_scan_steps.rb'

###############################################################################
# BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
###############################################################################

Before do
  $type_to_uri = {
      "studentSchoolAssociation" => "studentSchoolAssociations",
      "teacherSectionAssociation" => "teacherSectionAssociations"
  }
  $uri_to_type = $type_to_uri.invert
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = @newId if human_readable_id == "NEW ID"
  # Entity list from scenario outlines
  id = "b408d88e-8fd5-11e1-86ec-0021701f543f_idb40c0ce1-8fd5-11e1-86ec-0021701f543f_id,"
    + "b408d88e-8fd5-11e1-86ec-0021701f543f_idb40de1a6-8fd5-11e1-86ec-0021701f543f_id,"
    + "b408635d-8fd5-11e1-86ec-0021701f543f_idb40c5b02-8fd5-11e1-86ec-0021701f543f_id,"
    + "b408635d-8fd5-11e1-86ec-0021701f543f_idb40d1e54-8fd5-11e1-86ec-0021701f543f_id,"
    + "b408635d-8fd5-11e1-86ec-0021701f543f_idb40e2fc7-8fd5-11e1-86ec-0021701f543f_id"
    if human_readable_id == "LIST-SCA-RROGERS"
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a new "([^\"]*)"$/ do |type|
  data = {
      "studentSchoolAssociation" => {
          "studentId" => "0f0d9bac-0081-4900-af7c-d17915e02378_id", # Daniela Cusimana
          "schoolId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731", # East Daybreak Junior High
          "entryGradeLevel" => "First grade",
          "entryDate" => "2010-09-01"
      },
      "teacherSectionAssociation" => {
          "teacherId" => "67ed9078-431a-465e-adf7-c720d08ef512", # Linda Kim
          "sectionId" => "392d1835-f372-4690-b221-7065db1aed33_id", # Phys-Ed 8A - Sec 6f08
          "classroomPosition" => "Teacher of Record"
      }
  }
  @fields = data[type]
end

Given /^the session date range is "([^\"]*)" to "([^\"]*)"$/ do |begin_date, end_date|
  # Nothing to do. This is for better readability.
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I set the date "([^\"]*)" to "([^\"]*)"$/ do |date_type, value|
  @fields = @result unless defined? @fields
  if value == "?"
    @fields.delete date_type
  else
    @fields[date_type] = value
  end
end

When /^I GET the sub-resources that are time-(sensitive|insensitive)$/ do |s|
  sensitive = (s == "sensitive")
  all_subresource_urls = resources
  insensitive_urls = File.read("#{File.expand_path(File.dirname(__FILE__))}/time_insensitive_subresources.txt").split("\n")
  insensitive_urls.each do |url|
    base_resource = get_base_resource url
    id = get_id_for_resource base_resource
    url.gsub!("{id}", id)
  end
  @subresource_request_urls = sensitive ? all_subresource_urls - insensitive_urls : insensitive_urls
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should only find "([^\"]*)" in the list of entities returned$/ do |list|
  count = list.split(",").size
  assert(@result.size == count, "Expected #{count} entities from list, received #{@result.size} from response")
  result_as_string = @result.join
  list.each do |item|
    assert(result_as_string.include? item, "Cannot find #{item} in the response")
  end
end

Then /^I delete the new "([^\"]*)" for the next test scenario$/ do |type|
  steps %Q{
    When I navigate to DELETE "/#{$type_to_uri[type]}/#{@newId}"
    Then I should receive a return code of 204
  }
end

Then /^the error message should say "([^\"]*)"$/ do |message|
  assert(@result.to_s.include? message, "Cannot find the message in response body")
end

Then /^the error message should say "([^\"]*)" for all requests$/ do |message|
  @subresource_request_urls.each do |url|
    begin
      @result = @all_results[url]
      steps "Then the error message should say \"#{message}\""
    rescue RuntimeError => e
      puts url
      raise e.message
    end
  end
end

Then /^I should receive a return code of (\d+) for all requests$/ do |code|
  @all_results = {}
  @subresource_request_urls.each do |url|
    begin
      steps %Q{
        When I navigate to GET "/v1#{url}"
        Then I should receive a return code of #{code}
      }
      @all_results[url] = @result
    rescue RuntimeError => e
      puts url
      raise e.message
    end
  end
end

Then /^I should receive correct counts (with|without) date range$/ do |w|
  with_range = (w == "with")
  all_counts = get_all_counts_from_file
  @subresource_request_urls.each do |url|
    begin
      count = with_range ? all_counts[url]["with_range"] : all_counts[url]["without_range"]
      @result = @all_results[url]
      steps "And I should receive a collection of \"#{count}\" entities"
    rescue RuntimeError => e
      puts url
      raise e.message
    end
  end
end

###############################################################################
# DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF DEF
###############################################################################

private

def get_all_counts_from_file
  all_counts = {}
  # line format: url,count_without_range,count_with_range
  File.read("#{File.expand_path(File.dirname(__FILE__))}/subresource_counts.txt").split("\n").each_line do |line|
    ary = line.split(",")
    base_resource = get_base_resource url
    id = get_id_for_resource base_resource
    url = ary[0].gsub("{id}", id)
    @all_counts[url] = {
      "without_range" => ary[1].to_i,
      "with_range" => ary[2].to_i
    }
  end
  all_counts
end
