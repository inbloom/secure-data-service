require 'rumbster'
require 'message_observers'

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

Given /^parameter "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |param, op, value|
  if !defined? @queryParams
    @queryParams = []
  end
  @queryParams.delete_if do |entry|
    entry.start_with? param
  end
  @queryParams << URI.escape("#{param}#{op}#{value}")
end

When /^I navigate to DELETE "([^"]*)"$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  assert(@result.has_key?("links"), "Response contains no links")
  found = false
  @result["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

When /^I PUT the entity to "([^"]*)"$/ do |url|
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil '#{@res}'")
  assert(@res.body == nil || @res.body.length == 0, "Response body from rest-client PUT is not nil '#{@res.body}'")
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
  @mode = (live_or_mock == "live")
  
  if @mode
    @email_conf = {
      :host => 'mon.slidev.org',
      :port => 3000
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
