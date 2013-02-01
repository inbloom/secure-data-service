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

@options = {}
ARGV.options do |opts|
  opts.banner = "Usage:\n  > ruby #{$0} -b <old_branch> [-v <old_version>] [-B <new_branch>] [-c] [-h]"
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
  @sli_workspace = File.absolute_path("#{File.dirname(__FILE__)}/../")
  @extract_dest = "#{@sli_workspace}/test-bundle-extract/"
  @api_log = "#{@sli_workspace}/acceptance-tests/target/api_version_upgrade_test.log"
  @dirs_to_archive = ["acceptance-tests",
                      "opstools",
                      "admin-tools/approval",
                      "ingestion/ingestion-validation/target/",
                      "api/src/main/resources/wadl/"]
  @old_version = @options[:old_version]
  @old_branch = @options[:old_branch]
  @new_branch = @options[:new_branch]
  @env_var = @options[:env_var] ? @options[:env_var].split(",") : ["TOGGLE_TABLESCANS", "FORCE_COLOR"]
  @env_var.map! {|e| "#{e}=1"}
  # @rake_tasks = @options[:tasks] ? @options[:tasks].join(" ") : "apiAndSecurityTests"
  @rake_tasks = "apiAndSecurityTests"
  @ci = @options[:ci]
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
  puts "---- Replace API versions in URLs with \"#{@old_version}\" in test code"
  Dir[File.join("#{@extract_dest}/acceptance-tests/test/features", "**", "*.{rb,feature}")].each do |f|
    text = File.read(f).gsub(/v\d+\.?\d*/, "#{@old_version}")
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

def copy_new_custom_roles
  puts "---- Replace custom role fixtures"
  Dir["#{@sli_workspace}/acceptance-tests/test/data/customRole*.json"].each do |f|
    FileUtils.cp(f, "#{@extract_dest}/acceptance-tests/test/data/")
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
    start_api unless @ci
    copy_new_custom_roles
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
