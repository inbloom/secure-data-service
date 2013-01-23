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
      "studentAssessment" => "studentAssessments",
      "studentSchoolAssociation" => "studentSchoolAssociations",
      "teacherSectionAssociation" => "teacherSectionAssociations"
  }
  $uri_to_type = $type_to_uri.invert
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = human_readable_id
  id = @newId                    if human_readable_id == "NEW ID"
  id = get_all_ids_from_response if human_readable_id == "ALL IDs"
  # Entity list from scenario outlines
  id = ["af37dbe2-301f-4409-b6e9-b05e11989694",
      "80b9ffeb-e7c4-463d-afd1-6a38079ac77d",
      "79b5a29a-0852-4d0b-8a85-0729849eed9f",
      "e4358c4a-2ecd-4a68-b6d3-7f1cb5904282"].join "," if human_readable_id == "LIST-SSA-AKOPEL"
  id = ["LCC1779GR1", "LCC1214GR1", "LCC2901GR1", "LCC8391GR1", "LCC2727GR1", "LCC6850GR1",
    "LCC4024GR1", "LCC7332GR1", "LCC8527GR1", "LCC6660GR1", "LCC1406GR1", "LCC5901GR1"].join "," if human_readable_id == "LIST-CO-LINDAKIM"
  id = ["Fall 2010 East Daybreak Junior High",
      "Spring 2011 East Daybreak Junior High"].join "," if human_readable_id == "LIST-SESSION-LINDAKIM"
  id = ["af37dbe2-301f-4409-b6e9-b05e11989694",
      "80b9ffeb-e7c4-463d-afd1-6a38079ac77d",
      "79b5a29a-0852-4d0b-8a85-0729849eed9f",
      "925d3f23-001f-4173-883b-04cf04ed9ad4",
      "964f0c0c-e316-476a-9ae6-96711fbf6ed5"].join "," if human_readable_id == "LIST-SSA-LINDAKIM"
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a new "([^\"]*)"$/ do |type|
  data = {
      "studentAssessment" => {
          "studentId"=> "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
          "assessmentId"=> "dd916592-7d7e-5d27-a87d-dfc7fcb757f6",
          "administrationDate"=> "2011-10-01",
          "administrationEndDate"=> "2012-01-01",
          "retestIndicator"=> "1st Retest"
      },
      "studentSchoolAssociation" => {
          "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id", # Rafaela Coleson
          "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb", # South Daybreak Elementary
          "entryGradeLevel" => "First grade",
          "entryDate" => "2010-09-01"
      },
      "teacherSectionAssociation" => {
          "teacherId" => "bcfcc33f-f4a6-488f-baee-b92fbd062e8d", # Rebecca Braverman
          "sectionId" => "224e86dd-1e00-45cf-a980-bd233cb9826b_id", # Writing 5A Sec 5f08
          "classroomPosition" => "Teacher of Record"
      }
  }
  @fields = data[type]
  @entity_posted = type
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
  count = list.empty? ? 0 : list.split(",").size
  assert(@result.size == count, "Expected #{count} entities from list, received #{@result.size} from response")
  unless list.empty?
    result_as_string = @result.join
    list.split(",").each do |item|
      assert(result_as_string.include?(item), "Cannot find #{item} in the response")
    end
  end
end

Then /^I delete the new "([^\"]*)" for the next test scenario$/ do |type|
  steps %Q{
    When I navigate to DELETE "/v1/#{$type_to_uri[type]}/#{@newId}"
    Then I should receive a return code of 204
  }
end

Then /^the error message should say "([^\"]*)"$/ do |message|
  assert(@result.to_s.include?(message), "Cannot find the message in response body")
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
# AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER AFTER
###############################################################################

After do
  steps "When I navigate to DELETE \"/v1/#{$type_to_uri[@entity_posted]}/#{@newId}\"" if @newId
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

def get_all_ids_from_response
  ret = []
  @results.each do |result|
    ret << result["id"]
  end
  ret.join ","
end