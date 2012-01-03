require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |id_arg|
  id = "244520d2-8c6b-4a1e-b35e-d67819ec0211"  if id_arg == "Ms. Jones' ID"
  id = "8e5b2d0e-959c-42ef-b3df-9b83cba85a33"  if id_arg == "Mr. Smith's ID"
  id = "41baa245-ceea-4336-a9dd-0ba868526b9b"  if id_arg == "Algebra Alternative's ID"
  id = "0f464187-30ff-4e61-a0dd-74f45e5c7a9d"  if id_arg == "Biology High's ID"
  id = "b6ad1eb2-3cf7-41c4-96e7-2f393f0dd847"  if id_arg == "Chemistry Elementary's ID"
  id = "fcb4b719-8d9c-4ef1-a164-9eff33ef8f0c"  if id_arg == "Physics Middle's ID"
  id = "53616a21-df46-4990-aca7-2c8514a9fdb4"  if id_arg == "Mr. Smith's ID> and School <Biology High's ID"
  id = "41bd4c23-665d-4bae-88fb-1cfab8312d17"  if id_arg == "Ms. Jones' ID> and School <Algebra Alternative's ID"
  id = @previousUri                            if id_arg == "the previous association Id"
  id = @newId                                  if id_arg == "newly created teacher-school-association ID"
  id
end

