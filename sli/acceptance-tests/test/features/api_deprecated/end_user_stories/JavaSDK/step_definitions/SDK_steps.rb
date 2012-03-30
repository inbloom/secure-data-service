require 'json'
require 'builder'
require 'aruba/cucumber'
require_relative '../../../../utils/sli_utils.rb'

Transform /^<([^"]*)>$/ do |step_arg|
  id = "linda.kim"  if step_arg == "<Username>"
  id = "linda.kim1234" if step_arg == "<Password>"    
  id
end

Given /^I am a valid SEA\/LEA end user "([^\"]*)" with password "([^\"]*)"$/ do |arg1,arg2|
  @user = arg1;
  @pwd = arg2;
end

Given /^I am authenticated with the "([^\"]*)" Realm$/ do |arg1|
  @realm = arg1;
end

@announce
When /^I run the "API SLI SDK Sample Application" interactively$/ do

  Dir.chdir("../SDK/target")
  wd = Dir.pwd
  
  print ("java -jar " + wd + "/SDK.jar -u " + @user + " -h " + PropLoader.getProps['api_server_url'] + " -r\"" + @realm + "\"")
  run_interactive(unescape("java -jar " + wd + "/SDK.jar -u " + @user + " -h " + PropLoader.getProps['api_server_url'] + " -r\"" + @realm + "\""))
end

When /^I enter my password$/ do
  type(@pwd)
end
