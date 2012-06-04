require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^the testing device app key has been created$/ do
  @oauthClientId = "kGb34ZYaHc"
  @oauthClientSecret = "iqGeAxGugciVwwZtsMJy06inNKBk7gbRUjSw0AecqpnvBere"
  @oauthRedirectURI = "http://device"
end

When /^I navigate to the API authorization endpoint with my client ID$/ do
  @driver.get PropLoader.getProps['api_server_url'] + "/api/oauth/authorize?response_type=code&client_id=#{@oauthClientId}"
end

Then /^I should be redirected to the realm choosing page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

When /^I select "(.*?)" drop the dropdown and click go$/ do |arg1|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
  assertWithWait("Could not find the Go button")  { @driver.find_element(:id, "go") }
  @driver.find_element(:id, "go").click
end

Then /^I should receive a json response containing my authorization code$/ do  
  assertWithWait("Could not find text 'authorization_code' on page") {@driver.page_source.include?("authorization_code")}

  @oauthAuthCode = @driver.page_source.match(/"authorization_code":"([^"]*)"/)[0]
end

When /^I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI$/ do
  @driver.get PropLoader.getProps['api_server_url'] + "/api/oauth/token?response_type=code&client_id=#{@oauthClientId}" +
                   "&client_secret=#{@oauthClientSecret}&auth_code=#{@oauthAuthCode}&redirect_uri=#{@oauthRedirectURI}"
end

Then /^I should receive a json response containing my authorization token$/ do
  assertWithWait("Could not find text 'authorization_token' on page") {@driver.page_source.include?("authorization_token")}

  @sessionId = @driver.page_source.match(/"authorization_token":"([^"]*)"/)[0]
end

Then /^I should be able to use the token to make valid API calls$/ do
  restHttpGet("/system/session/check", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res != nil, "Response is nil")
  data = JSON.parse(@res.body)
  assert(data != nil, "Response body is nil")
  assert(data['authentication'] != nil, "Session debug context doesn't contain 'authentication'")
  assert(data['authentication']['authenticated'] == true, 
         "Session debug context 'authentication.authenticated' is not true")
end
