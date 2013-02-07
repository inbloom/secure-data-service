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

require 'optparse'
require 'zip/zip'
require 'fileutils'
require 'timeout'
require 'json'

@options = {}
ARGV.options do |opts|
  opts.banner = "Usage:\n  > ruby #{$0} -b <old_branch> [-v <old_version>] [-B <new_branch>] [-e <env_vars>] [-c] [-h]"
  opts.on(:REQUIRED, /.+/, '-b', '--old_branch (required)', 'The branch to extract the test code from (prior version).') do |old_branch|
    @options[:old_branch] = old_branch
  end
  opts.on(:REQUIRED, /.+/, '-v', '--old_version', 'A specific (older) API version to use in test. Example: v1.0. Use tests\' default if omitted.') do |old_version|
    @options[:old_version] = old_version
  end
  opts.on(:REQUIRED, /.+/, '-B', '--new_branch', 'The branch to run the extracted test code on. Use current branch if omitted.') do |new_branch|
    @options[:new_branch] = new_branch
  end
  opts.on(:REQUIRED, /.+/, '-e', '--env_var', 'Run the tests with these comma-separated environment variables. Defaults: TOGGLE_TABLESCANS,FORCE_COLOR.') do |env_var|
    @options[:env_var] = env_var
  end
  # opts.on(:REQUIRED, /.+/, '-t', '--task', Array, 'Comma-separated list of Rake tasks to execute. Use apiAndSecurityTests if omitted.') do |tasks|
  #   @options[:tasks] = tasks
  # end
  opts.on(:OPTIONAL, '-c', '--ci', 'Provide this option if running on CI.') do |ci|
    @options[:ci] = ci
  end
  opts.on_tail(:NONE, '-h', '--help', "Display this screen.") do |arg|
    exit 1
  end
  begin 
    opts.parse!
    raise Exception.new unless @options.include? :old_branch
  rescue Exception => e
    puts opts
    exit 1
  end
end

def init
  @sli_workspace = "#{`git rev-parse --show-toplevel`.chomp}/sli"
  @extract_dest = "#{@sli_workspace}/test-bundle-extract/"
  @api_log = "#{@sli_workspace}/acceptance-tests/target/api_version_upgrade_test.log"
  @dirs_to_archive = ["acceptance-tests",
                      "opstools",
                      "admin-tools/approval",
                      "ingestion/ingestion-validation/target/",
                      "api/src/main/resources/wadl/",
                      "ingestion/ingestion-core/src/main/resources/tenants/"]
  @old_version = @options[:old_version]
  @old_branch = @options[:old_branch]
  @new_branch = @options[:new_branch]
  @env_var = @options[:env_var] ? @options[:env_var].split(",") : ["TOGGLE_TABLESCANS", "FORCE_COLOR"]
  @env_var.map! {|e| "#{e}=1"}
  # @rake_tasks = @options[:tasks] ? @options[:tasks].join(" ") : "apiAndSecurityTests"
  @rake_tasks = "apiV1EntityTests"
  @ci = @options[:ci]

  scripts_loc = "#{@sli_workspace}/opstools/migration"
  @migrations = [
      "bundle exec ruby #{scripts_loc}/67to68TeacherSchoolDenormalizer.rb",
      "bundle exec ruby #{scripts_loc}/69WritePublicRoleMigration.rb localhost:27017",
      "bundle exec ruby #{@sli_workspace}/acceptance-tests/api-versions/fix_staffEdOrg_type.rb 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a",
      "bundle exec ruby #{@sli_workspace}/acceptance-tests/api-versions/fix_staffEdOrg_type.rb f25ce1b8a399bd8621a57427a20039b4b13935db"
  ]
end

def run_cmd(cmd)
  puts "> #{cmd}"
  raise Exception.new "'#{cmd}' failed." unless system cmd
end

def checkout(branch)
  puts "---- Checkout branch #{branch}"
  cmd = "git checkout #{branch}"
  run_cmd cmd
end

def package_test_code(branch)
  puts "---- Package test code"
  run_cmd "git fetch"
  dirs = @dirs_to_archive.join(" ")
  @test_code_package = "#{@sli_workspace}/testBundle_#{Time.new.strftime("%m%d%Y_%H%M%S")}_#{branch}.zip"
  Dir.chdir(@sli_workspace)
  cmd = "git archive --format=zip origin/#{branch} #{dirs} > #{@test_code_package}"
  run_cmd cmd
end

