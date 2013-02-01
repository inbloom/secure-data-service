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


require_relative '../../utils/sli_utils.rb'

Given /^Another user has authenticated to SLI previously$/ do
  # This should be done as part of the Rake task config
end

When /^I access the API resource "([^"]*)" with no authorization headers present$/ do |arg1|
  url = PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url){|response, request, result| response }
end

Then /^I should see a message that I am forbidden$/ do
  assertWithWait("Could not find Not Authorized in page title")  {@driver.page_source.index("error")!= nil}
end

Then /^I should see a message that I am an invalid user$/ do
  assertWithWait("Could not find Invalid Username or password in page title")  {@driver.page_source.index("error")!= nil}
end

When /^I have a _tla cookie set to an expired session$/ do

    #Need to be at a page in the domain before we can set a cookie.  Should take us to a 404 page within the db
    url = PropLoader.getProps['databrowser_server_url']+"/sadfasdf/sadfadsfa"

    @driver.get(url)
    @driver.manage.add_cookie(:name => '_tla', :value => 'badbada5-9d81-8c1f-f91a-1fc23a1e6a79')
    @driver.manage.all_cookies.each { |cookie|
    puts "#{cookie[:name]} => #{cookie[:value]}"
}
end
