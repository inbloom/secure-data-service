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

if (ARGV.size != 1)
  puts " How to use... "
  puts " ./schema-upversion-all.rb <XSD File>"
  exit
end

filename = ARGV[0]

xsd = File.read(filename)
upversioned_xsd = xsd.gsub(%r{<sli:schemaVersion>\d*</sli:schemaVersion>}, '<sli:schemaVersion>999999</sli:schemaVersion>')
File.open(filename, 'w') {|f| f.write(upversioned_xsd)}
