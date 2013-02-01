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
require 'rexml/document'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'
include REXML

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "706ee3be-0dae-4e98-9525-f564e05aa388_id" if template == "LINDA KIM SECTION ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id" if template == "MARVIN MILLER STUDENT ID"
  id = @newId                                 if template == "NEWLY CREATED ENTITY ID"
  id = "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"  if template == "SCHOOL ENTITY TO BE UPDATED"
  id
end

Transform /^<(.+)><(.+)>$/ do |template1,template2|
  id = template1 + "/" + template2
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

Given /^a valid XML document for a new school entity$/ do
  @result = <<-eos
    <school xmlns:sli="urn:sli">        
        <schoolCategories sli:member="true">Elementary School</schoolCategories>
        <gradesOffered sli:member="true">Third grade</gradesOffered>
        <gradesOffered sli:member="true">Fifth grade</gradesOffered>
        <gradesOffered sli:member="true">Fourth grade</gradesOffered>
        <gradesOffered sli:member="true">Sixth grade</gradesOffered>
        <organizationCategories sli:member="true">School</organizationCategories>
        <address sli:member="true">
            <addressType>Physical</addressType>
            <streetNumberName>123 Main Street</streetNumberName>
            <city>Lebanon</city>
            <stateAbbreviation>KS</stateAbbreviation>
            <postalCode>66952</postalCode>
            <nameOfCounty>Smith County</nameOfCounty>
        </address>
        <stateOrganizationId>825408847</stateOrganizationId>
        <telephone sli:member="true">
            <institutionTelephoneNumberType>Main</institutionTelephoneNumberType>
            <telephoneNumber>(785) 667-6006</telephoneNumber>
        </telephone>
        <nameOfInstitution>Apple Alternative Elementary School</nameOfInstitution>
    </school>
eos
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I change the name to "([^"]*)"$/ do |newName|
  @result = <<-eos
  <school xmlns:sli="urn:sli">        
        <schoolCategories sli:member="true">Elementary School</schoolCategories>
        <gradesOffered sli:member="true">Third grade</gradesOffered>
        <gradesOffered sli:member="true">Fifth grade</gradesOffered>
        <gradesOffered sli:member="true">Fourth grade</gradesOffered>
        <gradesOffered sli:member="true">Sixth grade</gradesOffered>
        <organizationCategories sli:member="true">School</organizationCategories>
        <address sli:member="true">
            <addressType>Physical</addressType>
            <streetNumberName>123 Main Street</streetNumberName>
            <city>Lebanon</city>
            <stateAbbreviation>KS</stateAbbreviation>
            <postalCode>66952</postalCode>
            <nameOfCounty>Smith County</nameOfCounty>
        </address>
        <stateOrganizationId>Sunset Central High School</stateOrganizationId>
        <telephone sli:member="true">
            <institutionTelephoneNumberType>Main</institutionTelephoneNumberType>
            <telephoneNumber>(785) 667-6006</telephoneNumber>
        </telephone>
    <nameOfInstitution>#{newName}</nameOfInstitution>
    <parentEducationAgencyReference>b2c6e292-37b0-4148-bf75-c98a2fcc905f</parentEducationAgencyReference>
</school>
eos
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive an XML document$/ do
  assert(contentType(@res).match "application/xml")
  #@node = @result.elements[1]
end

Then /^I should receive (\d+) entities$/ do |count|
  assert(@result.elements != nil, "Cannot find any element")
    assert(@result.elements.size == convert(count), "Expected #{count}, received #{@result.elements.size}")
end

Then /^when I look at the student "([^\"]*)" "([^\"]*)"$/ do |fname, lname|
  found = false
  @result.elements.each do |element|
    if element.elements["name/firstName"].text == fname && element.elements["name/lastSurname"].text == lname
      found = true
      @result = element
      break
    end
  end
  assert(found, "Cannot find #{fname} #{lname}")
end

Then /^I should see "([^\"]*)" is "([^\"]*)"$/ do |key, value|
  assert(@result.elements["#{key}"] != nil, "Cannot find element #{key}")
  assert(@result.elements["#{key}"].text == value, "Value does not match")
end

Then /^I should see each entity's "([^\"]*)" is "([^\"]*)"$/ do |key, value|
  @result.elements.each do |element|
    ele = element.elements["#{key}"]
    if ele.nil?
      ele = @result.elements["#{key}"]
    end
    assert(ele != nil, "Cannot find element #{key}")
    assert(ele.text == value, "Value does not match")
  end
end

Then /^I should receive ([\d]*) records$/ do |count|
  assert(@result.elements.size == convert(count), "Expected #{count}, received #{@result.elements.size}")
end

Then /^I should find "([^"]*)" under "([^"]*)"$/ do |key, arg|
  path = arg.split("><").join("/")
  assert(@result.elements["#{path}/#{key}"] != nil, "Cannot find #{key} under #{path}")
  @node = @result.elements["#{path}/#{key}"]
end

Then /^I should find "([^"]*)" under it$/ do |key|
  assert(@node.elements["#{key}"] != nil, "Cannot find the element #{key}")
  @node = @node.elements["#{key}"]
end

Then /^I should find (\d+) "([^"]*)" under it$/ do |count, key|
  assert(@node.get_elements("#{key}") != nil, "Cannot find the element #{key}")
  assert(@node.get_elements("#{key}").size == convert(count), "Expected #{count}, received #{@node.get_elements("#{key}").size}")
  @node = @node.get_elements("#{key}")
end

Then /^I should find ([\d]*) "([^"]*)"$/ do |count, key|
  assert(@result.get_elements("#{key}") != nil, "Cannot find #{key}")
  assert(@result.get_elements("#{key}").size == convert(count), "Expected #{count}, received #{@result.get_elements("#{key}").size}")
  @node = @result.get_elements("#{key}")
end

Then /^I should find ([\d]*) "([^"]*)" under "([^"]*)"$/ do |count, key, arg|
  path = arg.split("><").join("/")
  assert(@result.elements["#{path}"].get_elements("#{key}") != nil, "Cannot find #{key} under #{path}")
  assert(@result.elements["#{path}"].get_elements("#{key}").size == convert(count), "Expected #{count}, received #{@result.elements["#{path}"].get_elements("#{key}").size}")
  @node = @result.elements["#{path}"].get_elements("#{key}")
end

Then /^I should see "([^"]*)" is "([^"]*)" for one of them$/ do |key, value|
  found = false
  @node.each do |element|
    if element.elements["#{key}"].text == value
      found = true
      @node = element
      break
    end
  end
  assert(found, "Cannot find the element #{key} = #{value}")
end

Then /^I should see "([^"]*)" is "([^"]*)" for it$/ do |key, value|
  assert(@node.elements["#{key}"] != nil,  "Cannot find element #{key}")
  assert(@node.elements["#{key}"].text == value, "Value does not match. expected #{value}, received #{@node.elements["#{key}"].text}")
end

Then /^I should find (\d+) entries with "([^"]*)" including the string "([^"]*)"$/ do |count, key, value|
  cnt = 0
  @node.each do |element|
    if (element.elements["#{key}"].text.include? value)
      cnt = cnt + 1
    end
  end
  assert(cnt == convert(count), "Received #{cnt}, expected #{count}")
end
