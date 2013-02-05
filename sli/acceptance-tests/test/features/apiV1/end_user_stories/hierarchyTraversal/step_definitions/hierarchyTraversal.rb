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


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'


Transform /^<([^>]*)>$/ do |human_readable_id|
  guid = "b1bd3db6-d020-4651-b1b8-a8dba688d9e1" if human_readable_id == "'STATE EDUCATION ORGANIZATION' ID"
  guid = "bd086bae-ee82-4cf2-baf9-221a9407ea07" if human_readable_id == "'LOCAL EDUCATION ORGANIZATION' ID"
  guid = "92d6d5a0-852c-45f4-907a-912752831772" if human_readable_id == "'SCHOOL' ID"
  guid = "87770f3e-93c6-11e1-adcc-101f74582c4c" if human_readable_id == "'SCHOOL-SESSION-ASSOCIATION' ID"
  guid = "c549e272-9a7b-4c02-aff7-b105ed76c904" if human_readable_id == "'SESSION' ID"
  guid = "88ddb0c4-1787-4ed8-884e-96aa774e6d42" if human_readable_id == "'SESSION-COURSE-ASSOCIATION' ID"
  guid = "5841cf31-16a6-4b4d-abe1-3909d86b4fc3" if human_readable_id == "'COURSE' ID"
  guid = "15ab6363-5509-470c-8b59-4f289c224107_id" if human_readable_id == "'SECTION' ID"
  guid = "15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id" if human_readable_id == "'TEACHER-SECTION-ASSOCIATION' ID"
  guid = "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b" if human_readable_id == "'TEACHER' ID"
  guid = "15ab6363-5509-470c-8b59-4f289c224107_id76ac366b-ee0d-4db9-b820-ac5bc83e53ac_id" if human_readable_id == "'STUDENT-SECTION' ID"
  guid = "0f0d9bac-0081-4900-af7c-d17915e02378_id" if human_readable_id == "'STUDENT' ID"
  guid
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/path
Transform /^(\/[\w-]+\/)(<.+>)(\/[\w-]+)$/ do |uri, template, uri2|
  uri + Transform(template) + uri2
end


# Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  # @result[key].should_not == nil
  # assert(@result[key].to_s == value.to_s, "Expected value \"#{value}\" != \"#{@result[key]}\"")
# end

Then /^a "([^"]*)" "([^"]*)" should be "([^"]*)"$/ do |subdocumentName, key, value|
  assert(@result[subdocumentName] != nil, "No #{subdocumentName} present in response")
  found = false
  @result[subdocumentName].each do |subdocument|
    if subdocument[key] == value
       found = true
    end
  end
  assert(found, "#{value} not found in results")
end

Then /^a "([^"]*)" should be "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  found = false;
  @result[key].each do |value_from_array|
    found = (value==value_from_array) ? true:found;
  end
  assert(found, "Expected value #{value} not found in array")
end

Then /^the "([^"]*)" should be "([^"]*)"$/ do |key, value|
  @result[key].should_not == nil
  found = false;
  @result[key].each do |value_from_array|
    found = (value==value_from_array) ? true:found;
  end
  assert(found, "Expected value #{value} not found in array")
end

Transform /^(\/[\w-].+\/)([\w-]+\/)(<.+>)$/ do |version, uri, template|
  version + uri + Transform(template)
end

Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/+[\w-]+)$/ do |version, uri, template, uri2|
  version + uri + Transform(template) + uri2
end

Transform /^(\/[\w-]+\/)([\w-]+\??[=\w-]*)(<.+>)$/ do |version, uri, template|
  version + uri + Transform(template)
end
When /^I navigate to GET the "([^"]*)" link named "([^"]*)"$/ do |arg1, arg2|
  puts @the_link.count
  case arg1
  when /fifth/
    @the_link = @the_link[4]
  when /fourth/
    @the_link = @the_link[3]
  when /third/
    @the_link = @the_link[2]
  when /second/
    @the_link = @the_link[1]
  when /first/
    @the_link = @the_link[0]
  end
  restHttpGetAbs(@the_link)
  #@result = JSON.parse(@res.body)
end

Then /^I should receive a collection link named "([^"]*)"$/ do |arg1|
  step "in an entity, I should receive a link named \"#{arg1}\""
end

Then /^I should receive (\d+) entities$/ do |arg1|
  if arg1.to_i == 0
    assert(@result.empty?, "Expected zero entities, received #{@result.size}")
  else
    assert(@result.size == arg1.to_i, "Expected #{arg1} entities, recieved #{@result.size()}")
  end
end

And /^I should receive zero entities$/ do
  step "I should receive 0 entities"
end

When /^I navigate to GET the link named "(.*?)" with "(.*?)" of "(.*?)"$/ do |linkName, key, value|
  @id_link.each do |link|
    if link[key]==value
      restHttpGetAbs(link["link"])
      break
    end
  end
end

