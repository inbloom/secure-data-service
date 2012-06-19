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

When /^I have a _tla cookie set to an expired session$/ do
    url = PropLoader.getProps['api_server_url']+"/api/"
    #Need to be at a valid page in the domain before we can set a cookie
    @driver.get(url)
    @driver.manage.add_cookie(:name => '_tla', :value => 'badbada5-9d81-8c1f-f91a-1fc23a1e6a79')
    @driver.manage.all_cookies.each { |cookie|
    puts "#{cookie[:name]} => #{cookie[:value]}"
}
end
