
 
Given /^I make a call to \/token with an invalid auth code$/ do
  code = "bad_code"
  client = "2zhRrEXh8r"
  @format = "application/json"
  url = Property['api_server_url']+"/api/oauth/token?code=#{code}&redirect_uri=blah&client_id=#{client}&client_secret=blah"
  headers = {:content_type => @format}
  @res = RestClient.get(url, headers){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

Given /^I make a call to \/token with an invalid client_id$/ do
  code = "c-820d0646-551b-4086-934e-5129263711ad"
  client = "bad_client"
  @format = "application/json"
  url = Property['api_server_url']+"/api/oauth/token?code=#{code}&redirect_uri=blah&client_id=#{client}&client_secret=blah"
  headers = {:content_type => @format}
  @res = RestClient.get(url, headers){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end

Given /^I make a call to \/token with an expired auth code$/ do
  code = "c-820d0646-551b-4086-934e-5129263711ad"
  client = "2zhRrEXh8r"
  @format = "application/json"
  url = Property['api_server_url']+"/api/oauth/token?code=#{code}&redirect_uri=blah&client_id=#{client}&client_secret=blah"
  headers = {:content_type => @format}
  @res = RestClient.get(url, headers){|response, request, result| response }
  assert(@res != nil, "Response from rest-client GET is nil")
end


Then /^I get a "(.*?)" response code$/ do |arg1|
  assert(@res.code.to_s == arg1, "Response from rest-client GET should be 400")
  puts @res.inspect
end

Then /^the response error is "(.*?)"$/ do |arg1|
  json = JSON.parse(@res.body)
  assert(json["error"] == arg1, "Error should be #{arg1}")
end

