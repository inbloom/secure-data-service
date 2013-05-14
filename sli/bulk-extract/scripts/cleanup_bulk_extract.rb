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

require 'yaml'
require 'logger'

CURRENT_ABSOLUTE_PARENT_DIRNAME = File.expand_path('..', File.absolute_path(File.dirname($PROGRAM_NAME)))
require_relative CURRENT_ABSOLUTE_PARENT_DIRNAME + '/lib/TenantCleaner.rb'

LogLevels = {"UNKNOWN" => Logger::UNKNOWN, "FATAL" => Logger::FATAL, "ERROR" => Logger::ERROR, "WARN" => Logger::WARN, \
             "INFO" => Logger::INFO, "DEBUG" => Logger::DEBUG}

CONFIGURATION_FILE = CURRENT_ABSOLUTE_PARENT_DIRNAME + "/config/bulk_extract_cleanup.yml"

# Get configuration properties.
PROPERTIES = YAML::load_file(CURRENT_ABSOLUTE_PARENT_DIRNAME + '/config/bulk_extract_cleanup.yml')

# Logger properties 
LOG_FILE_PATHNAME = PROPERTIES['log_file_pathname']
LOG_FILE_ROTATION = PROPERTIES['log_file_rotation']
LOG_LEVEL = LogLevels[PROPERTIES['log_level']]

# TenantCleaner properties.
DATABASE_NAME = PROPERTIES['sli_database_name']
DATABASE_HOST = PROPERTIES['bulk_extract_host']
DATABASE_PORT = PROPERTIES['bulk_extract_port']
REMOVE_DB_RECORD_RETRIES = PROPERTIES['remove_db_record_retries']
REMOVE_DB_RECORD_RETRY_SECS = PROPERTIES['remove_db_record_retry_interval_secs']

# Create logger.
begin
  LOGGER = Logger.new(LOG_FILE_PATHNAME, LOG_FILE_ROTATION)
  LOGGER.level = LOG_LEVEL
rescue Exception => ex
  puts "FATAL: Cannot create log file " + LOG_FILE_PATHNAME + ": " + ex.message
  exit 1
end

def main()
  begin
    # Initialize the log file.
    puts "Writing output to log file " + LOG_FILE_PATHNAME
    LOGGER.info ""
    LOGGER.info "--------------------------------------------------"
    LOGGER.info ""
    LOGGER.info $PROGRAM_NAME + " " + ARGV.join(" ")

    # Check the argumment signature.
    tenantCleaner = check_args(ARGV)
    if (tenantCleaner != nil)
      # Perform the actual bulk extract cleanup.
      tenantCleaner.clean()
    end
  rescue Exception => ex
    puts "FATAL: " + ex.message
    (LOGGER.fatal ex.message) if (LOGGER != nil)
    print_usage() if (ex.class == ArgumentError)
    exit 1
  end
  exit 0
end

def print_usage()
  puts "Usage:"
  puts $PROGRAM_NAME + " -t<tenant>"
  puts $PROGRAM_NAME + " -t<tenant> -d<date>"
  puts $PROGRAM_NAME + " -t<tenant> -e<edorg>"
  puts $PROGRAM_NAME + " -t<tenant> -e<edorg> -d<date>"
  puts $PROGRAM_NAME + " -t<tenant> -f<file>"
  puts $PROGRAM_NAME + " -h | -help"
end

def print_help()
  print_usage()
  puts
  puts "Use Cases:"
  puts $PROGRAM_NAME + " -t<tenant>"
  puts "    Remove all bulk extract files and their database metadata for tenant <tenant>"
  puts $PROGRAM_NAME + " -t<tenant> -d<date>"
  puts "    Remove all bulk extract files and their database metadata dated previous to"
  puts "    extraction date <date> for tenant <tenant>"
  puts $PROGRAM_NAME + " -t<tenant> -e<edorg>"
  puts "    Remove all bulk extract files and their database metadata for"
  puts "    educational organization <edOrg> belonging to tenant <tenant>"
  puts $PROGRAM_NAME + " -t<tenant> -e<edorg> -d<date>"
  puts "    Remove all bulk extract files and their database metadata dated previous to"
  puts "    extraction date <date> for educational organization <edOrg> belonging to tenant <tenant>"
  puts $PROGRAM_NAME + " -t<tenant> -f<file_path>"
  puts "    Remove the particular bulk extract file <file> and its database metadata for tenant <tenant>"
  puts $PROGRAM_NAME + " -h | -help"
  puts "    Print this help page and exit"
  puts
  puts "Note:"
  puts "      <tenant> specifies tenant unique ID, e.g. Midgar"
  puts "      <date> is in UTC or ISO8601 datetime format (YYYY-MM-DD[Thh[:mm[:ss[.s[s[s]]]]]][Z|+/-hh:hh]),\n" + \
       "             e.g. 2013-05-10T01:33:27.857Z, 2013-05-10T01:33:27-04:00, 2013-05-10T01:33:27,\n" + \
       "             2013-05-10T01:33, 2013-05-10, \"Sun May 12 12:07:22 EDT 2013\", etc.\n" + \
       "             Time without at least 'hh:mmZ' suffix is local time, e.g. 2013-05-10T00:33:27 (EST)\n" + \
       "             becomes 2013-05-10T00:33:27-05:00, or 2013-05-10T05:33:27Z (GMT)"
  puts "      <edOrg> specifies educational organization state unique ID, e.g. IL-DAYBREAK"
  puts "      <file> specifies extract file full directory pathname, e.g. /bulk/extract/tarfile.tar"
  puts "      Any parameter containing whitespace must be quoted, e.g. -e\"Sunset Central High School\""
end

def check_args(argv)
  # Identify non-use cases..
  if ((argv.length < 1) || (argv.length > 3))
    raise(ArgumentError, "Wrong number of arguments")
  elsif ((argv.length == 1) && (argv[0].eql?("-h") || argv[0].eql?("-help")))
    print_help()
    return nil
  end

  # Verify use case parameters.
  tenant = nil
  edorg = nil
  date = nil
  file = nil
  argv[0..argv.length - 1].each do |param|
    case param[0..1]
      when "-t"
        if (tenant != nil)
          raise(ArgumentError, "Illegal or wrongly included tenant argument")
        end
        tenant = param[2..param.length - 1]
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
        if ((file != nil) || (edorg != nil) || (date != nil) || (param.length < 3))
          raise(ArgumentError, "Illegal or wrongly included file argument")
        end
        file = param[2..param.length - 1]
      else
        raise(ArgumentError, "Invalid argument(s)")
    end
  end
  if (tenant == nil)
    raise(ArgumentError, "Tenant not specified")
  end
  return TenantCleaner.new(tenant, date, edorg, file, LOGGER, DATABASE_HOST, DATABASE_PORT, DATABASE_NAME, \
                           REMOVE_DB_RECORD_RETRIES, REMOVE_DB_RECORD_RETRY_SECS)
end

# Run the main program here.
main()

at_exit do
  LOGGER.close if (LOGGER != nil)
end
