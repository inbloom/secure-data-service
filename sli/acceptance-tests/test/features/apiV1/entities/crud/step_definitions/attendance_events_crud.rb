When /^I POST an attendance with an attendance event$/ do
  @expected_attendance = {
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
                               "reason" => "some string",
                               "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                             },
                             {
                               "date" => "2011-09-06",
                               "event" => "In Attendance",
                               "educationalEnvironment" => "Classroom",
                               "reason" => "some string",
                             }
                           ]
                         }
                        ]
                     }
  puts @sessionId
  puts @expected_attendance.to_json.to_s
  restHttpPost('/v1/attendances', @expected_attendance.to_json, 'application/vnd.slc+json')
  assert(@res.code == 201, "Could not create attendance #{@expected_attendance.to_json}.")
  @location = @res.raw_headers['location'][0]
  puts @location
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "Could not fetch newly created attendance #{@expected_attendance.to_json}.")
end

When /^I POST a yearly attendance with an attendance event$/ do
 @expected_yearly_attendance = {
                       "schoolYear" => "2011-2012",
                       "attendanceEvent"=> [
                         {
                           "date" => "2011-09-06",
                           "event" => "In Attendance",
                           "educationalEnvironment" => "Classroom",
                           "reason" => "some string",
                           "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                         },
                         {
                           "date" => "2011-09-06",
                           "event" => "In Attendance",
                           "educationalEnvironment" => "Classroom",
                           "reason" => "some string",
                         }
                       ],      
                       "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
                       "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
                       }
  puts @sessionId
  puts @expected_yearly_attendance.to_json.to_s
  restHttpPost('/v1/yearlyAttendances',@expected_yearly_attendance.to_json, 'application/vnd.slc+json')
  assert(@res.code == 201, "Could not create yearlyAttendance #{@expected_yearly_attendance.to_json}.")
  @location = @res.raw_headers['location'][0]
  puts @location
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "Could not fetch newly created attendance #{@expected_yearly_attendance.to_json}.")
end

When /^I GET an attendance with an attendance event$/ do
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "Could not fetch newly created attendance #{@expected_attendance.to_json}.")
  actual = JSON.parse @res
  @id = actual['id'].to_s
  puts @id
  actual.delete('id')
  actual.delete('links')
  actual.delete('entityType')
  assert(actual.eql?(@expected_attendance),"GET contents different to that expected #{actual.to_json}.")
end

When /^I GET a yearly attendance with an attendance event$/ do
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "Could not fetch newly created yearly attendance #{@expected_yearly_attendance.to_json}.")
  actual = JSON.parse @res
  @id = actual['id'].to_s
  puts @id
  actual.delete('id')
  actual.delete('links')
  actual.delete('entityType')
  assert(actual.eql?(@expected_yearly_attendance),"GET contents different to that expected #{actual.to_json}.")
end

Then /^I PUT an attendance with an attendance event$/ do
  restHttpPut("/v1/attendances/#{@id}", @expected_attendance.to_json, 'application/vnd.slc+json')
  assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

Then /^I PUT a yearly attendance with an attendance event$/ do
  restHttpPut("/v1/yearlyAttendances/#{@id}",@expected_yearly_attendance.to_json, 'application/vnd.slc+json')
  assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

Then /^I DELETE an attendance with an attendance event$/ do
  restHttpDelete("/v1/attendances/#{@id}", 'application/vnd.slc+json')
  assert(@res.code == 204, "Unexpected HTTP code returned: #{@res.code}.")
end

Then /^I DELETE a yearly attendance with an attendance event$/ do
  restHttpDelete("/v1/yearlyAttendances/#{@id}", 'application/vnd.slc+json')
  assert(@res.code == 204, "Unexpected HTTP code returned: #{@res.code}.")
end

Then /^I PATCH an attendance's attendance events/ do
  patch_attendance_event={
                            "attendanceEvent"=> [
                             {
                               "date" => "2011-09-06",
                               "event" => "In Attendance",
                               "educationalEnvironment" => "Resource room",
                               "reason" => "some other string",
                               "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                             },
                             {
                                "date" => "2011-09-06",
                                "event" => "Early departure",
                                "educationalEnvironment" => "Resource room",
                                "reason" => "some string",
                                "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                             },
                             {
                                  "date" => "2011-09-07",
                                  "event" => "Early departure",
                                  "educationalEnvironment" => "Resource room",
                                  "reason" => "some string",
                             }
                           ]
                         }
  @expected_attendance = {
                       "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
                       "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe",
                       "schoolYearAttendance" => [
                         {
                           "schoolYear"=> "2011-2012",
                           "attendanceEvent"=> patch_attendance_event["attendanceEvent"]
                         }
                        ]
                     }
  restHttpPatch("/v1/attendances/#{@id}", patch_attendance_event.to_json, 'application/vnd.slc+json')
  assert(@res.code == 204, "#{@res.code} - Could not update attendance with PATCH: #{patch_attendance_event.to_json}.")
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "Could not fetch updated attendance #{@expected_attendance.to_json}.")
end

Then /^I PATCH a yearly attendance's attendance events/ do
  patch_attendance_event={
                            "attendanceEvent"=> [
                             {
                               "date" => "2011-09-06",
                               "event" => "In Attendance",
                               "educationalEnvironment" => "Resource room",
                               "reason" => "some other string",
                               "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                             },
                             {
                                "date" => "2011-09-06",
                                "event" => "Early departure",
                                "educationalEnvironment" => "Resource room",
                                "reason" => "some string",
                                "sectionId" => "17a8658c-6fcb-4ece-99d1-b2dea1afd987_id"
                             },
                             {
                                  "date" => "2011-09-07",
                                  "event" => "Early departure",
                                  "educationalEnvironment" => "Resource room",
                                  "reason" => "some string",
                             }
                           ]
                         }
 @expected_yearly_attendance = {
                               "schoolYear" => "2011-2012",
                               "attendanceEvent"=> patch_attendance_event["attendanceEvent"],
                               "studentId" => "fff656b2-5031-4897-b6b8-7b0f5769b482_id",
                               "schoolId" => "6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
                             }
 restHttpPatch("/v1/attendances/#{@id}", patch_attendance_event.to_json, 'application/vnd.slc+json')
 assert(@res.code == 204, "#{@res.code} - Could not update attendance with PATCH: #{patch_attendance_event.to_json}.")
 restHttpGetAbs(@location, 'application/vnd.slc+json')
 assert(@res.code == 200, "Could not fetch updated attendance #{@expected_attendance.to_json}.")
end

