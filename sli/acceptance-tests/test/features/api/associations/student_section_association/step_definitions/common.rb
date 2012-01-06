require_relative '../../../../utils/sli_utils.rb'

#vnd.slc+json format is not ready for testing
#remove this transform to switch to new format
Transform /^application\/vnd\.slc\+json$/ do |args|
  "application/json"
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |usr, pass|
  idpLogin(usr, pass)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

Given /^(\w+) is "([^"]*)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

When /^(\w+) is updated to "([^"]*)"$/ do |key, new_value|
  if !defined? @updates
    @updates = {}
  end
  @updates[key] = new_value
end

When /^I navigate to GET "([^"]*)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  if @format == "application/json"
    begin
      @data = JSON.parse(@res.body);
    rescue
      @data = nil
    end
  elsif @format == "application/xml"
    assert(false, "XML not supported yet")
  else
    assert(false, "Unsupported MediaType")
  end
end

When /^I navigate to PUT "([^"]*)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET was nil")
  assert(@res.code == 200, "Return code was not expected: #{@res.code} but expected 200")
  
  if @format == "application/json"
    dataH = JSON.parse(@res.body)
    @updates.each do |key, value|
      dataH[key] = value
    end
    data = dataH.to_json
  elsif @format == "application/xml"
    data = Document.new(@res.body)
  end
  restHttpPut(uri, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I navigate to POST "([^"]*)"$/ do |uri|
  if @format == "application/json"
    dataH = @fields
    data=dataH.to_json
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to DELETE "([^"]*)"$/ do |arg1|
  restHttpDelete(arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a collection of (\d+) (.*) that resolve to$/ do |size, type|
  assert(@data != nil, "Response contained no data")
  assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
  assert(@data.length == Integer(size), "Expected response of size #{size}, received #{@res.length}")
end

Then /^I should receive 1 (.*)$/ do |type|
  assert(@data != nil, "Response contained no data")
  assert(@data.is_a?(Hash), "Response contains a #{@data.class}, expected Hash")
end

Then /^I should receive a ID for the newly created (.*)$/ do |type|
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  assocId = s[s.rindex('/')+1..-1]
  assert(assocId != nil, "#{type} ID is nil")
  @new_id = assocId
end

Then /^I should receive a return code of (\d+)$/ do |code|
  assert(@res.code == Integer(code), "Return code was not expected: #{@res.code.to_s} but expected #{code}")
end

Then /^the (\w+) should be "([^"]*)"$/ do |key, value|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?(key), "Response does not contain key #{key}")
  assert(@data[key] == value, "Expected #{key} to equal #{value}, received #{@data[key]}")
end

Then /^I should receive a link named "([^"]*)" with URI for "([^"]*)"$/ do |link_name, uri|
  found = false
  if @data.is_a?(Hash)
    @data['links'].each do |link|
      if link["rel"] == link_name && link["href"] =~ /#{Regexp.escape(uri)}$/
        found = true
      end
    end
  else
    @data.each do |item|
      link = item['link']['href']
      response =  RestClient.get(link, {:accept => @format, :sessionId => @sessionId}){|response, request, result| response }
      response = JSON.parse(response.body)
      response['links'].each do |link|
        if link["rel"] == link_name && link["href"] =~ /#{Regexp.escape(uri)}$/
          found = true
        end
      end
    end
  end
  assert(found, "Link was not found! looking for #{link_name} in #{uri}")
end
