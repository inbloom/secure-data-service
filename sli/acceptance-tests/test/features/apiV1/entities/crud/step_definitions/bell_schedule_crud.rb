After('@bell_schedule') do
  delete_bell_schedule(entity_id)
end

Given /^I create a (?:school\-level )?bell schedule$/ do
  restHttpPost(bell_schedule_endpoint, bell_schedule_resource.to_json)
  @res.code.should == 201
  restHttpGetAbs(@res.headers[:location])
  @entity = JSON.parse(@res)
end

When /^I POST a new bell schedule$/ do
  @new_entity = bell_schedule_resource
  restHttpPost(bell_schedule_endpoint, @new_entity.to_json)
end

When /^I POST to that bell schedule$/ do
  restHttpPost("#{bell_schedule_endpoint}/#{entity_id}", bell_schedule_resource.to_json)
end

When /^I DELETE that bell schedule$/ do
  restHttpDelete("#{bell_schedule_endpoint}/#{entity_id}")
end

When /^I PUT that bell schedule$/ do
  @updated = bell_schedule_resource
  @updated['gradeLevels'] = ['First grade','Third grade']
  restHttpPut("#{bell_schedule_endpoint}/#{entity_id}", @updated.to_json)
end

When /^I PATCH that bell schedule$/ do
  @updated = {
      "gradeLevels" => [
          "First grade",
          "Fifth grade"
      ]
  }
  restHttpPatch("#{bell_schedule_endpoint}/#{entity_id}", @updated.to_json, 'application/vnd.slc+json')
end

Then /^the bell schedule should be updated$/ do
  entity = pare_entity find_bell_schedule(entity_id)
  entity.should == bell_schedule_resource.merge(@updated)
end

When /^I PUT the bell schedule list$/ do
  restHttpPut(bell_schedule_endpoint, {}.to_json)
end

When /^I PATCH the bell schedule list$/ do
  restHttpPatch(bell_schedule_endpoint, {}.to_json)
end

When /^I DELETE the bell schedule list$/ do
  restHttpDelete(bell_schedule_endpoint)
end

When /^I GET that bell schedule$/ do
  restHttpGet("#{bell_schedule_endpoint}/#{entity_id}")
  @entity = JSON.parse(@res)
end

When /^I POST custom data to that bell schedule$/ do
  post_custom_data(bell_schedule_endpoint, entity_id)
end

When /^I PUT the custom data for that bell schedule$/ do
  put_custom_data(bell_schedule_endpoint, entity_id)
end

When /^I GET the custom data for that bell schedule$/ do
  restHttpGet("#{bell_schedule_endpoint}/#{entity_id}/custom")
end

When /^I PATCH the bell schedule custom data$/ do
  restHttpPatch("#{bell_schedule_endpoint}/#{entity_id}/custom", {}.to_json)
end

When /^I DELETE the custom data for that bell schedule$/ do
  restHttpDelete("#{bell_schedule_endpoint}/#{entity_id}/custom")
end

When /^I GET the list of bell schedules$/ do
  restHttpGet(bell_schedule_endpoint)
end

Then /^the response resource should contain multiple bell schedules$/ do
  entities = JSON.parse @res
  entities.should be_kind_of Array # ensures its an array
  entities.count.should be >= 1 # should be at least one
  entities.first['entityType'].should == 'bellSchedule'
end

Then /^the response location header should link to the new bell schedule$/ do
  location = @res.headers[:location]
  location.should match( resource_regexp(bell_schedule_endpoint) )
end

Then /^the bell schedule should be saved$/ do
  _id = @res.headers[:location].split('/').last
  @entity = find_bell_schedule(_id)
  entity_id.should == _id
end

Then /^the bell schedule should be deleted$/ do
  find_bell_schedule(entity_id).should be_nil
end

Then /^the response resource should contain expected bell schedule data$/ do
  pare_entity(@entity).should == bell_schedule_resource
end

Then /^I should only see bell schedules for my education organizations?$/ do
  bell_schedules = JSON.parse @res
  my_ed_org_ids = ed_orgs_for_staff(current_user).map{|ed_org| ed_org['id']}.uniq
  bell_schedule_ed_org_ids = bell_schedules.map{|cp| cp['educationOrganizationId']}.uniq
  bell_schedule_ed_org_ids.each {|id| my_ed_org_ids.should include(id)}
end

Then /^the response resource should be a list of bell schedules$/ do
  list = JSON.parse @res
  list.should be_kind_of(Array)
  list.each{|l| l['entityType'].should == 'bellSchedule'}
end

When /^I get the bell schedules for that school$/ do
  find_bell_schedules_for_ed_org(bell_schedule_resource['educationOrganizationId'])
end

Then /^the result should include that bell schedule$/ do
  @entities = JSON.parse @res
  bell_schedules = @entities.map{|entity| pare_entity(entity)}
  bell_schedules.should include(bell_schedule_resource)
end

def bell_schedule_resource
  {
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
end

def bell_schedule_endpoint
  "/v1.5/bellSchedules"
end

def education_organization_endpoint
  "/v1.5/educationOrganizations"
end


# Attempt to find the bell schedule by ID
#   - parse the response if 200,
#   - return nil if 404;
#   - otherwise, assert an expectation failure
def find_bell_schedule(id)
  restHttpGet("#{bell_schedule_endpoint}/#{id}")
  case @res.code
    when 200
      JSON.parse(@res)
    when 404
      nil
    else
      @res.code.to_s.should match(/^(200|404)$/)
  end
end

def find_bell_schedules_for_ed_org(ed_org_id)
  restHttpGet("#{education_organization_endpoint}/#{ed_org_id}/bellSchedules")
end

def delete_bell_schedule(id)
  restHttpDelete("#{bell_schedule_endpoint}/#{id}") if id
end

def verify_bell_schedule_entity_links
  links = @entity['links']

  links.should include( build_entity_link 'getSchool', bell_schedule_resource['educationOrganizationId'])
  links.should include( build_entity_link 'getEducationOrganization', bell_schedule_resource['educationOrganizationId'])
  links.should include( build_entity_link 'getClassPeriod', bell_schedule_resource['meetingTime']['classPeriodId'])
  links.should include( build_entity_link 'getCalendarDate', bell_schedule_resource['calendarDateReference'])
end