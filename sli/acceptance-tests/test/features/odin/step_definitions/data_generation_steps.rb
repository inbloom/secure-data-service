require 'mongo'
require 'fileutils'
require_relative '../../utils/sli_utils.rb'

def generate(scenario="10students")
  # clear the generate dir
  unless @gen_path.nil? || @gen_path.empty? || "/".eql?(@gen_path)
    runShellCommand("rm -rf #{@gen_path}*")
  end
  # run bundle exec rake in the Odin directory
  command = "bundle exec ruby driver.rb --normalgc #{scenario}"
  puts "Shell command will be #{command}"
  FileUtils.cd @odin_working_path
  t1 = Time.now
  puts "Generating Data based on #{scenario} scenario.."
  `#{command}`
  runtime(t1, Time.now)
  FileUtils.cd @at_working_path
  @files = Dir.entries("#{@gen_path}")
end

############################################################
# STEPS: BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
############################################################
Before do
  @at_working_path = Dir.pwd
  @odin_working_path = "#{File.dirname(__FILE__)}" + "/../../../../../../tools/odin/"
end

############################################################
# STEPS: GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
############################################################
Given /^I am using the odin working directory$/ do
  @odin_working_path = "#{File.dirname(__FILE__)}" + "/../../../../../../tools/odin/"
end

############################################################
# STEPS: WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
############################################################
When /^I generate the "(.*?)" data set in the "(.*?)" directory$/ do |data_set, gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for #{data_set} scenario"
  generate(data_set)
end

When /^I generate the 10 student data set with optional fields on in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for 10 students scenario"
  generate("10students_optional")
end

When /^I generate the 10001 student data set in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for 10001 students scenario"
  generate("10001students")
end

When /^I generate the jmeter api performance data set in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for jmeter api performance scenario"
  generate("jmeter_api_performance")
end

When /^I generate the api data set in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for api testing scenario"
  generate("api_testing")
end

When /^I generate the bulk extract data set in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for api testing scenario"
  generate("api_2lea_testing")
end

When /^I generate the contextual roles data set in the (.*?) directory$/ do |gen_dir|
  @gen_path = "#{@odin_working_path}#{gen_dir}/"
  puts "Calling generate function for api testing scenario"
  generate("contextual_roles")
end

When /^I zip generated data under filename (.*?) to the new (.*?) directory$/ do |zip_file, new_dir|
  @zip_path = "#{@gen_path}#{new_dir}/"
  FileUtils.mkdir_p(@zip_path)
  FileUtils.chmod(0777, @zip_path)
  runShellCommand("zip -j #{@zip_path}#{zip_file} #{@gen_path}*.xml #{@gen_path}*.ctl")
end

When /^I zip generated data under filename "(.*?)"$/ do |zip_file|
  @zip_path = "#{@gen_path}/"
  FileUtils.mkdir_p(@zip_path)
  FileUtils.chmod(0777, @zip_path)
  runShellCommand("zip -j #{@zip_path}#{zip_file} #{@gen_path}*.xml #{@gen_path}*.ctl")
end

When /^I copy generated data to the new (.*?) directory$/ do |new_dir|
  @zip_path = "#{@gen_path}#{new_dir}"
  Dir["#{@gen_path}*.xml"].each do |f|
    FileUtils.cp(f, @zip_path)
    puts "Copied file #{f} to #{@zip_path}"
  end
  Dir["#{@gen_path}*.ctl"].each {|f| FileUtils.cp(f, @zip_path)}
end

# TODO this should be removed once Odin supports hybrid edorgs natively
When /^I convert schools to charter schools in "(.*?)"$/ do |filename|
  edorg_xml_file = "#{@gen_path}#{filename}"
  text = File.read(edorg_xml_file)
  replaced = text.gsub(/<OrganizationCategory>School<\/OrganizationCategory>/, "<OrganizationCategory>School</OrganizationCategory>\n      <OrganizationCategory>Local Education Agency</OrganizationCategory>")
  File.open(edorg_xml_file, "w") {|file| file.puts replaced}
end

############################################################
# STEPS: THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
############################################################
Then /^I should see generated file <File>$/ do |table|
  table.hashes.each do |f|
    raise "Did not find expected file #{f}" unless @files.find($f)
  end
end

Then /^I should see (.*?) has been generated$/ do |filename|
  raise "#{filename} does not exist" if !File.exist?("#{@gen_path}#{filename}")
end

Given /^the edfi manifest that was generated in the '([^\']*)' directory$/ do |dir|
  @manifest = "#{@odin_working_path}#{dir}/manifest.json"
end

Given /^the tenant is '([^\']*)'$/ do |tenant_id|
  @tenant_id = tenant_id
end

Then /^the sli\-verify script completes successfully$/ do
  disable_NOTABLESCAN()
  results = `bundle exec ruby #{@odin_working_path}sli-verify.rb #{@tenant_id} #{@manifest}`
  assert(results.include?("All expected entities found\n"), "verification script failed, results are #{results}")
  enable_NOTABLESCAN
end

def ingest_odin(scenario)
  step "I am using preconfigured Ingestion Landing Zone for \"Midgar-Daybreak\""
  @gen_path = "#{@odin_working_path}generated/"
  generate(scenario)
  steps %Q{
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
    When I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
    And I copy generated data to the new OdinSampleDataSet directory
    Given I am using odin data store
    And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OdinSampleDataSet.zip" is completed in database
  }
end

def runtime(t1, t2)
  t = (t2 - t1).to_s
  t_sec, t_dec = t.split(".")
  puts "Data generation took approximately: " + t_sec + "." + t_dec[0..-5] + " seconds to complete."
end


