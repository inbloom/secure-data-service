After('@section') do
  delete_class_period(class_period_entity_id)
  delete_section(entity_id)
end

Given /^I create a section$/ do
  create_class_period
  restHttpPost(section_endpoint, section_resource.to_json)
  @res.code.should == 201
  restHttpGetAbs(@res.headers[:location])
  @entity = JSON.parse(@res)
end

When /^I POST a new section$/ do
  create_class_period
  @new_entity = section_resource
  restHttpPost(section_endpoint, @new_entity.to_json)
end

When /^I GET that section$/ do
  restHttpGet("#{section_endpoint}/#{entity_id}")
  @entity = JSON.parse(@res)
end

When /^I DELETE that section$/ do
  restHttpDelete("#{section_endpoint}/#{entity_id}")
end

When /^I PUT that section$/ do
  @updated = section_resource.merge('populationServed' => 'High Achievers')
  restHttpPut("#{section_endpoint}/#{entity_id}", @updated.to_json)
end

When /^I PATCH that section$/ do
  @updated = {'populationServed' => 'Gifted'}
  restHttpPatch("#{section_endpoint}/#{entity_id}", @updated.to_json)
end

Then /^the section should be updated$/ do
  entity = pare_entity find_section(entity_id)
  entity.should == section_resource.merge(@updated)
end

Then /^the section should be deleted$/ do
  find_section(entity_id).should be_nil
end

Then /^the response resource should contain expected section data$/ do
  pare_entity(@entity).should == section_resource
end

Then /^the response location header should link to the new section$/ do
  location = @res.headers[:location]
  location.should match( resource_regexp(section_endpoint) )
end

Then /^the section should be saved$/ do
  _id = @res.headers[:location].split('/').last
  @entity = find_section(_id)
  entity_id.should == _id
end

# Custom data step defs

When /^I POST custom data to that section$/ do
  post_custom_data(section_endpoint, entity_id)
end

When /^I PUT the custom data for that section$/ do
  put_custom_data(section_endpoint, entity_id)
end

When /^I GET the custom data for that section$/ do
  restHttpGet("#{section_endpoint}/#{entity_id}/custom")
end

When /^I PATCH the section custom data$/ do
  restHttpPatch("#{section_endpoint}/#{entity_id}/custom", {}.to_json)
end

When /^I DELETE the custom data for that section$/ do
  restHttpDelete("#{section_endpoint}/#{entity_id}/custom")
end

# Attempt to find the class period by ID
#   - parse the response if 200,
#   - return nil if 404;
#   - otherwise, assert an expectation failure
def find_section(id)
  restHttpGet("#{section_endpoint}/#{id}")
  case @res.code
    when 200
      JSON.parse(@res)
    when 404
      nil
    else
      @res.code.to_s.should match(/^(200|404)$/)
  end
end

def delete_section(id)
  restHttpDelete("#{section_endpoint}/#{id}") if id
end

def class_period_entity_id
  @class_period_entity['id'] if @class_period_entity
end

def section_endpoint
  '/v1.5/sections'
end

def section_resource
  {
      "educationalEnvironment" => "Classroom",
      "sessionId" => "377c734f-7c15-455f-9209-ac15b3118236",
      "courseOfferingId" => "dab41da7-8c78-4908-8e61-1d4e9c34c84d",
      "populationServed" => "Regular Students",
      "sequenceOfCourse" => 3,
      "classPeriodId" => class_period_entity_id,
      "mediumOfInstruction" => "Independent study",
      "uniqueSectionCode" => "Science 7A - Sec dog",
      "schoolId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731"
  }
end

def class_period_resource
  {
      "classPeriodName" => "dog period",
      "educationOrganizationId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731"
  }
end

def create_class_period
  restHttpPost(class_period_endpoint, class_period_resource.to_json)
  @res.code.should == 201
  restHttpGetAbs(@res.headers[:location])
  @class_period_entity = JSON.parse(@res)
end

def verify_section_entity_links
  links = remove_common_links(@entity['links'].dup)

  verify_section_other_entity_links links
  verify_section_query_links links
  verify_section_sublinks links

  links.map{|l| l['rel']}.should == []
end

def verify_section_other_entity_links(links)
  # Entity style links (e.g. /classPeriods/[id])
  [
      ['getSchool', @entity['schoolId']],
      ['getEducationOrganization', @entity['schoolId']],
      ['getSession', @entity['sessionId']],
      ['getClassPeriod', @entity['classPeriodId']],
      ['getCourseOffering', @entity['courseOfferingId']]
  ].each do |rel, id|
    link = build_entity_link rel, id
    links.should include( link )
    links.delete link
  end
end

def verify_section_query_links(links)
  # Query style links (e.g. /grades?sectionId=[id])
  [
      ['getAttendances', "attendanceEvent.sectionId=#{@entity['id']}"],
      ['getYearlyAttendances', "attendanceEvent.sectionId=#{@entity['id']}"],
      ['getStudentGradebookEntries', "sectionId=#{@entity['id']}"],
      ['getGradebookEntries', "sectionId=#{@entity['id']}"],
      ['getGrades', "sectionId=#{@entity['id']}"],
  ].each do |rel, query|
    link = build_query_link rel, query
    links.should include( link )
    links.delete link
  end
end

def verify_section_sublinks(links)
  # Section sublinks (e.g. /sections/[id]/teacherSectionAssociations)
  [
      ['getStudentSectionAssociations', 'studentSectionAssociations'],
      ['getStudents', 'studentSectionAssociations/students'],
      ['getTeachers', 'teacherSectionAssociations/teachers'],
      ['getTeacherSectionAssociations', 'teacherSectionAssociations']
  ].each do |rel, path|
    link = build_section_link(rel, path)
    links.should include( link )
    links.delete link
  end
end

def build_section_link(rel, path)
  build_link(rel, "#{url_base}#{section_endpoint}/#{entity_id}/#{path}")
end