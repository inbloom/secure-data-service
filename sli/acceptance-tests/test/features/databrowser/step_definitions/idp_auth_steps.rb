require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Given /^My API is running on HTTPS$/ do
  apiUrl = PropLoader.getProps['api_server_url']
  assert("https" == apiUrl[0,5] , "Your API must be running SSL (https://) to run this test")
end