Transform /^Teacher School Associations for (Teacher|School) (<[^"]*>)$/ do |type_arg,id_arg|
  uri = "/teacher-school-associations/" + (Transform id_arg)
  uri
end

Transform /^(\d+)(st|nd|rd|th)$/ do |grade_arg,suffix|
    grade = "First_grade"       if grade_arg == "1"
    grade = "Second_grade"      if grade_arg == "2"
    grade = "Third_grade"       if grade_arg == "3"
    grade = "Fourth_grade"      if grade_arg == "4"
    grade = "Fifth_grade"       if grade_arg == "5"
    grade = "Sixth_grade"       if grade_arg == "6"
    grade = "Seventh_grade"     if grade_arg == "7"
    grade = "Eighth_grade"      if grade_arg == "8"
    grade = "Ninth_grade"       if grade_arg == "9"
    grade = "Tenth_grade"       if grade_arg == "10"
    grade = "Eleventh_grade"    if grade_arg == "11"
    grade = "Twelfth_grade"     if grade_arg == "12"
    grade
end

Transform /^(program assignment type)|(subject taught)|(grade level)$/ do |property1, property2, property3|
    transformed = "programAssignment"           if property1 == "program assignment type"
    transformed = "academicSubjects"            if property2 == "subject taught"
    transformed = "instructionalGradeLevels"    if property3 == "grade level"
    transformed
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^the Teacher is (<[^"]*>)$/ do |teacher_arg|
    if @fields == nil
        @fields = Hash[]
    end
    
    @fields["teacherId"] = teacher_arg
end

Given /^the School is (<[^"]*>)$/ do |school_arg|
    if @fields == nil
        @fields = Hash[]
    end
    
    @fields["schoolId"] = school_arg
end

Given /^the Program Assignment Type is <([^"]*)>$/ do |programAssignment_arg|
    if @fields == nil
        @fields = Hash[]
    end
    
    @fields["programAssignment"] = programAssignment_arg
end

Given /^an Instructional Grade Level is <([^"]*)>$/ do |gradeLevel_arg|
    if @fields == nil
        @fields = Hash[]
    end
    
    gradeLevelArray = @fields["instructionalGradeLevels"]
    
    if(gradeLevelArray == nil)
        gradeLevelArray = Array.new
    end
    
    gradeLevelArray << gradeLevel_arg
    
    @fields["instructionalGradeLevels"] = gradeLevelArray
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I navigate to POST "([^"]*)"$/ do |post_uri|
    data = data_builder
    restHttpPost(post_uri, data)
    assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to (Teacher School Associations for (Teacher|School) <[^"]*>)$/ do |uri,arg2|
    @previousUri = uri
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
end

When /^I set the ([^"]*) to "([^"]*)"$/ do |property,value|
    if @newData == nil
        @newData = Hash[]
    end
    @newData[property]=value
end

When /^I navigate to PUT \/teacher\-school\-associations\/(<[^"]*>)$/ do |uri|
    if @format == "application/json"
        restHttpGet(@previousUri)
        assert(@res != nil, "Response from rest-client GET is nil")
        if(@res.code == 200)
            modified = JSON.parse(@res.body)
            @newData.each do |key, value|
                modified[key] = value
            end
          
            restHttpPut(@previousUri,modified.to_json)
            assert(@res != nil, "Response from rest-client PUT is nil")
        end
    elsif @format == "application/xml"
        assert(false, "application/xml is not supported")
    else
        assert(false, "Unsupported MIME type")
    end
end

When /^I navigate to DELETE (Teacher School Associations for (Teacher|School) <[^"]*>)$/ do |uri,arg2|
    @previousUri = uri
    restHttpDelete(uri)
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I navigate to GET \/teacher\-school\-associations\/<the previous association Id>$/ do
  restHttpGet(@previousUri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive an ID for the newly created (.*)$/ do |object_type_arg|
  #common definition for extracting newly created object's ID
  @newId = getIdOfNewlyCreatedObject(object_type_arg)
end

Then /^I should receive a collection of (\d+) teacher\-school\-associations(.*)$/ do |association_count,resolve_arg|
    @data = JSON.parse(@res.body)
    @resolve_links = (resolve_arg==" that resolve to")
    assert(@data != nil, "Response contains no data")
    assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
    assert(@data.length == Integer(association_count), "Expected response of size #{association_count}, received #{@res.length}");
end

Then /^I should receive a link named "([^"]*)" with URI (\/[^"]*\/)(<[^"]*>)$/ do |link_name, uri_prefix, uri_suffix|
  found = false
  uri = uri_prefix+uri_suffix
  if @data.is_a?(Hash)
    @data['links'].each do |link|
      if link["rel"] == link_name && link["href"] =~ /#{Regexp.escape(uri)}$/
        found = true
      end
    end
  else
    @data.each do |item|
      link = item['link']['href']
      response =  RestClient.get(link, {:accept => @format, :sessionId => @cookie}){|response, request, result| response }
      response = JSON.parse(response.body)
      response['links'].each do |link|
        if link["rel"] == link_name && link["href"] =~ /#{Regexp.escape(uri)}$/
          found = true
        end
      end
    end
  end
  assert(found,"Link #{link_name}->#{uri}not found")
end

Then /^I should receive a teacher-school-associations$/ do
    @data = JSON.parse(@res.body)
    assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
end

Then /^a (subject taught) should be "([^"]*)"$/ do |property_type, subject_arg|
    validateValueInResultArray(subject_arg,property_type,"subject")
end

Then /^a (grade level) should be "([^"]*)"$/ do |property_type, grade_arg|
    validateValueInResultArray(grade_arg,property_type,"grade")
end

Then /^the (Teacher) should be (<[^"]*>)$/ do |property_type, teacher_arg|
    validateValueInResults(teacher_arg,"teacherId","teacher")
end

Then /^the (School) should be (<[^"]*>)$/ do |property_type, school_arg|
    validateValueInResults(school_arg,"schoolId","school")
end


# Function data_builder
# Inputs: None
# Output: Data object in json or XML format depending on what the @format variable is set to
# Returns: Nothing, see Output
# Description: Helper function to create json or XML data structures to PUT or POST 
#                   to reduce replication of code
def data_builder
  if @format == "application/json"
  formatted_data = @fields.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent => 2)
    formatted_data = builder.section { |b| 
      b.teacherId(@fields["teacherId"])
      b.schoolId(@fields["schoolId"])  
      }
  else
    assert(false, "Unsupported MIME type: #{@format}")
  end
  formatted_data
end























Given /^I have access to all teachers and schools$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I should see (\d+) teachers$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should find the teacher "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should not find the teacher "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^"([^"]*)" works at "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see the teacher "([^"]*)" works at "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^I should see the teacher "([^"]*)" does not work at "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end





