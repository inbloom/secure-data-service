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

require 'rake'
require 'socket'
require 'timeout'
require 'set'
require_relative 'test/features/utils/rakefile_common.rb'

if ARGV.include? "?" or ARGV.include? "-h" or ARGV.include? "--help"
  puts "Usage:"
  puts "  > bundle exec ruby #{$0} [rake-task options]"
  puts "  Unstaged changes will be stashed and popped automatically."
  exit 0
end

def init
  ARGV.each do |arg|
    pair = arg.split "="
    ENV[pair[0]] = pair[1]
  end
  if ENV['api_server_url'].nil? or ENV['api_server_url'].match(/localhost/) or ENV['api_server_url'].match(/local\.slidev\.org/)
    @local = true
    if !is_port_open?
      puts "Please stop API and re-run this script."
      exit 1
    end
  else
    @local = false
  end
  @sli_workspace = "#{`git rev-parse --show-toplevel`.chomp}/sli"
  # Stashing all current changes before migration
  system "git stash"
  Dir.chdir "#{@sli_workspace}/config/scripts"
  system "sh resetAllDbs.sh"
  Dir.chdir "#{@sli_workspace}/acceptance-tests"
  Rake.load_rakefile "suites/versioning.rake"
end

def is_port_open?(host = "localhost", port = "8080")
  begin
    t = TCPSocket.new host, port
    t.close
    return false
  rescue Exception => e
    @exception = e
    return true
  end
end

def run_test(other_tags)
  ENV['OTHER_TAGS'] = other_tags
  Dir.chdir "#{@sli_workspace}/acceptance-tests"
  Rake.load_rakefile "Rakefile"
  task = Rake::Task['versioningTests']
  # Rake::Task.execute does not run prerequisites
  task.prerequisites.each do |pre|
    p = Rake::Task[pre]
    p.invoke
    p.reenable
  end
  task.execute
end

def start_api
  if @local
    puts "Starting API"
    raise Exception.new "Port 8080 is not open. Stop API and re-run this script." if !is_port_open?
    @api_log = "#{@sli_workspace}/acceptance-tests/target/db_migration_api.log"
    @api_pid = `mvn jetty:run -f #{@sli_workspace}/api/pom.xml > #{@api_log} 2>&1 & echo $!`
    begin
      pattern = /Starting scanner at interval of 5 seconds/
      Timeout::timeout(60) {
        while true do
          text = File.read @api_log
          return true if text =~ pattern
          print "."
          $stdout.flush
          sleep 1
        end
      }
    rescue Timeout::Error
      raise Exception.new "API fails to start in 60 seconds."
    end
  end
end

def stop_api
  if @api_pid
    puts "Stopping API"
    `kill #{@api_pid}`
  end
end

def build_api
  system "mvn -pl api -am -ff clean install -DskipTests=true -f #{@sli_workspace}/pom.xml" if @local
end

def run_migration_prep
  Dir.chdir "#{@sli_workspace}/acceptance-tests/test/features/utils"
  system "ruby schema-migration-prep.rb ../../../../domain/src/main/resources/sliXsd/ComplexTypes.xsd ../../../../data-access/dal/src/test/resources/migration/acc-test-migration-config.json ../../../../data-access/dal/src/main/resources/migration/migration-config.json"
  @files_modified = true
end

def revert_modified_files
  if @files_modified
    puts "ComplexTypes.xsd and migration-config.json are modified. Running git checkout and git stash pop."
    Dir.chdir "#{@sli_workspace}"
    system "git checkout --  domain/src/main/resources/sliXsd/ComplexTypes.xsd data-access/dal/src/main/resources/migration/migration-config.json"
    system "git stash pop"
  end
end

begin
  init
  build_api
  run_test "@DB_MIGRATION_BEFORE_API_STARTS"
  start_api
  run_test "@DB_MIGRATION_AFTER_API_STARTS"
  stop_api
  run_migration_prep
  build_api
  start_api
  run_test "@DB_MIGRATION_AFTER_UPVERSIONING"
rescue Exception => e
  puts e.message
ensure
  puts "API log: #{@api_log}"
  stop_api
  revert_modified_files
end
