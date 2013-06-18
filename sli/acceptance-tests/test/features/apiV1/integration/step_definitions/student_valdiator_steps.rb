=begin

Copyright 2013-2014 inBloom, Inc. and its affiliates.

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

require_relative '../../../utils/sli_utils.rb'


When /^I make API calls for multiple IDs in accordance to the following table:$/ do |table|
  # table is a Cucumber::Ast::Table

  # Strings for ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"

  success = true
  table.hashes.each do |row|
    puts "====Starting Row for #{row["Path"]}====" #Seperator between requests

    # Make the good API Request
    restHttpGet("/v1#{row["Path"].gsub("@ids",row["GoodId"])}")

    #Verify the return code
    if (@res.code != 200 || !validate_id_presence(@res))
      success = false
      puts "#{startRed}Expected 200 Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Expected 200 Return code for URI: #{row["Path"]} was #{@res.code}"
    end

    # Make the bad API Request
    restHttpGet("/v1#{row["Path"].gsub("@ids",row["BadId"])}")

    #Verify the return code
    if (@res.code != 403)
      success = false
      puts "#{startRed}Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}"
    end


    # Make the mixed API Request
    restHttpGet("/v1#{row["Path"].gsub("@ids","#{row["GoodId"]},#{row["BadId"]}")}")

    #Verify the return code
    if (@res.code != 403)
      success = false
      puts "#{startRed}Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Expected 403 Return code for URI: #{row["Path"]} was #{@res.code}"
    end


    puts "====Ending Row for #{row["Path"]}====" #Seperator between requests
  end
  assert(success, "Blacklisting Expectations failed, see above logs for specific failure(s)")

end

When /^I request the Good ID, I should be allowed$/ do
  # Explainitory step, doesn't actually do what it says, it gets done as part of a previous step
end

When /^I request the Bad ID, I should be denied$/ do
  # Explainitory step, doesn't actually do what it says, it gets done as part of a previous step
end

When /^I request both IDs, I should be denied$/ do
  # Explainitory step, doesn't actually do what it says, it gets done as part of a previous step
end

def validate_id_presence(res)
  id = nil
  return false if res == nil
  data = JSON.parse(res.body)
  return false if data == nil
  if data.is_a? Array
    id = data[0]["id"]
  end
  if data.is_a? Hash
    id = data["id"]
  end
  return id != nil
end
