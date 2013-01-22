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
  id = human_readable_id
  id = @newId if human_readable_id == "NEW ID"
  # Entity list from scenario outlines
  id = ["b408d88e-8fd5-11e1-86ec-0021701f543f_idb40c0ce1-8fd5-11e1-86ec-0021701f543f_id",
    "b408d88e-8fd5-11e1-86ec-0021701f543f_idb40de1a6-8fd5-11e1-86ec-0021701f543f_id",
    "b408635d-8fd5-11e1-86ec-0021701f543f_idb40c5b02-8fd5-11e1-86ec-0021701f543f_id",
    "b408635d-8fd5-11e1-86ec-0021701f543f_idb40d1e54-8fd5-11e1-86ec-0021701f543f_id",
    "b408635d-8fd5-11e1-86ec-0021701f543f_idb40e2fc7-8fd5-11e1-86ec-0021701f543f_id"].join "," if human_readable_id == "LIST-SCA-RROGERS"
  id = ["LCC2722GR1", "LCC7800GR1", "LCC8512GR1", "LCC5279GR1", "LCC8670GR1", "LCC5005GR1",
    "LCC1779GR1", "LCC1214GR1", "LCC2901GR1", "LCC8391GR1", "LCC2727GR1", "LCC6850GR1",
    "LCC4024GR1", "LCC7332GR1", "LCC8527GR1", "LCC6660GR1", "LCC1406GR1", "LCC5901GR1",
    "LCC1737GR1"].join "," if human_readable_id == "LIST-CO-LINDAKIM"
  id = ["Fall 2011 East Daybreak Junior High",
    "Fall 2010 East Daybreak Junior High",
    "Spring 2011 East Daybreak Junior High",
    "Spring 2010 East Daybreak Junior High"].join "," if human_readable_id == "LIST-SESSION-LINDAKIM"
  id = ["18b98c27-8496-40f8-98f7-eb9aeeeeaf0a", "e34c700c-88f7-4b83-b7f0-69f154e802bd",
    "0f38f3ae-350b-481b-88ec-1444cfec6faa", "1be4dc69-3c66-4ffc-ad65-74e1a1f9c300",
    "d056ef93-d686-4ca4-8b83-9201579da663", "6178df17-e275-406a-8b5d-4a3c089b0b9f",
    "fccbdb5c-3146-4efc-a9d9-3fe6ab4bb6fb", "b5d95bef-9d5e-4afa-9b35-cbce37e5a8a7",
    "322da7f4-063b-4c98-b3e5-8b9d9a74d96b", "1c681963-0b51-4b45-86ee-fa7b0b4dcfb2",
    "2a73c0f5-adf2-4e5f-a8a9-3bac20cac02c", "5102d543-c6d4-495e-8195-a89891379efa",
    "5fa8109b-d022-43c0-99fd-e1902da4045c", "5572abca-61da-49d0-ad54-0ad636e05cbb",
    "ec2e4218-6483-4e9c-8954-0aecccfd4731", "e5091d2c-4b9a-47a4-88fd-cbb5277c16a7",
    "2c008500-0dbb-4923-860d-9c275c0e1433", "bf2912b7-c9ba-4fe9-8663-987a64af9631",
    "c4097288-2437-4b76-8ab1-f722663364a0", "5a8e6b60-71a7-4e67-a848-28b8f69068fc",
    "9344c1d5-2ec1-4ce5-a701-63d1a592b022", "0580d61c-090a-4246-9c65-7f00d91978b3",
    "0034787f-3d62-4973-b5d8-7923baade0ef", "bb8dd9e1-d3e2-49a8-a1f3-4b0336383040",
    "0ba8dbea-54c9-40df-8b23-bac0e1c19290", "efd8557a-ee42-465a-bb7f-21884d069a8b",
    "5e058b4a-2628-4e3b-bd56-254eb5142262", "2ee39d59-4dcf-4a5d-bfd7-726731c2241f",
    "5f4f7b8d-86e5-4b27-9a0b-bcaa2bff890f", "9249a8ef-8c83-4c50-8f94-fbc12700e7f2",
    "964f0c0c-e316-476a-9ae6-96711fbf6ed5", "925d3f23-001f-4173-883b-04cf04ed9ad4",
    "e4358c4a-2ecd-4a68-b6d3-7f1cb5904282", "79b5a29a-0852-4d0b-8a85-0729849eed9f",
    "80b9ffeb-e7c4-463d-afd1-6a38079ac77d", "af37dbe2-301f-4409-b6e9-b05e11989694"].join "," if human_readable_id == "LIST-SSA-LINDAKIM"
  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a new "([^\"]*)"$/ do |type|
  data = {
      "studentSchoolAssociation" => {
          "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id", # Rafaela Coleson
          "schoolId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb", # South Daybreak Elementary
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
    When I navigate to DELETE "/#{$type_to_uri[type]}/#{@newId}"
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
