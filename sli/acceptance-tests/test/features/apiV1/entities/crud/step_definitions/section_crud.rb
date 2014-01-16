When /^I POST a class period for a section$/ do
  @expected_entity = {
                       "classPeriodName" => "dog period",
                       "educationOrganizationId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731"
                     }
  post_entity("classPeriods")
  @class_period_id = @id
end

When /^I DELETE the class period for the section$/ do
  restHttpDelete("/v1/classPeriods/#{@class_period_id}", 'application/vnd.slc+json')
  assert(@res.code == 204, "Unexpected HTTP code returned: #{@res.code}.")
  restHttpGet("/v1/classPeriods/#{@class_period_id}", 'application/vnd.slc+json')
  assert(@res.code == 404, "Unexpected HTTP code returned: #{@res.code}.")
end

When /^I POST a section$/ do
  @expected_entity = {
    "educationalEnvironment" => "Classroom",
    "sessionId" => "377c734f-7c15-455f-9209-ac15b3118236",
    "courseOfferingId" => "dab41da7-8c78-4908-8e61-1d4e9c34c84d",
    "populationServed" => "Regular Students",
    "sequenceOfCourse" => 3,
    "classPeriodId" => "#{@class_period_id}",
    "mediumOfInstruction" => "Independent study",
    "uniqueSectionCode" => "Science 7A - Sec dog",
    "schoolId" => "ec2e4218-6483-4e9c-8954-0aecccfd4731"
                     }
  post_entity("sections")
  @expected_links = { "links" => [
                                   {
                                     "rel" => "self",
                                     "href" =>  "sections/#{@id}"
                                   },
                                   {
                                     "rel" => "custom",
                                     "href" =>  "sections/#{@id}/custom"
                                   },
                                   {
                                     "rel" => "getSchool",
                                     "href" =>  "schools/" + @expected_entity['schoolId']
                                   },
                                   {
                                     "rel" => "getEducationOrganization",
                                     "href" =>  "educationOrganizations/" + @expected_entity['schoolId']
                                   },
                                   {
                                     "rel" => "getSession",
                                     "href" =>  "sessions/" + @expected_entity['sessionId']
                                   },
                                   {
                                     "rel" => "getClassPeriod",
                                     "href" =>  "classPeriods/" + @expected_entity['classPeriodId']
                                   },
                                   {
                                     "rel" => "getCourseOffering",
                                     "href" =>  "courseOfferings/" + @expected_entity['courseOfferingId']
                                   },
                                   {
                                     "rel" => "getGradebookEntries",
                                     "href" =>  "gradebookEntries?sectionId=#{@id}"
                                   },
                                   {
                                     "rel" => "getStudentSectionAssociations",
                                     "href" =>  "sections/#{@id}/studentSectionAssociations"
                                   },
                                   {
                                     "rel" => "getStudents",
                                     "href" =>  "sections/#{@id}/studentSectionAssociations/students"
                                   },
                                   {
                                     "rel" => "getTeacherSectionAssociations",
                                      "href" => "sections/#{@id}/teacherSectionAssociations"
                                   },
                                   {
                                     "rel" => "getTeachers",
                                     "href" =>  "sections/#{@id}/teacherSectionAssociations/teachers"
                                   },
                                   {
                                     "rel" => "getStudentGradebookEntries",
                                     "href" =>  "studentGradebookEntries?sectionId=#{@id}"
                                   },
                                   {
                                     "rel" => "getAttendances",
                                     "href" =>  "attendances?attendanceEvent.sectionId=#{@id}"
                                   },
                                   {
                                     "rel" => "getYearlyAttendances",
                                     "href" =>  "yearlyAttendances?attendanceEvent.sectionId=#{@id}"
                                   },
                                   {
                                     "rel" => "getGrades",
                                     "href" =>  "grades?sectionId=#{@id}"
                                   }
                                 ]
                     }
  puts "expected links: " + @expected_links["links"].to_json
  @expected_type = 'section'
  puts "expected type: " + @expected_type
end

When /^I GET the section$/ do
  get_entity
end

When /^I try the not supported PUT for the section$/ do
  unsupported_put("sections")
end

When /^I try the not supported PATCH for the section$/ do
  unsupported_patch("sections")
end

Then /^I DELETE the section$/ do
  deleteEntity("sections")
end

When /^I POST a custom section$/ do
  post_custom_entity("sections")
end

When /^I GET the custom section$/ do
  get_custom_entity("sections")
end

When /^I PUT a custom section$/ do
  put_custom_entity("sections")
end

When /^I DELETE the custom section$/ do
  delete_custom_entity("sections")
end

Then /^I GET the deleted custom section$/ do
  get_deleted_custom_entity("sections")
end

When /^I try the not supported PATCH for custom section$/ do
  patch_custom_entity("sections")
end
