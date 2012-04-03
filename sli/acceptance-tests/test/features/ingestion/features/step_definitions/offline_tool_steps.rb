require 'rubygems'
require 'mongo'
require 'fileutils'

require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

INGESTION_SERVER_URL = PropLoader.getProps['ingestion_server_url']
INGESTION_MODE = PropLoader.getProps['ingestion_mode']

############################################################
# STEPS: GIVEN
############################################################

Given /^I am using local data store$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end

Given /^I am using default offline tool package$/ do
  @tool_path = File.dirname(__FILE__) + '/../../test_data/'
end

Given /^I post "([^"]*)" file as an input to offline validation tool$/ do |file_name|
  path_name = file_name[0..-5]

  # copy everything into a new directory (to avoid touching git tracked files)
  zip_dir = @local_file_store_path + "temp-" + path_name + "/"
  if Dir.exists?(zip_dir)
    FileUtils.rm_r zip_dir
  end
  FileUtils.cp_r @local_file_store_path + path_name, zip_dir

  ctl_template = nil
  Dir.foreach(zip_dir) do |file|
    if /.*.ctl$/.match file
      ctl_template = file
    end
  end

  # for each line in the ctl file, recompute the md5 hash
  new_ctl_file = File.open(zip_dir + ctl_template + "-tmp", "w")
  File.open(zip_dir + ctl_template, "r") do |ctl_file|
    ctl_file.each_line do |line|
      if line.chomp.length == 0
        next
      end
      entries = line.chomp.split ","
      if entries.length < 3
        puts "DEBUG:  less than 3 elements on the control file line.  Passing it through untouched: " + line
        new_ctl_file.puts line.chomp
        next
      end
      payload_file = entries[2]
      md5 = Digest::MD5.file(zip_dir + payload_file).hexdigest;
      if entries[3] != md5.to_s
        puts "MD5 mismatch.  Replacing MD5 digest for #{entries[2]} in file #{ctl_template}"
      end
      # swap out the md5 unless we encounter the special all zero md5 used for unhappy path tests
      entries[3] = md5 unless entries[3] == "00000000000000000000000000000000"
      new_ctl_file.puts entries.join ","
    end
  end
  new_ctl_file.close
  FileUtils.mv zip_dir + ctl_template + "-tmp", zip_dir + ctl_template

  runShellCommand("zip -j #{@local_file_store_path}#{file_name} #{zip_dir}/*")
  @source_file_name = file_name

  FileUtils.rm_r zip_dir
end

############################################################
# STEPS: WHEN
############################################################

When /^I run offline validation command on input file$/ do
  @source_path = @local_file_store_path + @source_file_name
  
  #Run shell script
  puts "Will Execute sh: " + "java -jar #{@tool_path} #{@source_path}"
  runShellCommand("java -jar " + @tool_path + " " + @source_path)
end

When /^ "([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end

############################################################
# STEPS: THEN
############################################################

Then /^I should see a log file in same directory$/ do
  #TODO
end

############################################################
# END
############################################################