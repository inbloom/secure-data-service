=begin

Copyright 2013-2014 inBloom, Inc. and its affiliates.

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

require_relative '../../../utils/sli_utils.rb'


When /^I make API calls for multiple IDs in accordance to the following table:$/ do |table|
  # table is a Cucumber::Ast::Table

  # Strings for ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"

  success = true
  table.hashes.each do |row|
    puts "====Starting Row for #{row["Path"]}====" #Seperator between requests

    # Make the good API Request
    restHttpGet("/v1#{row["Path"].gsub("@ids",row["GoodId"])}")

    #Verify the return code
    if (@res.code != 200 || !validate_id_presence(@res))
      success = false
      puts "#{startRed}Expected 200 Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Expected 200 Return code for URI: #{row["Path"]} was #{@res.code}"
    end

    # Make the bad API Request
    restHttpGet("/v1#{row["Path"].gsub("@ids",row["BadId"])}")

    #Verify the return code
    if (@res.code != 403)
      success = false
      puts "#{startRed}Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}"
    end


    # Make the mixed API Request
    restHttpGet("/v1#{row["Path"].gsub("@ids","#{row["GoodId"]},#{row["BadId"]}")}")

    #Verify the return code
    if (@res.code != 403)
      success = false
      puts "#{startRed}Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}"
    end


    puts "====Ending Row for #{row["Path"]}====" #Seperator between requests
  end
  assert(success, "Blacklisting Expectations failed, see above logs for specific failure(s)")

end

When /^I request the Good ID, I should be allowed$/ do
  # Explainitory step, doesn't actually do what it says, it gets done as part of a previous step
end

When /^I request the Bad ID, I should be denied$/ do
  # Explainitory step, doesn't actually do what it says, it gets done as part of a previous step
end

When /^I request both IDs, I should be denied$/ do
  # Explainitory step, doesn't actually do what it says, it gets done as part of a previous step
end

Then /^I should be able to see <Fields> for the entity with ID <ID>:$/ do |table|
  # table is a Cucumber::Ast::Table
  # Strings for ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"

  success = true
  data = JSON.parse(@res.body)
  assert(data != nil, "Response from API was nil")
  assert(data.is_a?(Array), "Response for a listing endpoint was not an array")
  data.each do |entity|
    id = entity["id"]
    expectations = table.hashes.select{|row| row["ID"]==id}
    if (expectations != nil && !expectations.empty?)
      expectations = expectations[0]
    else
      success = false
      puts "#{startRed}ID #{id} returned from API but not expected!#{colorReset}"
      next
    end
    puts "=== Starting Field Validation for ID #{id} ==="
    success &= validate_field_existance_nonexistance(entity, expectations["Fields"])
    puts "=== Ending Field Validation for ID #{id} ==="
  end
  actual_set = Set.new(data.map{|entity|entity["id"]})
  expected_set = Set.new(table.hashes.collect{|row| row["ID"]})
  assert(actual_set == expected_set,"Set Expectation failed: Outlier IDs: #{(expected_set.subtract(expected_set&actual_set)).select{|x| x}}")
  assert(success, "Tests failed")
end

def validate_id_presence(res)
  id = nil
  return false if res == nil
  data = JSON.parse(res.body)
  return false if data == nil
  if data.is_a? Array
    id = data[0]["id"]
  end
  if data.is_a? Hash
    id = data["id"]
  end
  return id != nil
end

def validate_field_existance_nonexistance(entity_hash, field_string_enum)
  # ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"


  fields_should_exist = []
  fields_should_not = []
  # First populate, fields that should exist and not exist from the field string enum param
  case field_string_enum
    when "NameOnly"
      fields_should_exist = ["name", "id"]
      fields_should_not = ["studentUniqueStateId","studentIdentificationCode","otherName","sex","birthData","address",
                           "telephone","electronicMail","profileThumbnail","hispanicLatinoEthnicity","oldEthnicity","race",
                           "economicDisadvantaged","schoolFoodServicesEligibility","studentCharacteristics","limitedEnglishProficiency",
                           "languages","homeLanguages","disabilities","section504Disabilities","displacementStatus","programParticipations",
                           "learningStyles","cohortYears","studentIndicators","loginId","gradeLevel","schoolId"]
    when "Everything"
      fields_should_exist = ["name", "id", "birthData", "studentUniqueStateId", "economicDisadvantaged","schoolFoodServicesEligibility"]
      fields_should_not = []
    when "AllStudent"
      fields_should_exist = ["name", "id", "birthData", "studentUniqueStateId"]
      fields_should_not = ["economicDisadvantaged","schoolFoodServicesEligibility"]
    when "SecionIds"
      fields_should_exist = ["sectionId", "studentId"]
      fields_should_not = ["beginDate","endDate","homeroomIndicator","repeatIdentifier"]
    when "AllSectAsoc"
      fields_should_exist = ["sectionId", "studentId", "beginDate"]
      fields_should_not = []
    when "ProgramIds"
      fields_should_exist = ["programId", "studentId"]
      fields_should_not = ["beginDate","endDate", "services", "educationOrganizationId", "reasonExited"]
    when "AllProgAssoc"
      fields_should_exist = ["programId", "studentId", "beginDate", "educationOrganizationId"]
      fields_should_not = []
    when "CohortIds"
      fields_should_exist = ["cohortId", "studentId"]
      fields_should_not = ["beginDate","endDate"]
    when "AllCohrtAssoc"
      fields_should_exist = ["cohortId", "studentId", "beginDate"]
      fields_should_not = []
  end

  # Now check for existance or non-existance of fields
  success = true
  fields_should_exist.each do |field|
    if entity_hash[field] == nil
      success = false
      puts "#{startRed}Expected field #{field}, but was absent#{colorReset}"
    end
  end
  puts "Expected field validation passed" if success
  fields_should_not.each do |field|
    if entity_hash[field] != nil
      success = false
      puts "#{startRed}Did not expect field #{field}, but was present#{colorReset}"
    end
  end
  puts "Non-expected field validation passed" if success

  return success
end
