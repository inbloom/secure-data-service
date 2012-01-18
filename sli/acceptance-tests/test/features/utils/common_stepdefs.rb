
Given /^I am logged in using "([^\"]*)" "([^\"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have access to all [^"]*$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^\"]*)"$/ do |fmt|
  ["application/json", "application/xml", "text/plain"].should include(fmt)
  @format = fmt
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive an ID for the newly created ([\w-]+)$/ do |entity|
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  assert(@newId != nil, "After POST, #{entity} ID is nil")
end

When /^I navigate to GET "([^"]*)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  contentType = contentType(@res)
  if /application\/json/.match contentType
    @result = JSON.parse(@res.body)
    assert(@result != nil, "Result of JSON parsing is nil")
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
    puts @result
  else
    @result = {}
  end
end

When /^I navigate to DELETE "([^"]*)"$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
  @result["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end







##############################################################################
##############################################################################
### Util methods ###

def convert(value)
  if /^true$/.match value
    true;
  elsif /^false$/.match value
    false;
  elsif /^\d+\.\d+$/.match value
    Float(value)
  elsif /^\d+$/.match value
    Integer(value)
  else
    value
  end
end

def prepareData(format, hash)
  if format == "application/json"
    hash.to_json
  elsif format == "application/xml"
    raise "XML not implemented"
  else
    assert(false, "Unsupported MIME type")
  end
end

def contentType(response) 
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['content-type'] != nil, "There is no content-type set in the response")
  headers['content-type'][0]
end

#return boolean
def findLink(id, type, rel, href)
  found = false
  uri = type+id
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    dataH["links"].each do |link|
      if link["rel"]==rel and link["href"].include? href
        found = true
        break
      end
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  return found
end

