require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^<(.+)>$/ do |arg1|
  id = "6c572483-fe75-421c-9588-d82f1f5f3af5" if arg1 == "'Writing Advanced Placement Test' ID"
  id = "714c1304-8a04-4e23-b043-4ad80eb60992" if arg1 == "'Alfonso' ID"
  id = "e1af7127-743a-4437-ab15-5b0dacd1bde0" if arg1 == "'Priscilla' ID"
  id = "61f13b73-92fa-4a86-aaab-84999c511148" if arg1 == "'Alden' ID"
  id = "289c933b-ca69-448c-9afd-2c5879b7d221" if arg1 == "'Donna' ID"
  id = "c7146300-5bb9-4cc6-8b95-9e401ce34a03" if arg1 == "'Rachel' ID"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |user, passwd|
  idpLogin(user,passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

When /^I navigate to GET "([^"]*)"$/ do |url|
  restHttpGet(url)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  contentType = contentType(@res)
  if /application\/json/.match contentType
    @result = JSON.parse(@res.body)
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
    puts @result
  else
    @result = {}
  end
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end


Then /^I should receive a link named "([^\"]*)" with URI "([^\"]*)"$/ do |rel, href|
  @result["links"].should_not == nil
  found = false
  @result["links"].each do |link|;
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
      break
    end
  end
  assert(found, "Did not find a link rel=#{rel} href=#{href}")
end

Then /^I should receive a collection of (\d+) student links$/ do |size|
  assert(@result != nil, "Response contains no data")
  assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")
  assert(@result.length == Integer(size), "Expected response of size #{size}, received #{@result.length}");
  
  @ids = Array.new
  @result.each do |link|
    if link["link"]["rel"]=="self"
      @ids.push(link["id"])
    end
  end
end

Then /^after resolution, I should receive a "([^"]*)" with ID "([^"]*)"$/ do |arg1, arg2| 
  found = false
  @ids.each do |id|
    uri = "/students/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body) 
      if dataH['id'] == arg2
        found=true 
        break
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  
  assert(found, "Object #{arg1} with id #{arg2} was not found")
end

Then /^I should receive a collection of (\d+) Section links$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should receive a collection of (\d+) School links$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should receive a collection of (\d+) Assessment links$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I should receive a collection of (\d+) Teacher links$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

def contentType(response) 
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['content-type'] != nil, "There is no content-type set in the response")
  headers['content-type'][0]
end
