=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require 'json'

if (ARGV.size != 3)
  puts " How to use... "
  puts " ./schema-migration-prep.rb <XSD File> <Src Migration Config File> <Dst Migration Config File>"
  exit
end

# Modify schema

schemaFilename = ARGV[0]

xsd = File.read(schemaFilename)
upversioned_xsd = xsd.gsub(%r{<sli:schemaVersion>\d*</sli:schemaVersion>}, '<sli:schemaVersion>999999</sli:schemaVersion>')

insert_index = upversioned_xsd.index("<xs:element name=\"studentUniqueStateId\"")

new_schema_field = "<xs:element name=\"favoriteFood\" type=\"xs:string\"/>\n"
upversioned_xsd.insert(insert_index,new_schema_field)


File.open(schemaFilename, 'w') {|f| f.write(upversioned_xsd)}



# Modify migration configuration

src_migration_config_filename = ARGV[1]
dst_migration_config_filename = ARGV[2]

src_migration_config = File.read(src_migration_config_filename)
dst_migration_config = File.read(dst_migration_config_filename)

src_hash = JSON.parse src_migration_config
dst_hash = JSON.parse dst_migration_config

ENTITIES = "entities"

src_hash[ENTITIES].each_pair do |entity_name,version_update|

	if(dst_hash[ENTITIES].nil?)
		dst_hash[ENTITIES] = Hash.new
	end

	if (!dst_hash[ENTITIES].has_key? (entity_name))
		dst_hash[ENTITIES][entity_name] = Hash.new
	end

	version_update.each_pair do |version_number,migration|
		dst_hash[ENTITIES][entity_name][version_number] = migration
	end
end

new_dst_json = dst_hash.to_json


File.open(dst_migration_config_filename, 'w') {|f| f.write(new_dst_json)}














