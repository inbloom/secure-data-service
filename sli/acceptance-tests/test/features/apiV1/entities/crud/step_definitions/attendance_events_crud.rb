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
  pare_entity(@entity).should == resource
end

def find_attendance(id)
  restHttpGet("#{attendance_endpoint}/#{id}")
  @res.code == 200 ? JSON.parse(@res) : nil
end

def delete_attendance(id)
  restHttpDelete("#{attendance_endpoint}/#{id}", 'application/vnd.slc+json') if id
end

def verify_attendance_entity_links
  links = remove_common_links( @entity['links'] ).dup
  [
    ['getSchool', @entity['schoolId']],
    ['getStudent', @entity['studentId']],
    ['getEducationOrganization', @entity['schoolId']]
  ].each do |rel, id|
    link = build_entity_link rel, id
    links.should include(link)
    links.delete link
  end

  if yearly?
    link = build_entity_link 'getSection', @entity['attendanceEvent'].first['sectionId']
    links.should include(link)
    links.delete link
  end

  links.map{|link| link['rel']}.should == []
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