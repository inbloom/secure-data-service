When /^I POST a bell schedule$/ do
  @expected_entity = {
                       "bellScheduleName" => "Grade School Schedule",
                       "educationOrganizationId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
                       "meetingTime"  =>  {
                                            "classPeriodId" => "97094f3eb0e089264bbc1d937a1d22b5c7f668af_id",
                                             "alternateDayName" => "Beige",
                                             "startTime" => "09:00:00.000",
                                             "endTime" => "09:55:00.000",
                                             "officialAttendancePeriod" => true
                                          },
                       "gradeLevels" => [
                                         "First grade",
                                         "Second grade",
                                         "Third grade",
                                         "Fourth grade"
                                       ],
                       "calendarDateReference" => "2012ai-7963b924-ceb0-11e1-8af5-0a0027000000"
                     }
  post_entity("bellSchedules")
  @expected_links = { "links" => [
                                   {
                                     "rel" => "self",
                                     "href" =>  "bellSchedules/#{@id}"
                                   },
                                   {
                                     "rel" => "custom",
                                     "href" =>  "bellSchedules/#{@id}/custom"
                                   },
                                   {
                                     "rel" => "getSchool",
                                     "href" => "schools/" + @expected_entity['educationOrganizationId']
                                   },
                                   {
                                     "rel" => "getEducationOrganization",
                                     "href" => "educationOrganizations/" + @expected_entity['educationOrganizationId']
                                   },
                                   {
                                     "rel" => "getCalendar",
                                     "href" =>  "calendarDates/" + @expected_entity['calendarDateReference']
                                   },
                                   {
                                     "rel" => "getClassPeriod",
                                     "href" =>  "classPeriods/" + @expected_entity['meetingTime']['classPeriodId']
                                   }
                                 ]
                     }
  puts "expected links: " + @expected_links["links"].to_json
  @expected_type = 'bellSchedule'
  puts "expected type: " + @expected_type
end

When /^I GET the bell schedule$/ do
  get_entity
end

When /^I try the not supported PUT for the bell schedule$/ do
  unsupported_put("bellSchedules")
end

When /^I try the not supported PATCH for the bell schedule$/ do
  unsupported_patch("bellSchedules")
end

Then /^I DELETE the bell schedule$/ do
  deleteEntity("bellSchedules")
end

When /^I POST a custom bell schedule$/ do
  post_custom_entity("bellSchedules")
end

When /^I GET the custom bell schedule$/ do
  get_custom_entity("bellSchedules")
end

When /^I PUT a custom bell schedule$/ do
  put_custom_entity("bellSchedules")
end

When /^I DELETE the custom bell schedule$/ do
  delete_custom_entity("bellSchedules")
end

Then /^I GET the deleted custom bell schedule$/ do
  get_deleted_custom_entity("bellSchedules")
end

When /^I PUT the bell schedule$/ do
  @expected_entity = {
                       "bellScheduleName" => "Grade School Schedule",
                       "educationOrganizationId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
                       "meetingTime"  =>  {
                                            "classPeriodId" => "97094f3eb0e089264bbc1d937a1d22b5c7f668af_id",
                                             "startTime" => "09:55:00.000",
                                             "endTime" => "10:45:00.000"
                                          },
                       "gradeLevels" => [
                                         "First grade",
                                         "Second grade",
                                         "Third grade",
                                         "Fourth grade"
                                       ],
                       "calendarDateReference" => "2012ai-7963b924-ceb0-11e1-8af5-0a0027000000"
                     }
  put_entity("bellSchedules")
end

Then /^I PATCH the bell schedule$/ do
  @expected_patch_entity = {
                        "gradeLevels" => [
                                         "First grade",
                                         "Fifth grade"
                                        ]
                  }
  @expected_entity = {
                        "bellScheduleName" => "Grade School Schedule",
                        "educationOrganizationId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb",
                        "meetingTime"  =>  {
                                             "classPeriodId" => "97094f3eb0e089264bbc1d937a1d22b5c7f668af_id",
                                              "alternateDayName" => "Beige",
                                              "startTime" => "09:00:00.000",
                                              "endTime" => "09:55:00.000",
                                              "officialAttendancePeriod" => true
                                           },
                        "gradeLevels" => @expected_patch_entity["gradeLevels"],
                        "calendarDateReference" => "2012ai-7963b924-ceb0-11e1-8af5-0a0027000000"
                      }
  patch_entity("bellSchedules")
end

When /^I try the not supported PATCH for custom bell schedule$/ do
  patch_custom_entity("bellSchedules")
end

When /^I try the not supported POST for the bell schedule id endpoint$/ do
  post_id("bellSchedules")
end

When /^I try the not supported PUT for the bell schedule list endpoint$/ do
  put_list("bellSchedules")
end

When /^I try the not supported PATCH for the bell schedule list endpoint$/ do
  patch_list("bellSchedules")
end

When /^I try the not supported DELETE for the bell schedule list endpoint$/ do
  delete_list("bellSchedules")
end
