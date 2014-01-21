After('@attendance') do |scenario|
  delete_attendance(entity_id)
end

def yearly?
  !!@yearly
end

def attendance_endpoint
  "/v1.5/#{yearly? ? 'yearlyAttendances' : 'attendances'}"
end

def resource
  yearly? ? yearly_attendance_resource : attendance_resource
end

def entity_id
  @entity['id'] if @entity
end

Given /^I create an? (yearly )?attendance event$/ do |yearly|
  @yearly = yearly
  restHttpPost(attendance_endpoint, resource.to_json, 'application/vnd.slc+json')
  @res.code.should == 201
  restHttpGetAbs(@res.headers[:location], 'application/vnd.slc+json')
  @entity = JSON.parse(@res)
end

When /^I POST an? (yearly )?attendance event$/ do |yearly|
  @yearly = yearly
  @new_entity = resource
  restHttpPost(attendance_endpoint, @new_entity.to_json, 'application/vnd.slc+json')
end

When /^I GET that attendance event$/ do
  restHttpGet("#{attendance_endpoint}/#{entity_id}")
  @entity = JSON.parse(@res)
end

When /^I GET a non-existent (yearly )?attendance event$/ do |yearly|
  @yearly = yearly
  restHttpGet("#{attendance_endpoint}/unknown")
end

When /^I PUT that attendance event$/ do
  restHttpPut("#{attendance_endpoint}/#{entity_id}", {}.to_json, 'application/vnd.slc+json')
end

When /^I DELETE that attendance event$/ do
  restHttpDelete("#{attendance_endpoint}/#{entity_id}", 'application/vnd.slc+json')
end

When /^I PATCH that attendance event$/ do
  @updated = {
    'attendanceEvent'=> [
      {'date'=>'2011-09-08', 'event'=>'In Attendance'},
      {'date'=>'2011-09-18', 'event'=>'Tardy'}
    ]
  }
  restHttpPatch("#{attendance_endpoint}/#{entity_id}", @updated.to_json, 'application/vnd.slc+json')
end

Then /^the attendance event should be updated$/ do
  entity = find_attendance(entity_id)
  attendance_event = yearly? ? entity['attendanceEvent'] : entity['schoolYearAttendance'].first['attendanceEvent']
  attendance_event.should == @updated['attendanceEvent']
end

Then /^the response status should be ([0-9]{3})(?:.*)$/ do |status|
  @res.code.should == status.to_i
end

Then /^the response location header should link to the new attendance event$/ do
  location = @res.headers[:location]
  location.should match( resource_regexp(attendance_endpoint) )
end

Then /^the entity type should be "(.*)"$/ do |type|
  @entity['entityType'].should == type
end

Then /^the attendance event should be saved$/ do
  _id = @res.headers[:location].split('/').last
  @entity = find_attendance(_id)
  entity_id.should == _id
end

Then /^the attendance event should be deleted$/ do
  find_attendance(entity_id).should be_nil
end

Then /^the response resource should contain expected attendance data$/ do
  entity = @entity.reject{|key,_| %w(id entityType links).include?(key)}
  entity.should == resource
end

Then /^the response resource should have an id$/ do
  entity_id.should_not be_nil
end

Then /^the response resource entity type should be "(.*?)"$/ do |entity_type|
  @entity['entityType'].should == entity_type
end

Then /^the response resource should have HATEOAS links for attendance$/ do
  verify_common_links
  verify_entity_links
end

def find_attendance(id)
  restHttpGet("#{attendance_endpoint}/#{id}")
  @res.code == 200 ? JSON.parse(@res) : nil
end

def delete_attendance(id)
  restHttpDelete("#{attendance_endpoint}/#{id}", 'application/vnd.slc+json') if id
end

def verify_common_links
  links = @entity['links']
  links.should_not be_empty
  links.should include( build_link('self', make_self_url(entity_id)) )
  links.should include( build_link('custom', make_custom_url(entity_id)) )
end

def verify_entity_links
  links = @entity['links']

  links.should include( build_entity_link 'getSchool', @entity['schoolId'])
  links.should include( build_entity_link 'getStudent', @entity['studentId'])
  links.should include( build_entity_link 'getEducationOrganization', @entity['schoolId'])

  links.should include( build_entity_link 'getSection', @entity['attendanceEvent'].first['sectionId']) if yearly?
end

def build_link(rel, href)
  {'rel' => rel, 'href' => href}
end

def build_entity_link(rel, entity_id)
  build_link rel, make_entity_url(entity_id ,rel)
end

def make_self_url(entity_id)
  "#{url_base}#{attendance_endpoint}/#{entity_id}"
end

def make_custom_url(entity_id)
  "#{url_base}#{attendance_endpoint}/#{entity_id}/custom"
end

def make_entity_url(entity_id, rel)
  "#{url_base}#{endpoint_for_rel rel}/#{entity_id}"
end

def url_base
  "#{Property['api_server_url']}/api/rest"
end

def endpoint_for_rel(rel)
  map = {
      'getStudent' => 'students',
      'getSchool' => 'schools',
      'getEducationOrganization' => 'educationOrganizations',
      'getSection' => 'sections'
  }
  "/v1.5/#{map[rel]}"
end

# Match against the resource name (typically plural) followed by the entity ID
# (e.g. attendances/ab5b8dd1d4d5c8b613ff60b86a6a2f0fd610934c_id)
def resource_regexp(url)
  %r{#{url}/[a-z0-9]+_id$}
end

def attendance_resource
  {
      "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
      "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
      "schoolYearAttendance" => [
          {
              "schoolYear"=> "2011-2012",
              "attendanceEvent"=> [
                  {
                      "date" => "2011-09-06",
                      "event" => "In Attendance",
                      "educationalEnvironment" => "Classroom",
                      "reason" => "some event",
                      "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                  },
                  {
                      "date" => "2011-09-06",
                      "event" => "In Attendance",
                      "educationalEnvironment" => "Classroom",
                      "reason" => "some other event",
                  }
              ]
          }
      ]
  }
end

def yearly_attendance_resource
  {
    "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
    "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
    "schoolYear" => "2011-2012",
    "attendanceEvent"=> [
      {
        "date" => "2011-09-06",
        "event" => "In Attendance",
        "educationalEnvironment" => "Classroom",
        "reason" => "some yearly event",
        "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
      },
      {
        "date" => "2011-09-06",
        "event" => "In Attendance",
        "educationalEnvironment" => "Classroom",
        "reason" => "some other yearly event",
      }
    ]
  }
end