After('@class_period') do
  delete_class_period(entity_id)
end

Given /^I create a class period$/ do
  restHttpPost(class_period_endpoint, class_period_resource.to_json)
  @res.code.should == 201
  restHttpGetAbs(@res.headers[:location])
  @entity = JSON.parse(@res)
end

When /^I POST a new class period$/ do
  @new_entity = class_period_resource
  restHttpPost(class_period_endpoint, @new_entity.to_json)
end

When /^I POST to that class period$/ do
  restHttpPost("#{class_period_endpoint}/#{entity_id}", class_period_resource.to_json)
end

When /^I DELETE that class period$/ do
  restHttpDelete("#{class_period_endpoint}/#{entity_id}")
end

When /^I PUT that class period$/ do
  restHttpPut("#{class_period_endpoint}/#{entity_id}", class_period_resource.to_json)
end

When /^I PATCH that class period$/ do
  restHttpPatch("#{class_period_endpoint}/#{entity_id}", class_period_resource.to_json)
end

When /^I PUT the class period list$/ do
  restHttpPut(class_period_endpoint, {}.to_json)
end

When /^I PATCH the class period list$/ do
  restHttpPatch(class_period_endpoint, {}.to_json)
end

When /^I DELETE the class period list$/ do
  restHttpDelete(class_period_endpoint)
end

When /^I GET that class period$/ do
  restHttpGet("#{class_period_endpoint}/#{entity_id}")
  @entity = JSON.parse(@res)
end

When /^I POST custom data to that class period$/ do
  post_custom_data(class_period_endpoint, entity_id)
end

When /^I PUT the custom data for that class period$/ do
  put_custom_data(class_period_endpoint, entity_id)
end

When /^I GET the custom data for that class period$/ do
  restHttpGet("#{class_period_endpoint}/#{entity_id}/custom")
end

When /^I PATCH the class period custom data$/ do
  restHttpPatch("#{class_period_endpoint}/#{entity_id}/custom", {}.to_json)
end

When /^I DELETE the custom data for that class period$/ do
  restHttpDelete("#{class_period_endpoint}/#{entity_id}/custom")
end

When /^I GET the list of class periods$/ do
  restHttpGet(class_period_endpoint)
end

Then /^the response resource should contain multiple class periods$/ do
  entities = JSON.parse @res
  entities.should respond_to(:count) # ensures its an array
  entities.count.should be >= 1 # should be at least one
  entities.first['entityType'].should == 'classPeriod'
end

Then /^the response location header should link to the new class period$/ do
  location = @res.headers[:location]
  location.should match( resource_regexp(class_period_endpoint) )
end

Then /^the class period should be saved$/ do
  _id = @res.headers[:location].split('/').last
  @entity = find_class_period(_id)
  entity_id.should == _id
end

Then /^the class period should be deleted$/ do
  find_class_period(entity_id).should be_nil
end

Then /^the response resource should contain expected class period data$/ do
  pare_entity(@entity).should == class_period_resource
end

Then /^the results should contain only the class periods for my education organization$/ do
  class_periods = JSON.parse @res
  my_ed_org_ids = ed_orgs_for_staff(current_user).map{|ed_org| ed_org['id']}.uniq.sort
  class_period_ed_org_ids = class_periods.map{|cp| cp['educationOrganizationId']}.uniq.sort
  class_period_ed_org_ids.should == my_ed_org_ids
end

def class_period_resource
  {
    "classPeriodName" => "First Period",
    "educationOrganizationId" => "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
  }
end

def class_period_endpoint
  "/v1.5/classPeriods"
end

# Attempt to find the class period by ID
#   - parse the response if 200,
#   - return nil if 404;
#   - otherwise, assert an expectation failure
def find_class_period(id)
  restHttpGet("#{class_period_endpoint}/#{id}")
  case @res.code
    when 200
      JSON.parse(@res)
    when 404
      nil
    else
      @res.code.to_s.should match(/^(200|404)$/)
  end
end

def delete_class_period(id)
  restHttpDelete("#{class_period_endpoint}/#{id}") if id
end

def verify_class_period_entity_links
  links = remove_common_links(@entity['links'].dup)
  [
    ['getSchool', @entity['educationOrganizationId']],
    ['getEducationOrganization', @entity['educationOrganizationId']]
  ].each do |rel, id|
    link = build_entity_link rel, id
    links.should include( link )
    links.delete link
  end

  [
    ['getSections', "classPeriodId=#{entity_id}"],
    ['getBellSchedules', "meetingTime.classPeriodId=#{entity_id}"]
  ].each do |rel, query|
    link = build_query_link rel, query
    links.should include( link )
    links.delete link
  end

  # Verify that there are no other links
  links.map{|link| link['rel']}.should == []
end