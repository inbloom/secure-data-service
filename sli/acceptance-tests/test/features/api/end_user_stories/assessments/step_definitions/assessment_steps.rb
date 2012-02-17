require 'rest-client'
require_relative '../../../../utils/sli_utils.rb'

Transform /(\/[^"].*\/)<(.+)>\/targets$/ do |arg1, arg2|
  id = arg1+"e24b24aa-2556-994b-d1ed-6e6f71d1be97/targets" if arg2 == "'Ms. Smith' ID"
  id = arg1+"58c9ef19-c172-4798-8e6e-c73e68ffb5a3/targets" if arg2 == "'Algebra II' ID"
  id = arg1+"12f25c0f-75d7-4e45-8f36-af1bcc342871/targets" if arg2 == "'Teacher Ms. Jones and Section Algebra II' ID"
  id = arg1+"4efb3a11-bc49-f388-0000-0000c93556fb/targets" if arg2 == "'Jane Doe' ID" 
  id = arg1+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6/targets" if arg2 == "'Grade 2 BOY DIBELS' ID"
  id
end

Transform /(\/[^"].*\/)<(.+)>$/ do |arg1, arg2|
  id = arg1+"e24b24aa-2556-994b-d1ed-6e6f71d1be97" if arg2 == "'Ms. Smith' ID"
  id = arg1+"58c9ef19-c172-4798-8e6e-c73e68ffb5a3" if arg2 == "'Algebra II' ID"
  id = arg1+"12f25c0f-75d7-4e45-8f36-af1bcc342871" if arg2 == "'Teacher Ms. Jones and Section Algebra II' ID"
  id = arg1+"4efb3a11-bc49-f388-0000-0000c93556fb" if arg2 == "'Jane Doe' ID" 
  id = arg1+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" if arg2 == "'Grade 2 BOY DIBELS' ID"
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "'Apple Alternative Elementary School' ID"
  id
end

Given /^I am a valid SEA\/LEA end user <username> with password <password>$/ do
    @user = "aggregator"
    @passwd = "aggregator1234"
end

Given /^I have a Role attribute returned from the "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^the role attribute equals "([^"]*)"$/ do |arg1|
  # No code needed, this is done during the IDP configuration
end

Given /^I am authenticated on "([^"]*)"$/ do |arg1|  
  idpRealmLogin(@user, @passwd, arg1)
  assert(@sessionId != nil, "Session returned was nil")
end
