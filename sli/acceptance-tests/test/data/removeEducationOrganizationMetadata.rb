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

directory     = Dir.pwd
new_directory = directory + "/without_edorgs"

puts "*********   REMOVING EDUCATION ORGANIZATION INFORMATION FROM METADATA IN FIXTURE FILES   *********"
if ARGV.length == 0
  puts "Using default arguments:"
  puts "\tdirectory     = #{directory}"
  puts "\tnew_directory = #{new_directory}"
elsif ARGV.length == 2
  studentFileString                          = ARGV[0]
  studentSchoolAssociationString             = ARGV[1]
else
  puts "Usage: #{$0} <directory> <new_directory>"
  puts "If no argument is given, the default is:"
  puts "\tdirectory     = #{directory}"
  puts "\tnew_directory = #{new_directory}"
  exit(1)
end

Dir.foreach(directory) do |filename|
  if filename.end_with? ".json"
    original_path = directory + "/" + filename
    migrated_path = new_directory + "/" + filename
    Dir.mkdir(new_directory) unless Dir.exists?(new_directory)
    
    puts " [info] migration: #{original_path} --> #{migrated_path}"
    
    original_file = File.new(original_path)
    migrated_file = File.new(migrated_path, "w")
    
    while (line = original_file.gets)
      begin
        entity = JSON.parse(line)
        keys = entity.keys
        keys.each do |key|
          if key != "_id" and key != "body" and key != "type"
            if entity[key].kind_of? Hash
              entity[key].delete("edOrgs") unless entity[key].nil? or entity[key]["edOrgs"].nil?
            elsif entity[key].kind_of? Array
              entity[key].each do |element|
                element["metaData"].delete("edOrgs") unless element["metaData"].nil? or element["metaData"]["edOrgs"].nil?
              end
            end
          end
        end
        migrated_file.puts entity.to_json
      rescue JSON::ParserError => e
        puts "Problem with fixture file: #{filename} --> line is #{line}"
        next
      end
    end    
    
    original_file.close
    migrated_file.close
  end
end
