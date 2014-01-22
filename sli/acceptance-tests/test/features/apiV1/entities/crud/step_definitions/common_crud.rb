# structure to hold posted ids for cleanup
# Hash of <endpoint> => [<id0>,...]
$posted_ids = {}

def clear_posted_ids
  $posted_ids = {}
end

def post_entity(endpoint)
  puts "token: " + @sessionId
  puts "expected entity body: " + @expected_entity.to_json.to_s
  restHttpPost("/v1/#{endpoint}", @expected_entity.to_json, 'application/vnd.slc+json')
  assert(@res.code == 201, "#{@res.code} - Could not create entity #{@expected_entity.to_json}.")
  @location = @res.raw_headers['location'][0]
  puts "location: " + @location
  @id = @location.split("/").last
  puts "id: " + @id

  # save the posted id in a stack in cased needed for cleanup
  if $posted_ids[endpoint].nil?
    $posted_ids[endpoint] = [@id]
  else
    # pre-pend so we can just use 'each' to cleanup in reverse order
    $posted_ids[endpoint].unshift(@id)
  end

  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "#{@res.code} - Could not fetch newly created entity #{@expected_entity.to_json}.")
end

def get_entity
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 200, "#{@res.code} - Could not fetch entity #{@expected_entity.to_json}.")
  actual = JSON.parse @res
  actual.delete('id')
  compare_links(actual) if !@expected_links.nil?
  actual.delete('links')
  compare_type(actual) if !@expected_type.nil?
  actual.delete('entityType')
  assert(actual.eql?(@expected_entity),"GET contents different to that expected #{actual.to_json}.")
end

def compare_links(actual)
  actual["links"].each do |link|
    href = link["href"]
    link["href"] = href.sub(Property['api_server_url'] + "/api/rest/v1.5/", '')
  end
  assert(actual["links"].eql?(@expected_links["links"]),"links contents different to that expected #{actual["links"].to_json}.")
end

def compare_type(actual)
  assert(actual['entityType'].eql?(@expected_type),"Entity Type contents different to that expected #{actual["entityType"].to_json}.")
end

def unsupported_put(endpoint)
  restHttpPut("/v1/#{endpoint}/#{@id}", @expected_entity.to_json, 'application/vnd.slc+json')
  assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

def unsupported_patch(endpoint)
  restHttpPatch("/v1/#{endpoint}/#{@id}", Hash.new.to_json, 'application/vnd.slc+json')
  assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

def deleteEntity(endpoint)
  restHttpDelete("/v1/#{endpoint}/#{@id}", 'application/vnd.slc+json')
  assert(@res.code == 204, "Unexpected HTTP code returned: #{@res.code}.")
end

def delete_posted_ids()
  $posted_ids.each_pair do |endpoint, ids|
    ids.each { |id|
      restHttpDelete("/v1/#{endpoint}/#{id}", 'application/vnd.slc+json')
      assert(@res.code == 204, "Unexpected HTTP code returned: #{@res.code}.")
    }
  end
end

def post_custom_entity(endpoint)
  @expected_custom_entity = {
                              "some field" => "some value",
                              "some other field" => 44
                            }
  restHttpPost("/v1/#{endpoint}/#{@id}/custom", @expected_custom_entity.to_json, 'application/vnd.slc+json')
  assert(@res.code == 201, "#{@res.code} - Could not create entity #{@expected_custom_entity.to_json}")
end

def get_custom_entity(endpoint)
  restHttpGet("/v1/#{endpoint}/#{@id}/custom", 'application/vnd.slc+json')
  assert(@res.code == 200, "#{@res.code} - Could not fetch entity #{@expected_custom_entity.to_json}")
  actual = JSON.parse @res
  assert(actual.eql?(@expected_custom_entity),"GET contents different to that expected #{actual.to_json}")
end

def put_custom_entity(endpoint)
  @expected_custom_entity = {
                              "some field" => "some other value",
                              "additional field" => 666
                            }
  restHttpPut("/v1/#{endpoint}/#{@id}/custom", @expected_custom_entity.to_json, 'application/vnd.slc+json')
  assert(@res.code == 204, "#{@res.code} - Could not update custom entity #{@expected_custom_entity.to_json}")
end

def delete_custom_entity(endpoint)
  restHttpDelete("/v1/#{endpoint}/#{@id}/custom", 'application/vnd.slc+json')
  assert(@res.code == 204, "#{@res.code} - Could not delete custom entity #{@expected_custom_entity.to_json}")
end

def get_deleted_custom_entity(endpoint)
  restHttpGet("/v1/#{endpoint}/#{@id}/custom", 'application/vnd.slc+json')
  assert(@res.code == 404, "Unexpected HTTP code returned: #{@res.code}")
end

