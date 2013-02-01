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


#A script that goes through directories of fixture files and verifies that each ID is unique

def checkFile(file)
  fileContents = File.read(file)
  found = fileContents.scan(/"\$binary" *: *"([^"]*)"/)
  found.each do |id|
    if !$ids.member?(id)
      $ids[id] = file
    else
      puts("ERROR: The ID #{id} exists in files #{file} and #{$ids[id]}")
    end
  end 
end

$ids = {}
ARGV.each do |dir|
  Dir.glob(dir + "*.json") do |file|
    checkFile(file)
  end
end

