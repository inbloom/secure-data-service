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

Given /^my contextual access is defined by the table:$/ do |table|
  # table is a Cucumber::Ast::Table
  @context={}
  table.hashes.each do |hash|
    @context[hash["Context"]]=hash["Ids"]
  end
end

When /^I make requests to both the versioned and un\-versioned URI "(.*?)"$/ do |uri|
  @res_unversion = restHttpGet(uri)
  @res_version = restHttpGet("/v1#{uri}")
end

Then /^both responses should be identical in return code and body$/ do
  # First validate both requests have responses
  assert(@res_version != nil, "Versioned response was Nil!")
  assert(@res_unversion != nil, "Versioned response was Nil!")

  #Then Check for same return code
  puts "Response code: #{@res_version.code}"
  assert(@res_unversion.code == @res_version.code, "Response Codes differed: Versioned Return Code:#{@res_version.code}, Unversioned Return Code:#{@res_unversion.code}")

  #Then check for same body (Headers will be different since the requested path is different)
  puts "Response body: #{@res_version.body}"
  assert(@res_version.body == @res_unversion.body, "Response Bodies differed: Versioned Body:#{@res_version.body}, Unversioned Body:#{@res_unversion.body}")
end

Then /^any future API request should result in a (\d+) response code$/ do |code|
  ["/system/session/debug","/v1/students","/v1/assessments"].each do |uri|
    restHttpGet(uri)
    assert(@res.code == code.to_i, "Response for #{uri} was #{@res.code} but expected #{code}")
  end
end

When /^I navigate to the base level URI <Entity> I should see the rewrite in the format of <URI>:$/ do |table|
  # table is a Cucumber::Ast::Table

  # Strings for ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"

  success = true
  table.hashes.each do |row|
    # Make the API Request
    restHttpGet("/v1#{row["Entity"]}")

    #Verify the return code
    if (@res.code != 200)
      success = false
      puts "#{startRed}Return code for URI: #{row["Entity"]} was #{@res.code}#{colorReset}"
    else
      puts "Return code for URI: #{row["Entity"]} was #{@res.code}"
    end

    #Verify the rewrite
    if (row["URI"].include? "@ids")
      row["URI"].match("/(.+)/@ids.*")
      context = $1
      row["URI"].gsub!("@ids", @context[context])
    end
    executedPath = @res.raw_headers.to_hash()["x-executedpath"][0]
    if (executedPath.include? row["URI"])
      puts "\e[32mRewrite #{row["Entity"]} -> #{executedPath}\e[0m"
    else
      success = false
      puts "#{startRed}Wrong rewrite: #{row["Entity"]} -> #{executedPath}#{colorReset}"
      puts "\e[32mExpected: #{row["URI"]}\e[0m"
    end
  end
  assert(success, "Rewrite Expectations failed, see above logs for specific failure(s)")
end

When /^I navigate to the the URI <Path> I should be denied:$/ do |table|
  # table is a Cucumber::Ast::Table

  # Strings for ANSI Color codes
  startRed = "\e[31m"
  colorReset = "\e[0m"

  success = true
  table.hashes.each do |row|
    # Make the API Request
    restHttpGet("/v1#{row["Path"]}")

    #Verify the return code
    if (@res.code != 403)
      success = false
      puts "#{startRed}Return code for URI: #{row["Path"]} was #{@res.code}#{colorReset}"
    else
      puts "Return code for URI: #{row["Path"]} was #{@res.code}"
    end
  end
  assert(success, "Blacklisting Expectations failed, see above logs for specific failure(s)")
end