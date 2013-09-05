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
@path_to_test_map = {
    "sli/acceptance-tests/test/features/utils" => ["api", "odin-api", "admin", "databrowser", "dashboard"],
    "sli/acceptance-tests/test/features/api" => ["api", "odin-api"],
    "sli/acceptance-tests/test/features/simple_idp" => ["admin" , "databrowser"],
    "sli/acceptance-tests/test/features/ingestion" => ["ingestion"],
    "sli/acceptance-tests/test/features/admintools" => ["admin"],
    "sli/acceptance-tests/test/features/databrowser" => ["databrowser"],
    "sli/acceptance-tests/test/features/dash" => ["dashboard"],
    "sli/acceptance-tests/test/features/odin" => ["odin"],
    "sli/acceptance-tests/test/features/apiV1/contextual_roles" => ["contextual-role"],
    "sli/acceptance-tests/test/features/ingestion/features/ingestion_dashboardSadPath.feature" => ["ingestion", "dashboard"],
    "sli/acceptance-tests/test/features/ingestion/test_data/DashboardSadPath_IL_Daybreak" => ["ingestion", "dashboard"],
    "sli/acceptance-tests/test/features/ingestion/test_data/DashboardSadPath_IL_Sunset" => ["ingestion", "dashboard"],
    "sli/acceptance-tests/test/features/ingestion/test_data/DashboardSadPath_NY" => ["ingestion", "dashboard"],
    "sli/acceptance-tests/test/features/bulk_extract" => ["bulk-extract"],
    "sli/acceptance-tests/test/data/Midgar_data" => ["api", "odin-api" , "databrowser", "sdk"],
    "sli/acceptance-tests/test/data/Hyrule_data" => ["api", "odin-api" , "sdk"],
    "sli/acceptance-tests/test/data/unified_data" => ["dashboard", "sdk"],
    "sli/acceptance-tests/test/data/application_fixture.json" => ["api", "odin-api", "admin", "sdk"],
    "sli/acceptance-tests/test/data/realm_fixture.json" => ["api", "odin-api", "admin", "dashboard", "sdk"],
    "sli/acceptance-tests/test/data/oauth_authentication_tokens.json" => ["api", "odin-api"],
    "sli/acceptance-tests/suites/bulk-extract.rake" => ["bulk-extract"],
    "sli/acceptance-tests/suites/ingestion.rake" => ["ingestion"],
    "sli/acceptance-tests/suites/dashboard.rake" => ["dashboard"],
    "sli/api/" => ["api", "odin-api", "search-indexer", "admin", "sdk", "bulk-extract", "databrowser", "contextual-role", "dashboard"],
    "sli/simple-idp" => ["api", "odin-api", "admin", "sdk", "contextual-role", "dashboard"],
    "sli/SDK" => ["admin", "dashboard", "sdk"],
    "sli/data-access" => ["api", "odin-api", "ingestion", "bulk-extract", "contextual-role", "dashboard"],
    "sli/domain" => ["api", "odin-api", "ingestion", "bulk-extract", "dashboard"],
    "sli/bulk-extract" => ["bulk-extract"],
    "sli/ingestion/ingestion-core" => ["ingestion", "odin"],
    "sli/ingestion/ingestion-base" => ["ingestion", "odin"],
    "sli/ingestion/ingestion-validation" => ["ingestion"],
    "sli/ingestion/ingestion-service" => ["ingestion", "odin"],
    "sli/admin-tools" => ["admin"],
    "sli/dashboard" => ["dashboard"],
    "sli/databrowser" => ["databrowser"],
    "sli/search-indexer" => ["search-indexer", "dashboard"],
    "tools/odin" => ["odin", "odin-api"]
}

def which_tests_to_run(file)
  tests_to_run = []
  files = IO.readlines(file)
  puts "Changed files: #{files}"
  keys = @path_to_test_map.keys
  files.each do |changed_file|
    keys.each do |key|
      if changed_file =~ /^#{key}/
        tests_to_run.push @path_to_test_map[key]
        next
      end
    end
  end
  tests_to_run.flatten.uniq
end

def create_files_for_each_job(list)
  list.each do |entry|
    File.open("#{entry}.job", "w") {}
  end
end

if __FILE__ == $0
  unless ARGV.length == 1
    puts "Usage: prompt>ruby " + $0 + " file"
    puts "Example: ruby #{$0} changes.file"
    exit(1)
  end

  file = ARGV[0]
end

create_files_for_each_job(which_tests_to_run(file))
