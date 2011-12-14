require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'


Given /^I have access to all students and schools$/ do
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+@user+"&password="+@passwd
  res = RestClient.get(url){|response, request, result| response }
  @cookie = res.body[res.body.rindex('=')+1..-1]
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Then /^I should receive a ID for the newly created student\-school\-association$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  assocId = s[s.rindex('/')+1..-1]
  assert(assocId != nil, "Student-School-Association ID is nil")
end


When /^I navigate to POST "([^"]*)"$/ do |uri|
  if @format == "application/json"
    data = @fields
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+uri
    @res = RestClient.post(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client POST is nil")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

When /^I navigate to PUT "([^"]*)"$/ do |uri|
  if @format == "application/json"
      url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+uri
      @res = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
      assert(@res != nil, "Response from rest-client GET is nil")
      assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
      modified = JSON.parse(@res.body)
      @fields.each do |key, value|
        modified[key] = value
      end
      
      url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+uri
      @res = RestClient.put(url, modified.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
      assert(@res != nil, "Response from rest-client PUT is nil")
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
end

When /^I attempt to update a non\-existing association "([^"]*)"$/ do |uri|
  data = {}
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+uri
  @res = RestClient.put(url, data.to_json, {:content_type => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
end
