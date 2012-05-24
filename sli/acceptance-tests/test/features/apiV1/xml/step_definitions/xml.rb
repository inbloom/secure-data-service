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
  id = "706ee3be-0dae-4e98-9525-f564e05aa388" if template == "LINDA KIM SECTION ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if template == "MARVIN MILLER STUDENT ID"
  id = @newId                                 if template == "NEWLY CREATED ENTITY ID"
  id = "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"  if template == "SCHOOL ENTITY TO BE UPDATED"
  id
end

Transform /^<(.+)><(.+)>$/ do |template1,template2|
  id = template1 + "/" + template2
  id
end

Transform /^<(.+)><(.+)><(.+)>$/ do |template1,template2,template3|
  id = template1 + "/" + template2 + "/" + template3
  id
end

Transform /^<(.+)><(.+)><(.+)><(.+)>$/ do |template1,template2,template3,template4|
  id = template1 + "/" + template2 + "/" + template3 + "/" + template4
  id
end

Transform /^<(.+)><(.+)><(.+)><(.+)><(.+)>$/ do |template1,template2,template3,template4,template5|
  id = template1 + "/" + template2 + "/" + template3 + "/" + template4 + "/" + template5
  id
end

Transform /^<(.+)><(.+)><(.+)><(.+)><(.+)><(.+)>$/ do |template1,template2,template3,template4,template5,template6|
  id = template1 + "/" + template2 + "/" + template3 + "/" + template4 + "/" + template5 + "/" + template6
  id
end

Transform /^<(.+)><(.+)><(.+)><(.+)><(.+)><(.+)><(.+)>$/ do |template1,template2,template3,template4,template5,template6,template7|
  id = template1 + "/" + template2 + "/" + template3 + "/" + template4 + "/" + template5 + "/" + template6 + "/" + template7
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^optional field "([^\"]*)"$/ do |field|
  if !defined? @queryParams
    @queryParams = [ "optionalFields=#{field}" ]
  else
    @fields = @queryParams[0].split("=")[1];
    @fields = @fields + ",#{field}"
    @queryParams[0] = "optionalFields=#{@fields}"
  end
end

Given /^a valid XML document for a new school entity$/ do
  @result = <<-eos
  <school>        
        <schoolCategories>
            <schoolCategories>Elementary School</schoolCategories>
        </schoolCategories>
        <gradesOffered>
            <gradesOffered>Third grade</gradesOffered>
            <gradesOffered>Fifth grade</gradesOffered>
            <gradesOffered>Fourth grade</gradesOffered>
            <gradesOffered>Sixth grade</gradesOffered>
        </gradesOffered>
        <organizationCategories>
            <organizationCategories>School</organizationCategories>
        </organizationCategories>
        <address>
            <address>
                <addressType>Physical</addressType>
                <streetNumberName>123 Main Street</streetNumberName>
                <city>Lebanon</city>
                <stateAbbreviation>KS</stateAbbreviation>
                <postalCode>66952</postalCode>
                <nameOfCounty>Smith County</nameOfCounty>
            </address>
        </address>        
        <stateOrganizationId>152901001</stateOrganizationId>
        <telephone>
            <telephone>
                <institutionTelephoneNumberType>Main</institutionTelephoneNumberType>
                <telephoneNumber>(785) 667-6006</telephoneNumber>
            </telephone>
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
<school>   
    <schoolCategories>
        <schoolCategories>Elementary School</schoolCategories>
    </schoolCategories>
    <gradesOffered>
        <gradesOffered>Third grade</gradesOffered>
        <gradesOffered>Fifth grade</gradesOffered>
        <gradesOffered>Fourth grade</gradesOffered>
        <gradesOffered>Sixth grade</gradesOffered>
    </gradesOffered>
    <organizationCategories>
        <organizationCategories>School</organizationCategories>
    </organizationCategories>
    <address>
        <address>
            <addressType>Physical</addressType>
            <streetNumberName>123 Main Street</streetNumberName>
            <city>Lebanon</city>
            <stateAbbreviation>KS</stateAbbreviation>
            <postalCode>66952</postalCode>
            <nameOfCounty>Smith County</nameOfCounty>
        </address>
    </address>    
    <stateOrganizationId>152901001</stateOrganizationId>
    <telephone>
        <telephone>
            <institutionTelephoneNumberType>Main</institutionTelephoneNumberType>
            <telephoneNumber>(785) 667-6006</telephoneNumber>
        </telephone>
    </telephone>
    <parentEducationAgencyReference>b2c6e292-37b0-4148-bf75-c98a2fcc905f</parentEducationAgencyReference>
    <nameOfInstitution>#{newName}</nameOfInstitution>
</school>
eos
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive an XML document$/ do
  assert(contentType(@res).match "application/xml")
  @node = @result.elements[1]
end

Then /^I should see "([^\"]*)" is "([^\"]*)"$/ do |key, value|
  assert(@result.elements["#{key}"] != nil, "Cannot find element #{key}")
  assert(@result.elements["#{key}"].text == value, "Value does not match")
end

Then /^I should see each entity's "([^\"]*)" is "([^\"]*)"$/ do |key, value|
  @result.elements.each do |element|
    ele = element.elements["#{key}"]
    assert(ele != nil, "Cannot find element #{key}")
    assert(ele.text == value, "Value does not match")
  end
end

Then /^I should receive ([\d]*) entities$/ do |count|
  assert(@result.elements.size == convert(count), "Expected #{count}, received #{@result.elements.size}")
end

Then /^I should find "([^"]*)" under "([^"]*)"$/ do |key, arg|
  path = arg.split("><").join("/")
  assert(@result.elements["#{path}/#{key}"] != nil, "Cannot find #{key} under #{path}")
  @node = @result.elements["#{path}/#{key}"]
end

Then /^I should find ([\d]*) "([^"]*)" under "([^"]*)"$/ do |count, key, arg|
  path = arg.split("><").join("/")
  assert(@result.elements["#{path}"].get_elements("#{key}") != nil, "Cannot find #{key} under #{path}")
  assert(@result.elements["#{path}"].get_elements("#{key}").size == convert(count), "Expected #{count}, received #{@result.elements["#{path}"].get_elements("#{key}").size}")
  @node = @result.elements["#{path}"].get_elements("#{key}")
end

Then /^I should see "([^"]*)" is "([^"]*)" for the one at position (\d+)$/ do |key, value, pos|
  assert(@node[convert(pos)-1].elements["#{key}"] != nil,  "Cannot find element #{key}")
  assert(@node[convert(pos)-1].elements["#{key}"].text == value, "Value does not match. expected #{value}, received #{@node[convert(pos)-1].elements["#{key}"].text}")
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
