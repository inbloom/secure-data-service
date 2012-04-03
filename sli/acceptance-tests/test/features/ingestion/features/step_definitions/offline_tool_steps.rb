require 'rubygems'
require 'mongo'
require 'fileutils'

require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

INGESTION_SERVER_URL = PropLoader.getProps['ingestion_server_url']
INGESTION_MODE = PropLoader.getProps['ingestion_mode']

############################################################
# STEPS: GIVEN
############################################################

Given /^I am using default offline tool package$/ do
  #TODO
end

Given /^I post "([^"]*)" file as an input to offline validation tool$/ do |file_name|
  #TODO
end

############################################################
# STEPS: WHEN
############################################################

When /^ "([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end

############################################################
# STEPS: THEN
############################################################

Then /^I should see a log file in same directory$/ do
  #TODO
end

############################################################
# END
############################################################