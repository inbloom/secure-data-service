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

require 'date'
require 'json'
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

Then /^I expire a current student section association$/ do
  assert(@res.body, "Response body should not be nil.")
  associations = JSON.parse(@res.body)
  assert(associations.size > 0, "There are no associations to expire.")
  first_association = associations.first
  assert(first_association['endDate'].nil? || first_association['endDate'] >= Date.new, "First association encountered was not current.")
  @expired = first_association['id']
  # perform a PUT that sets the association endDate to be in the past
  first_association.delete('links')
  first_association['endDate'] = Date.today - 30
  puts "association: #{prepareData(@format, first_association)}"
  restHttpPut("/v1/studentSectionAssociations/#{@expired}", prepareData(@format, first_association))
  assert(@res != nil, "Response from rest-client PUT is nil")
  assert(@res.code == 204, "Response code indicates that PUT operation failed: #{@res.code}")
end

And /^the expired student section association is not in the response$/ do
  assert(@res.body, "Response body should not be nil.")
  associations = JSON.parse(@res.body)
  assert(associations.size > 0, "There are no associations to expire.")
  assert(associations.any? { |association| association['id'] == @expired } == false, "The expired association should not be present in the response body.")
end
