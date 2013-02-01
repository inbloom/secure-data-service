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

require "json"

directory         = Dir.pwd
user_sessions     = directory + "/oauth_authentication_tokens.json"
migrated_sessions = directory + "/oauth_authentication_tokens_migrated.json"

if ARGV.length == 0
  puts "Using default arguments:"
  puts "\tdirectory         = #{directory}"
  puts "\tuser_sessions     = #{user_sessions}"
  puts "\tmigrated_sessions = #{migrated_sessions}"
elsif ARGV.length == 3
  directory         = ARGV[0]
  user_sessions     = directory + "/" + ARGV[1]
  migrated_sessions = directory + "/" + ARGV[2]
else
  puts "Usage: #{$0} <directory> <user_sessions>"
  puts "If no argument is given, the default is:"
  puts "\tdirectory         = #{directory}"
  puts "\tuser_sessions     = #{user_sessions}"
  puts "\tmigrated_sessions = #{migrated_sessions}"
  exit(1)
end

user_sessions_file     = File.new(user_sessions)
migrated_sessions_file = File.new(migrated_sessions, "w")

while (line = user_sessions_file.gets)
  begin
    session = JSON.parse(line)
    realm = session["body"]["principal"]["realm"] unless session["body"].nil? or session["body"]["principal"].nil?
    session["body"]["realmId"] = realm
    migrated_sessions_file.puts session.to_json
  rescue JSON::ParserError => e
    puts "Problem with line: #{line}"
    next
  end
end

user_sessions_file.close
migrated_sessions_file.close

