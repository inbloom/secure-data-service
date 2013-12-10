When /^I POST a bell schedule$/ do
  @expected_entity = {
                       "bellScheduleName" => "Grade School Schedule",
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
                       "bellScheduleName" => "Updated Grade School Schedule",
                       "meetingTime"  =>  {
                                            "classPeriodId" => "97094f3eb0e089264bbc1d937a1d22b5c7f668af_id",
                                             "startTime" => "09:55:00.000",
                                             "endTime" => "10:45:00.000"
                                          },
                       "gradeLevels" => [
                                         "First grade",
                                         "Fifth grade"
                                       ],
                       "calendarDateReference" => "2012ai-7963b924-ceb0-11e1-8af5-0a0027000000"
                     }
  put_entity("bellSchedules")
end

Then /^I PATCH the bell schedule$/ do
  @expected_patch_entity = {
                     "bellScheduleName" => "Tweaked Grade School Schedule"
                  }
  @expected_entity = {
                        "bellScheduleName" => @expected_patch_entity["bellScheduleName"],
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
  patch_entity("bellSchedules")
end

When /^I try the not supported PATCH for custom bell schedule$/ do
  patch_custom_entity("bellSchedules")
end