def build_sli
  puts "---- Build SLI"
  cmd = "mvn clean package install -DskipTests -f #{@sli_workspace}/pom.xml"
  run_cmd cmd
end

def unpackage_test_code(dest)
  puts "---- Unpackage test code"
  if File.file? @test_code_package
    FileUtils.rm_rf dest if File.directory? dest
    Zip::ZipFile.open(@test_code_package) do |zip_file|
      zip_file.each do |f|
        f_path = File.join(dest, f.name)
        FileUtils.mkdir_p(File.dirname(f_path))
        zip_file.extract(f, f_path)
      end
    end
  else
    raise Exception.new "Cannot find test code package"
  end
end

def replace_api_version(version)
  puts "---- Replace API versions in URLs with \"#{version}\" in test code"
  Dir[File.join("#{@extract_dest}/acceptance-tests/test/features", "**", "*.{rb,feature}")].each do |f|
    temp_text = File.read(f).gsub(/v\d\.?\d*(?!_resources)/, version)
    text = ""
    replace_next_line_for_search = false
    temp_text.each_line do |line|
      if replace_next_line_for_search
        line += "  version = (expectedUri.include? \"/search\") ? \"v1.1\" : \"#{version}\"\n"
        replace_next_line_for_search = false
      end
      replace_next_line_for_search = true if line.include? "Then /^uri was rewritten to"
      # Fix bad transform regex for to match api minor versions
      line.gsub!(/\[\\w-\]/, "[^\\\"]") if line.match(/Transform.*\[\\w-\]+.*version/)
      text += "#{line}"
    end
    File.open(f, "w") do |f|
      f.write text
    end
  end
end

def start_api
  puts "---- Start API"
  cmd = "mvn jetty:run -f #{@sli_workspace}/api/pom.xml > #{@api_log} 2>&1 & echo $!"
  puts cmd
  @api_pid = `#{cmd}`
  puts "---- Wait for API to start"
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

def insert_migration_scripts
  puts "---- Inserting migration scripts in Rakefile"
  Dir.chdir "#{@sli_workspace}/acceptance-tests"
  run_cmd "bundle exec rake loadDefaultIngestionTenants"
  gemfile_lock = "#{@extract_dest}/acceptance-tests/Gemfile.lock"
  mongo_gem_version = File.read(gemfile_lock).match(/mongo \((.*)\)/)[1]
  if mongo_gem_version.to_f < "1.8.0".to_f
    # Migration scripts require 1.8.0 or above
    text = File.read(gemfile_lock).gsub(/mongo .+/, "mongo (1.8.0)")
    File.open(gemfile_lock, "w") do |f|
      f.write text
    end
  end
  text_to_insert = @migrations.map { |m| "sh \"#{m}\"" }.join "\n"
  rakefile = "#{@extract_dest}/acceptance-tests/Rakefile"
  text = File.read(rakefile).gsub(/def runTests\(testDirPath\)/, "def runTests(testDirPath)\n#{text_to_insert}")
  File.open(rakefile, "w") do |f|
    f.write text
  end
end

def update_feature_files
  puts "---- Updating feature files according to config"
  config = JSON.parse(File.read("#{@sli_workspace}/acceptance-tests/api-versions/feature_changes.json"))
  f_changes = config[@old_branch]
  f_changes.each do |f_change|
    file = "#{@extract_dest}/acceptance-tests/test/features/#{f_change["feature"]}"
    text = File.read(file)
    f_change["changes"].each do |change|
      text.gsub!(change["old"], change["new"])
    end
    File.open(file, "w") do |f|
      f.write text
    end
  end
end

def run_test(tasks)
  puts "---- Run #{tasks}"
  Dir.chdir("#{@extract_dest}/acceptance-tests")
  run_cmd "bundle install"
  run_cmd "bundle exec rake #{tasks} #{@env_var.join " "}"
end

def clean_up
  puts "---- Clean up"
  FileUtils.rm_rf @extract_dest
  FileUtils.rm @test_code_package if @test_code_package
  `kill #{@api_pid}` if @api_pid
end

def main
  begin
    init
    package_test_code @old_branch
    checkout @new_branch if @new_branch
    build_sli unless @ci
    unpackage_test_code @extract_dest
    replace_api_version @old_version if @old_version
    insert_migration_scripts
    update_feature_files
    start_api unless @ci
    run_test @rake_tasks
  rescue Exception => e
    puts e.message
    # puts e.backtrace.inspect
  ensure
    clean_up unless @ci
    puts "API log is available at #{@api_log}"
  end
end

main
