#!/usr/bin/ruby

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

# Clean up the bulk extraction zone and bulk extract database according to arguments.
# This is the implementation which uses the TenantCleaner class.

require_relative '../lib/TenantCleaner.rb'

def main()
  # Check the argumment signature.
  begin
    tenantCleaner = check_args(ARGV)
    if (tenantCleaner == nil)
      print_help()
      exit 0
    end
  rescue Exception => ex
    puts ex.message
    print_usage()
    exit 1
  end

  # Perform the actual bulk extract cleanup.
  tenantCleaner.clean()
  exit 0
end

def print_usage()
  puts "Usage:"
  puts $PROGRAM_NAME + " <tenant>"
  puts $PROGRAM_NAME + " <tenant> -d<date>"
  puts $PROGRAM_NAME + " <tenant> -e<edorg>"
  puts $PROGRAM_NAME + " <tenant> -e<edorg> -d<date>"
  puts $PROGRAM_NAME + " <tenant> -f<file>"
  puts $PROGRAM_NAME + " -h | -help"
end

def print_help()
  print_usage()
  puts
  puts "Use Cases:"
  puts $PROGRAM_NAME + " <tenant>:"
  puts "    Remove all bulk extract files and their database metadata for tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -d<date>"
  puts "    Remove all bulk extract files and their database metadata dated previous to"
  puts "    extraction date <date> for tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -e<edorg>"
  puts "    Remove all bulk extract files and their database metadata for"
  puts "    educational organization <edOrg> belonging to tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -e<edorg> -d<date>"
  puts "    Remove all bulk extract files and their database metadata dated previous to"
  puts "    extraction date <date> for educational organization <edOrg> belonging to tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -f<file_path>"
  puts "    Remove the particular bulk extract file <file> and its database metadata for tenant <tenant>."
  puts $PROGRAM_NAME + " -h | -help"
  puts "    Print this help page and exit."
  puts
  puts "Note:"
  puts "      <tenant> specifies tenant unique ID, e.g. \"Midgar\""
  puts "      <date> is in ISO8601 datetime format, e.g. \"2013-05-10T01:33:27.857Z\""
  puts "      <edOrg> specifies educational organization unique ID, e.g. \"IL-DAYBREAK\""
  puts "      <file> specifies extract file full directory pathname, e.g.\"/bulk/extract/tarfile.tar\""
end

def check_args(argv)
  # Identify non-use cases..
  if ((argv.length < 1) || (argv.length > 3))
    raise(ArgumentError, "Wrong number of arguments")
  elsif ((argv.length == 1) && (argv[0].eql?("-h") || argv[0].eql?("-help")))
    return nil
  end

  # Verify use case parameters.
  tenant = argv[0]
  edorg = nil
  date = nil
  file = nil
  argv[1..argv.length - 1].each do |param|
    case param[0..1]
      when "-e"
        if ((edorg != nil) || (file != nil) || (param.length < 3))
          raise(ArgumentError, "Illegal or wrongly included edorg argument")
        end
        edorg = param[2..param.length - 1]
      when "-d"
        if ((date != nil) || (file != nil) || (param.length < 3))
          raise(ArgumentError, "Illegal or wrongly included date argument")
        end
        date = param[2..param.length - 1]
      when "-f"
        if ((file != nil) || (edorg != nil) || (date != nil) || (param.length < 7))
          raise(ArgumentError, "Illegal or wrongly included file argument")
        end
        file = param[2..param.length - 1]
      else
        raise(ArgumentError, "Invalid argument(s)")
    end
  end
  return TenantCleaner.new(tenant, date, edorg, file)
end

# Run the main program here.
main()
