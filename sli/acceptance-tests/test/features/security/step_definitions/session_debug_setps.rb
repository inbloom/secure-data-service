require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML

require_relative '../../utils/sli_utils.rb'

Given /^I login with "([^"]*)" and "([^"]*)"$/ do |arg1, arg2|
    @user = arg1
    @passwd = arg2
    url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+@user+"&password="+@passwd
    res = RestClient.get(url){|response, request, result| response }
    assert(res.body != nil, "No res")
    @cookie = res.body[res.body.rindex('=')+1..-1]
end

Given /^I get an authentication cookie from the gatekeeper$/ do
    assert(@cookie != nil, "Cookie retrieved was nil")
end

When /^I GET the url "([^"]*)" using that cookie$/ do |arg1|
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+ arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => @cookie}}){|response, request, result| response }
    assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a return code of "([^"]*)"$/ do |arg1|
    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code == Integer(arg1), "Return code was not expected: '"+@res.code.to_s+"' but expected '" + arg1 + "'")
end

Then /^I should see the session debug context in the response body$/ do
    assert(@res != nil, "Response is nil")
    data = JSON.parse(@res.body)
    assert(data != nil, "Response body is nil")
    assert(data['authentication'] != nil, "Session debug context doesn't contain 'authentication'")
    assert(data['authentication']['principal'] != nil, 
           "Session debug context doesn't contain 'authentication.principal'")
    assert(data['authentication']['authenticated'] != nil, 
           "Session debug context doesn't contain 'authentication.authenticated'")
    assert(data['authentication']['authenticated'] == true, 
           "Session debug context 'authentication.authenticated' is not true")
end

When /^I GET the url "([^"]*)" using a blank cookie$/ do |arg1|
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+ arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => ""}}){|response, request, result| response }
end

When /^I GET the url "([^"]*)" using an invalid cookie$/ do |arg1|
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+ arg1
    @res = RestClient.get(url,{:accept => @format, :cookies => {:iPlanetDirectoryPro => "invalid"}}){|response, request, result| response }
end

Then /^I should see the authenticated object in the response body$/ do
    assert(@res != nil, "Response is nil")
    data = JSON.parse(@res.body)
    assert(data != nil, "Response body is nil")
    assert(data['authenticated'] != nil, "Response body does not contain 'authenticated'")
    assert(data['authenticated'] == true, "'authenticated' was not true")
end

Then /^I should see the non\-authenticated object in the response body$/ do
    assert(@res != nil, "Response is nil")
    data = JSON.parse(@res.body)
    assert(data != nil, "Response body is nil")
    assert(data['authenticated'] != nil, "Response body does not contain 'authenticated'")
    assert(data['authenticated'] == false, "'authenticated' was not false ")
end
