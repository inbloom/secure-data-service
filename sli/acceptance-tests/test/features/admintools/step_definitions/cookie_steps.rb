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

require "selenium-webdriver"
require_relative '../../utils/selenium_common.rb'

require "base64"

Then /^the decoded cookie "(.*?)" is encrypted and should not contain "(.*?)"$/ do |cookie_name, bad_cookie_content|
  decoded_cookie = Base64.decode64(@driver.manage.cookie_named(cookie_name)[:value])
  assert(!decoded_cookie.include?(bad_cookie_content), "cookie #{cookie_name} should not contain #{bad_cookie_content}: #{decoded_cookie}")
end
