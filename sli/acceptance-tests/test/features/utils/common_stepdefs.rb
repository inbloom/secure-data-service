=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require 'mongo'
require 'securerandom'
require 'rumbster'
require 'message_observers'

Before do
  @entity_type_to_uri = {
      "studentAssessment" => "studentAssessments",
      "studentSchoolAssociation" => "studentSchoolAssociations",
      "teacherSectionAssociation" => "teacherSectionAssociations",
      "session" => "sessions",
      "gradingPeriod" => "gradingPeriods",
      "courseOffering" => "courseOfferings",
      "course" => "courses"
}
end
Given /^I am logged in using "([^\"]*)" "([^\"]*)" to realm "([^\"]*)"$/ do |user, pass, realm|
  @user = user
  @passwd = pass
  @realm = realm
  idpRealmLogin(@user, @passwd, @realm)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^\"]*)"$/ do |fmt|
  ["application/json", "application/json;charset=utf-8", "application/xml", "text/plain", "application/vnd.slc.full+json", "application/vnd.slc+json", "application/vnd.slc.full+json;charset=utf-8", "application/vnd.slc+json;charset=utf-8"].should include(fmt)
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

When /^I navigate to GET "([^\"]*)"$/ do |uri|
  if defined? @queryParams
    uri = uri + "?#{@queryParams.join('&')}"
  end
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  contentType = contentType(@res).gsub(/\s+/,"")
  jsonTypes = ["application/json", "application/json;charset=utf-8", "application/vnd.slc.full+json", "application/vnd.slc+json" "application/vnd.slc.full+json;charset=utf-8", "application/vnd.slc+json;charset=utf-8"].to_set

  @headers=@res.raw_headers.to_hash()  
  if jsonTypes.include? contentType
    @result = JSON.parse(@res.body)
    assert(@result != nil, "Result of JSON parsing is nil")
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
    #puts @result
  else
    @result = {}
  end
end

Given /^parameter "([^\"]*)" is "([^\"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "=" "#{value}"}
end

Given /^all parameters are cleared$/ do
  @queryParams = [] 
end

Given /^parameter "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |param, op, value|
  if !defined? @queryParams
    @queryParams = []
  end
  @queryParams.delete_if do |entry|
    entry.start_with? param
  end
  @queryParams << URI.escape("#{param}#{op}#{value}")
end

When /^I navigate to PATCH "([^"]*)"$/ do |uri|
  data = prepareData(@format, @patch_body)
  restHttpPatch(uri, data)
  assert(@res != nil, "Response from rest-client PATCH is nil")
end

When /^I navigate to DELETE "([^"]*)"$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
  if !rel.nil? && !rel.empty?
    @result["links"].each do |link|
      if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
        found = true
      end
    end
  else
    found = true
  end
   assert(found, "Link not found rel=#{rel}, href ends with=#{href}")  
end

Then /^I should not receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
  if !rel.nil? && !rel.empty?
    @result["links"].each do |link|
      if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
        found = true
      end
    end
  else
    found = true
  end
   assert(!found, "Link found rel=#{rel}, href ends with=#{href}")  
end


When /^I PUT the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil '#{@res}'")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil '#{@res.body}'")
end

When /^I try to PUT the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil '#{@res}'")
end

When /^I POST the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPost(url, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Given /^I have a "([^"]*)" SMTP\/Email server configured$/ do |live_or_mock|
  sender_email_address = "hlufhdsaffhuawiwhfkj@slidev.org"
  @email_name = "SLC Admin"
  test_port = 2525
  @live_email_mode = (live_or_mock == "live")
  
  if @live_email_mode
    @email_conf = {
      :host => PropLoader.getProps['email_smtp_host'],
      :port => PropLoader.getProps['email_smtp_port']
    }
  else
    @rumbster = Rumbster.new(test_port)
    @message_observer = MailMessageObserver.new
    @rumbster.add_observer @message_observer
    @rumbster.start
    @email_conf = {
      :host => '127.0.0.1',
      :port => test_port
    }
  end
  @email_conf[:sender_name] = @email_name
  @email_conf[:replacer] = { "__URI__" => "http://localhost:3000"}
  @email_conf[:sender_email_addr] = sender_email_address
end

Then /^I get a link to "(.*?)"$/ do |linkName|
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  links = result["links"]
  @link = nil
  for l in links do
          if l['rel'] == linkName
                  @link = l["href"]
          end
  end
  assert(@link != nil, "Link to aggregates not found")
end

Then /^I navigate to that link$/ do
  restHttpGetAbs(@link)
  @result = JSON.parse(@res.body)
end

Given /^that dashboard has been authorized for all ed orgs$/ do
  disable_NOTABLESCAN()
  allLeaAllowApp("inBloom Dashboards")
  enable_NOTABLESCAN()
end

Given /^that databrowser has been authorized for all ed orgs$/ do
  allLeaAllowApp("inBloom Data Browser")
end

Then /^I should receive a link named "([^"]*)"$/ do |arg1|
  step "in an entity, I should receive a link named \"#{arg1}\""
end

Then /^in an entity, I should receive a link named "([^"]*)"$/ do |arg1|
  @the_link = []
  @id_link = []
  @result = JSON.parse(@res.body)
  found = false
  @result = [@result] unless @result.is_a? Array
  links = @result.map{|entity| entity["links"]}.flatten
  @result.each do |entity|
    #puts entity
    assert(entity.has_key?("links"), "Response #{entity} contains no links")
    entity["links"].each do |link|
      if link["rel"] == arg1
        @the_link.push link['href']
        @id_link.push({"id"=>entity["id"],"link"=>link["href"]})
        found = true
      end
    end
  end
  assert(found, "Link not found rel=#{arg1} only found #{links.map{|l| l['rel']}}")
end

Then /^in an entity "([^"]*)", I should receive a link named "([^"]*)"$/ do |id, arg1|
  @the_link = []
  @id_link = []
  @result = JSON.parse(@res.body)
  found = false
  @result = [@result] unless @result.is_a? Array
  @result.each do |entity|
    next if entity["id"] != id
    #puts entity
    assert(entity.has_key?("links"), "Response contains no links")
    entity["links"].each do |link|
      if link["rel"] == arg1
        @the_link.push link['href']
        @id_link.push({"id"=>entity["id"],"link"=>link["href"]})
        found = true
      end
    end
  end
  assert(found, "Link not found rel=#{arg1}")
end

When /^I navigate to GET the link named "([^"]*)"$/ do |arg1|
  #Try to make test more deterministic by using ordered search
  @the_link = @the_link.sort
  @the_link.each { |link|
    restHttpGetAbs(link)
    @result = JSON.parse(@res.body)
    break if @result.length > 0
  }
end
