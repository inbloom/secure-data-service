require_relative '../../../utils/sli_utils.rb'
require 'open3'

Given /^I used the long lived session token generator script to create a token for user "(.*?)" with role "(.*?)" in tenant "(.*?)"  for realm "(.*?)" that will expire in "(.*?)" seconds with client_id "(.*?)"$/ do |user, role, tenant, realm, expiration_in_seconds, client_id|
  #Open3.capture2 seems to run a command from the directory the script was started in but we need to know exactly where
  #the generator script is so we get the directory of this test with File.dirname(__FILE__) and go up a lot to get to the script
  script_loc = File.dirname(__FILE__) + "/../../../../../../opstools/token-generator/generator.rb"
  puts("Currently in directory #{File.dirname(__FILE__)}")
  puts("The path is #{script_loc}")
  out, status = Open3.capture2("ruby #{script_loc} -e #{expiration_in_seconds} -c #{client_id} -u #{user} -r \"#{role}\" -t \"#{tenant}\" -R \"#{realm}\"")
  puts("The output of the script was #{out}")
  ls, ls_status = Open3.capture2("ls #{File.dirname(__FILE__)}/../../../../../../")
  puts("The contents of sli directory are #{ls}")
  # match = /token is (.*)/.match(out)
  # @sessionId = match[1]
  # puts("The generated token is #{@sessionId}") if $SLI_DEBUG
end

Then /^I should see that my role is "(.*?)"$/ do |role|
  restHttpGet("/system/session/check", "application/json")
  check = JSON.parse(@res.body)
  assert(check["sliRoles"].include?("Educator"), "Should be an Educator") 
end


Then /^I should receive (\d+) section$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end
