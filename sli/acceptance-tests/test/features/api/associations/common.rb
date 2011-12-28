require_relative '../../utils/sli_utils.rb'

Transform /^(?:is|equal) "([^"]*)"$/ do |step_arg|
  retval = "FIRST_GRADE" if step_arg == "First grade"
  retval = "SECOND_GRADE" if step_arg == "Second grade"
  retval = "TENTH_GRADE" if step_arg == "Tenth grade"
  retval = step_arg if retval == nil
  retval
end


Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |usr, pass|
  idpLogin(usr, pass)
  assert(@cookie != nil, "Cookie retrieved was nil")
end


Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
end

Given /^"([^"]*)" (is "[^"]*")$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

Then /^I should receive a ID for the newly created (.*)$/ do |type|
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  assocId = s[s.rindex('/')+1..-1]
  assert(assocId != nil, "#{type} ID is nil")
end

Then /^I should receive a return code of (\d+)$/ do |code|
  assert(@res.code == Integer(code), "Return code was not expected: #{@res.code.to_s} but expected #{code}")
end

Then /^I should receive a link where rel is "([^"]*)" and href ends with "([^"]*)"$/ do |rel, href|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?("links"), "Response contains no links")
  found = false
  @data["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

Then /^I should receive a collection with "([^"]*)" elements$/ do |size|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
  assert(@data.length == Integer(size), "Expected response of size #{size}, received #{@data.length}");
end

Then /^the collection should contain a link where rel is "([^"]*)" and href ends with "([^"]*)"$/ do |rel, href|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Array), "Response contains #{@data.class}, expected Array")
  found = false
  @data.each do |ref|
    if ref["link"]["rel"] == rel && ref["link"]["href"] =~ /#{Regexp.escape(href)}$/
      found = true;
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

Then /^"([^"]*)" should (equal "[^"]*")$/ do |key, value|
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?(key), "Response does not contain key #{key}")
  assert(@data[key] == value, "Expected #{key} to equal #{value}, received #{@data[key]}")
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