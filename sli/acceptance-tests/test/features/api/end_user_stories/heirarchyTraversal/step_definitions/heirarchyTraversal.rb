require 'mongo'
require_relative '../../../../utils/sli_utils.rb'

Transform /^<([^>]*)>$/ do |human_readable_id|
  guid = "67ce204b-9999-4a11-aabe-000000000000" if human_readable_id == "'KANSAS STATE (EDORG)' ID"
  guid = "67ce204b-9999-4a11-aaca-000000000000" if human_readable_id == "'KANSAS STATE-SMALLVILLE DISTRICT (EDORG-EDORG)' ID"
  guid = "1d303c61-88d4-404a-ba13-d7c5cc324bc5" if human_readable_id == "'SMALLVILLE DISTRICT (EDORG)' ID"
  guid = "1cb629df-c482-4748-874a-425dd2007577" if human_readable_id == "'SMALLVILLE DISTRICT-DEREK SMALLS HIGH SCHOOL (EDORG-SCHOOL)' ID"
  guid = "67ce204b-9999-4a11-aaab-000000000008" if human_readable_id == "'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID"
  guid = "67ce204b-9999-4a11-aacd-000000000000" if human_readable_id == "'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID"
  guid = "67ce204b-9999-4a11-aacb-000000000000" if human_readable_id == "'SPRING 2011 (SESSION)' ID"
  guid = "67ce204b-9999-4a11-aace-000000000004" if human_readable_id == "'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID"
  guid = "67ce204b-9999-4a11-aacc-000000000004" if human_readable_id == "'MATH 1 (COURSE)' ID"
  guid = "67ce204b-9999-4a11-aacf-000000000008" if human_readable_id == "'MATH 1 SECTION 1 (COURSE-SECTION)' ID"
  guid = "67ce204b-9999-4a11-aaac-000000000008" if human_readable_id == "'MATH 1 (SECTION)' ID"
  guid = "67ce204b-9999-4a11-aabd-000000000008" if human_readable_id == "'TEACHER OF SECTION 1 (TEACHER-SECTION)' ID"
  guid = "67ce204b-9999-4a11-aabc-000000000038" if human_readable_id == "'OTIS OBAMA (TEACHER)' ID"
  guid = "67ce204b-9999-4a11-aaae-000000000120" if human_readable_id == "'SCOTTY PIERSON IN MATH 1 (STUDENT-SECTION)' ID"
  guid = "67ce204b-9999-4a11-aaaf-000000001184" if human_readable_id == "'SCOTTY PIERSON (STUDENT)' ID"
  guid
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  assert(@result[key].to_s == value.to_s, "Expected value \"#{value}\" != \"#{@result[key]}\"")
end

Then /^a "([^"]*)" "([^"]*)" should be "([^"]*)"$/ do |subdocumentName, key, value|
  assert(@result[subdocumentName] != nil, "No #{subdocumentName} present in response")
  found = false
  @result[subdocumentName].each do |subdocument|
    if subdocument[key] == value
       found = true
    end
  end
  assert(found, "#{value} not found in results")
end

Then /^the "name" should be "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |first_name, middle_name, last_name|
  assert(@result["name"] != nil, "Name is nil")
  expected_first_name = @result["name"]["firstName"]
  expected_middle_name = @result["name"]["middleName"]
  expected_last_name = @result["name"]["lastSurname"]
  assert(expected_first_name == first_name, "Unexpected first name. Input: #{first_name} Expected: #{expected_first_name}")
  assert(expected_middle_name == middle_name, "Unexpected first name. Input: #{middle_name} Expected: #{expected_middle_name}")
  assert(expected_last_name == last_name, "Unexpected first name. Input: #{last_name} Expected: #{expected_last_name}")
end

Then /^a "([^"]*)" should be "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  found = false;
  @result[key].each do |value_from_array| 
    found = (value==value_from_array) ? true:found;
  end
  assert(found, "Expected value #{value} not found in array")
end

Then /^I should receive a collection link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  found = false;
  @result.each do |link|
    if link["link"]["rel"]==rel
      if link["link"]["href"] =~ /#{Regexp.escape(href)}$/
        found = true
      end
    end
  end
  assert(found, "Response collection did not contain link rel \"#{rel}\" with value \"#{href}\"")
end

