=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


require 'selenium-webdriver'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'
require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'

$ingestion_job_success = false

Given /^I am using the data store "([^"]*)"$/ do |arg1|
  @local_file_store_path = File.dirname(__FILE__) + arg1
end

Then /^the ingestion job should be successful$/ do
  $ingestion_job_success = true
end

Given /^the ingestion job was successful$/ do
  assert($ingestion_job_success, "Previous required Ingestion job failed, aborting...")
end

When /^soemthing$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^Something$/ do
  pending # express the regexp above with the code you wish you had
end
