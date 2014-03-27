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

require_relative '../../utils/api_utils.rb'

Then /^all returned links should be version "(.*?)"$/ do |expected_version|
  link_substring = "/api/rest/#{expected_version}/"
  @result['links'].each do |link|
    link['href'].should include(link_substring)
  end
end
