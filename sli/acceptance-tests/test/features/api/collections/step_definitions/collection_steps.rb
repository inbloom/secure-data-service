require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'

# transform <Place Holder Id>
Transform /^<(.+)>$/ do |template|
  id = template
  id = "714c1304-8a04-4e23-b043-4ad80eb60992" if template == "'Alfonso' ID"
  id = "eb3b8c35-f582-df23-e406-6947249a19f2" if template == "'Apple Alternative Elementary School' ID"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end


# Function validate
# Inputs: (Array or Hash) data = Array or Hash of data (e.g. from JSON response)
# Inputs: (Hash) fields = Hash containing key=field name, value= field expected class, or Hash of expected values (if it's an Array or Hash)
# Output: Nothing, uses assertions to ensure data matches values
# Returns: Nothing, see Output
# Description: Validates if data from a GET has the specified fields and data types
def validate(data, fields)
  if data.is_a? Array
    data.each do |entity|
      validate(entity, fields)
    end
  elsif data.is_a? Hash
    fields.each do |key, value|
      if value.is_a? Hash
        assert(data[key] != nil, "object/array expected to exist: #{key}")
        validate(data[key], value)
      else
        if value == nil
          assert(data[key] == nil, "Field should not exist: #{key}")
        else
          assert(data[key].is_a?(value), "Field #{key} should be of type #{value}, found #{data[key].class}")
        end
      end
    end
  end
end

Then /^I should receive a collection of student objects$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  # puts @result
  validate(@result, {"id" => String, 
                             "studentUniqueStateId" => String, 
                            "name" => {"firstName" => String, 
                                       "lastSurname" => String}})
end

Then /^I should not receive a collection of student links\.$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  validate(@result, {"id" => String, "link" => nil})
end

Then /^I should receive a collection of student\-school\-association objects$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  validate(@result, {"id" => String, "studentId" => String, "schoolId" => String, "entryGradeLevel" => String})
end

Then /^I should not receive a collection of student\-school\-association links$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  validate(@result, {"id" => String, "link" => nil})
end

Then /^I should not receive a collection of student links$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  validate(@result, {"id" => String, "link" => nil})
end

Then /^I should receive a collection of school objects$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  validate(@result, {"id" => String, 
                             "stateOrganizationId" => String, 
                             "nameOfInstitution" => String, 
                             "address" => {"streetNumberName" => String,
                                           "city" => String,
                                           "stateAbbreviation" => String,
                                           "postalCode" => String}})
end

Then /^I should not receive a collection of school links$/ do
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  validate(@result, {"id" => String, "link" => nil})
end
