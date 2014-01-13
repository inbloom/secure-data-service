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





