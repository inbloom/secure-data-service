require "base64"

saml_data = '<samlp:Response xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"></samlp:Response>'
Given /^I am using "(.*?)" as my content type$/ do |type|
  @content_type = type
end

Given /^I make a post request to sso\/post$/ do
  url = Property['api_server_url']+"/api/rest/saml/sso/post"
  data = {"SAMLResponse" => Base64.encode64(saml_data)}
  @res = RestClient.post(url, data, {:content_type => "application/x-www-form-urlencoded", :accept => @content_type}){|response, request, result| response }
end

Then /^I get back a json\-formatted (\d+) error page$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result['code'] == 403, "Response needs to contain a 403 code")
end

Then /^I get back a html\-formatted (\d+) error page$/ do |arg1|
  assert(@res.body.include?("<html"), "Result must contain html tag")
end


