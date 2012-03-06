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
