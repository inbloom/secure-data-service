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
require 'active_support/core_ext/hash/deep_merge'

if (ARGV.size != 3)
  puts " How to use... "
  puts " ./schema-migration-prep.rb <XSD File> <Src Migration Config File> <Dst Migration Config File>"
  exit
end


def main
	modify_complex_types_schema()
	merge_migration_config()
end


def modify_complex_types_schema
	schemaFilename = ARGV[0]
	xsd = File.read(schemaFilename)

	xsd = update_version_numbers(xsd)
	xsd = add_new_field(xsd)
	xsd = remove_field(xsd)
	xsd = rename_field(xsd)

	File.open(schemaFilename, 'w') {|f| f.write(xsd)}
end



def update_version_numbers(xsd)
	# replace existing version numbers with 5
	upversioned_xsd = xsd.gsub(%r{<sli:schemaVersion>\d*</sli:schemaVersion>}, '<sli:schemaVersion>5</sli:schemaVersion>')
	return upversioned_xsd
end


#TODO - refactor to just insert the fields necessary by reading the config file
def add_new_field(xsd)
	# add new field to staff
	insert_index = xsd.index("<xs:element name=\'staffUniqueStateId\' ")
	new_schema_field = "<xs:element name=\'favoriteSubject\' type=\'xs:string\'/>\n"
	xsd.insert(insert_index,new_schema_field)

  # add fields to student
  insert_index = xsd.index("<xs:element name=\'studentUniqueStateId\'")
  new_schema_field = "<xs:element name=\'favoriteColor\' type=\'xs:string\'/>\n"
	xsd.insert(insert_index,new_schema_field)
  new_schema_field = "<xs:element name=\'mascot\' type=\'xs:string\'/>\n"
	xsd.insert(insert_index,new_schema_field)
  new_schema_field = "<xs:element name=\'somefield\' type=\'xs:string\'/>\n"
	xsd.insert(insert_index,new_schema_field)

	return xsd
end


def remove_field(xsd)

	entity_index = xsd.index("<xs:element name=\'staffUniqueStateId\' ") 
	begin_index = xsd.index("<xs:element name=\'sex\'",entity_index)
	end_index = xsd.index("</xs:element>",begin_index) + "</xs:element>".length - 1

	xsd.slice!(begin_index..end_index)

	return xsd
end


def rename_field(xsd)

	entity_index = xsd.index("<xs:element name=\'staffUniqueStateId\' ")
	begin_index = xsd.index("\'name\'",entity_index)

	xsd[begin_index+1..begin_index+4] = "nameData"

	return xsd
end


def merge_migration_config()
	# Modify migration configuration

	src_migration_config_filename = ARGV[1]
	dst_migration_config_filename = ARGV[2]

	src_migration_config = File.read(src_migration_config_filename)
	dst_migration_config = File.read(dst_migration_config_filename)

	src_hash = JSON.parse src_migration_config
	dst_hash = JSON.parse dst_migration_config

	combined_hash = src_hash.deep_merge(dst_hash)
	
	new_dst_json = combined_hash.to_json

	File.open(dst_migration_config_filename, 'w') {|f| f.write(new_dst_json)}
end




# execute main 
main()


