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

#portal, which also imports dashboard step def
Dir["./test/features/liferay/step_definitions/*.rb"].each {|file| require file}

#admin tools
Dir["./test/features/admintools/step_definitions/*.rb"].each {|file| require file}

#databrowser
Dir["./test/features/databrowser/step_definitions/*.rb"].each {|file| require file}

#search
Dir["./test/features/search/step_definitions/*.rb"].each {|file| require file}

$client_id = nil
$client_secret = nil

Transform /^<([^>]*)>$/ do |human_readable_text|
 if human_readable_text == "CI_IDP_Redirect_URL"
   url = PropLoader.getProps["ci_idp_redirect_url"]
 end
 url
end

When /^I make my app an installed app$/ do
  @driver.find_element(:css, 'input[id="app_installed"]').click
end


Then /^my new apps client ID is present$/ do
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_id = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[1]').text
  puts "client_id: " + client_id
  assert(client_id != '', "Expected non empty client Id, got #{client_id}")
  assert(client_id != 'Pending', "Expected non 'Pending' client Id, got #{client_id}")
  $client_id = client_id
end

Then /^my new apps shared secret is present$/ do
  #@driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  client_secret = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[2]').text
  puts "client_secret: " + client_secret
  assert(client_secret != '', "Expected non empty shared secret, got #{client_secret}")
  assert(client_secret != 'Pending', "Expected non 'Pending' shared secret, got #{client_secret}")
  $client_secret = client_secret
end

Then /^I enable my app for all districts$/ do
  assertWithWait("Could not find Enable All link") {@driver.find_element(:link, "Enable All")}
  @driver.find_element(:link, "Enable All").click
end

Given /^the testing device app key has been created$/ do
  @oauthClientId = $client_id
  @oauthClientSecret = $client_secret
  @oauthRedirectURI = "http://device"
end

When /^I navigate to the API authorization endpoint with my client ID$/ do
  @driver.get PropLoader.getProps['api_server_url'] + "/api/oauth/authorize?response_type=code&client_id=#{@oauthClientId}"
end

Then /^I should receive a json response containing my authorization code$/ do  
  assertWithWait("Could not find text 'authorization_code' on page") {@driver.page_source.include?("authorization_code")}
  
  @oauthAuthCode = @driver.page_source.match(/"authorization_code":"(?<Code>[^"]*)"/)[:Code]
end

When /^I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI$/ do
  @driver.get PropLoader.getProps['api_server_url'] + "/api/oauth/token?response_type=code&client_id=#{@oauthClientId}" +
                   "&client_secret=#{@oauthClientSecret}&code=#{@oauthAuthCode}&redirect_uri=#{@oauthRedirectURI}"
end

Then /^I should receive a json response containing my authorization token$/ do
  assertWithWait("Could not find text 'authorization_token' on page") {@driver.page_source.include?("access_token")}

  @sessionId = @driver.page_source.match(/"access_token":"(?<Token>[^"]*)"/)[:Token]
end

Then /^I should be able to use the token to make valid API calls$/ do
  restHttpGet("/system/session/check", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res != nil, "Response is nil")
  data = JSON.parse(@res.body)
  assert(data != nil, "Response body is nil")
  assert(data['authenticated'] == true, 
         "Session debug context 'authentication.authenticated' is not true")
end

Then /^my current url is "(.*?)"$/ do |url|
  assertWithWait("Not in expected URL") {@driver.current_url == url}
end

Then /^I enter "(.*?)" in the IDP URL field$/ do |url|  
  @driver.find_element(:name, 'realm[idp][id]').send_keys url
end

Then /^I enter "(.*?)" in the Redirect Endpoint field$/ do |url|
  @driver.find_element(:name, 'realm[idp][redirectEndpoint]').send_keys url
end