Then /^I GET the deleted entity/ do
  restHttpGetAbs(@location, 'application/vnd.slc+json')
  assert(@res.code == 404, "Unexpected HTTP code returned: #{@res.code}.")
end

def put_entity(endpoint)
  restHttpPut("/v1/#{endpoint}/#{@id}", @expected_entity.to_json, 'application/vnd.slc+json')
  assert(@res.code == 204, "#{@res.code} - Could not update entity #{@expected_entity.to_json}")
end

def patch_entity(endpoint)
 restHttpPatch("/v1/#{endpoint}/#{@id}", @expected_patch_entity.to_json, 'application/vnd.slc+json')
 assert(@res.code == 204, "#{@res.code} - Could not update entity with PATCH: #{@expected_patch_entity.to_json}")
end

def patch_custom_entity(endpoint)
   restHttpPatch("/v1/#{endpoint}/#{@id}/custom", Hash.new.to_json, 'application/vnd.slc+json')
   assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

def patch_list(endpoint)
   restHttpPatch("/v1/#{endpoint}", Hash.new.to_json, 'application/vnd.slc+json')
   assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

def put_list(endpoint)
   restHttpPut("/v1/#{endpoint}", @expected_entity.to_json, 'application/vnd.slc+json')
   assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

def delete_list(endpoint)
   restHttpDelete("/v1/#{endpoint}", 'application/vnd.slc+json')
   assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

def post_id(endpoint)
   restHttpPost("/v1/#{endpoint}/#{@id}", @expected_entity.to_json, 'application/vnd.slc+json')
   assert(@res.code == 405, "Unexpected HTTP code returned: #{@res.code}.")
end

# BELOW LIES IMPROVED CODE

Then /^the response resource should have an id$/ do
  entity_id.should_not be_nil
end

Then /^the response resource entity type should be "(.*?)"$/ do |entity_type|
  @entity['entityType'].should == entity_type
end

Then /^the response resource should have HATEOAS links for (.*)$/ do |resource_name|
  resource_name = resource_name.gsub(' ','_')
  verify_common_links resource_name
  send("verify_#{resource_name}_entity_links")
end

def post_custom_data(endpoint, id)
  @custom_data = {
      "some field" => "some value",
      "some other field" => 44
  }
  restHttpPost("#{endpoint}/#{id}/custom", @custom_data.to_json, 'application/vnd.slc+json')
end

def put_custom_data(endpoint, id)
  @custom_data = {
      "some field" => "some other value",
      "some other field" => 66
  }
  restHttpPut("#{endpoint}/#{id}/custom", @custom_data.to_json, 'application/vnd.slc+json')
end

# Attempt to find the class period by ID
#   - parse the response if 200,
#   - return nil if 404;
#   - otherwise, assert an expectation failure
def find_custom_data(endpoint, id)
  restHttpGet("#{endpoint}/#{id}/custom")
  case @res.code
    when 200
      JSON.parse(@res)
    when 404
      nil
    else
      @res.code.to_s.should match(/^(200|404)$/)
  end
end


def entity_id
  @entity['id'] if @entity
end

def verify_common_links(resource_name)
  links = @entity['links']
  links.should_not be_empty
  endpoint = send("#{resource_name}_endpoint")
  links.should include( build_link('self', make_self_url(endpoint, entity_id)) )
  links.should include( build_link('custom', make_custom_url(endpoint, entity_id)) )
end

def build_link(rel, href)
  {'rel' => rel, 'href' => href}
end

def build_entity_link(rel, entity_id)
  build_link rel, make_entity_url(entity_id, rel)
end

def build_query_link(rel, query)
  build_link rel, make_query_url(query, rel)
end

def make_self_url(endpoint, entity_id)
  "#{url_base}#{endpoint}/#{entity_id}"
end

def make_custom_url(endpoint, entity_id)
  "#{url_base}#{endpoint}/#{entity_id}/custom"
end

def make_entity_url(entity_id, rel)
  "#{url_base}#{endpoint_for_rel rel}/#{entity_id}"
end

def make_query_url(query, rel)
  "#{url_base}#{endpoint_for_rel rel}?#{query}"
end

def url_base
  "#{Property['api_server_url']}/api/rest"
end

def endpoint_for_rel(rel)
  map = {
      'getStudent' => 'students',
      'getSchool' => 'schools',
      'getEducationOrganization' => 'educationOrganizations',
      'getSection' => 'sections',
      'getSections' => 'sections',
      'getBellSchedules' => 'bellSchedules'
  }
  "/v1.5/#{map[rel]}"
end

# Match against the resource name (typically plural) followed by the entity ID
# (e.g. attendances/ab5b8dd1d4d5c8b613ff60b86a6a2f0fd610934c_id)
def resource_regexp(url)
  %r{#{url}/[a-z0-9]+_id$}
end





