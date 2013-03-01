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


require 'rubygems'
require 'fileutils'


############################################################
# ENVIRONMENT CONFIGURATION
############################################################

OFFLINE_TOOL_FLAG=false

############################################################
# STEPS: GIVEN
############################################################

Given /^I am using local input file directory$/ do
  @local_file_store_path = File.dirname(__FILE__) + '/../../test_data/'
end

Given /^I am using default offline tool package$/ do
  @tool_path = File.dirname(__FILE__) + '/../../../../../../ingestion/ingestion-validation/target/ingestion-validation-1.0-SNAPSHOT.jar'
end

#This is for handling zip file
Given /^I post "([^"]*)" file as an input to offline validation tool$/ do |file_name|
  OFFLINE_TOOL_FLAG=true

  path_name = file_name[0..-5]

  @pre_local_file_store_path = nil
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

  puts zip_dir
  FileUtils.rm_r zip_dir
end

#This is for handling ctl file
Given /^I post "([^"]*)" control file as an input to offline validation tool$/ do |file_name|
  path_name = file_name[0..-20]

  # copy everything into a new directory (to avoid touching git tracked files)
  ctl_file_path = @local_file_store_path + File.dirname(file_name) + "/"

  @pre_local_file_store_path = @local_file_store_path
  #changing local_file_store_path to the directory
  @local_file_store_path = @pre_local_file_store_path + File.dirname(file_name) + "/"
  ctl_template = nil
  puts File.dirname(file_name)
  # for each line in the ctl file, recompute the md5 hash
  File.open(ctl_file_path + "/" + File.basename(file_name), "r") do |ctl_file|
    ctl_file.each_line do |line|
      if line.chomp.length == 0
        next
      end
      entries = line.chomp.split ","
      if entries.length < 3
        puts "DEBUG:  less than 3 elements on the control file line.  Passing it through untouched: " + line
        next
      end
      payload_file = entries[2]
      md5 = Digest::MD5.file(ctl_file_path + payload_file).hexdigest;
      if entries[3] != md5.to_s
        puts "MD5 mismatch.  Replacing MD5 digest for #{entries[2]} in file #{ctl_template}"
      end
      # swap out the md5 unless we encounter the special all zero md5 used for unhappy path tests
      entries[3] = md5 unless entries[3] == "00000000000000000000000000000000"
    end
  end

  @source_file_name = File.basename(file_name)

end

############################################################
# STEPS: WHEN
############################################################

When /^I run offline validation command on input file$/ do
  @source_path = @local_file_store_path + @source_file_name

  if (INGESTION_MODE == 'remote')
    #Run shell script
    puts "Will Execute sh: " + "java -jar #{@tool_path} #{@source_path}"
    runShellCommand("java -jar " + @tool_path + " " + @source_path)
  else
    #Run shell script
    puts "Will Execute sh: " + "java -jar #{@tool_path} #{@source_path}"
    runShellCommand("java -jar " + @tool_path + " " + @source_path)
  end
end

When /^ "([^"]*)" seconds have elapsed$/ do |secs|
  sleep(Integer(secs))
end

############################################################
# STEPS: THEN
############################################################

def checkForContentInLogFile(message, prefix)

  if (INGESTION_MODE == 'remote')

    #offline tool doesn't need remote service
    #the processing is the same as local for now
    @job_status_filename = ""
    Dir.foreach(@local_file_store_path) do |entry|
      if (entry.rindex(prefix))
        # LAST ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(@local_file_store_path + @job_status_filename, "r")
    puts "STATUS FILENAME = " + @local_file_store_path + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(@local_file_store_path + @job_status_filename).join()
      #puts "FILE CONTENTS = " + file_contents

      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end
     aFile.close()
    else
       raise "File " + @job_status_filename + "can't be opened"
    end

  else
    @job_status_filename = ""
    Dir.foreach(@local_file_store_path) do |entry|
      if (entry.rindex(prefix))
        # LAST ENTRY IS OUR FILE
        @job_status_filename = entry
      end
    end

    aFile = File.new(@local_file_store_path + @job_status_filename, "r")
    puts "STATUS FILENAME = " + @local_file_store_path + @job_status_filename
    assert(aFile != nil, "File " + @job_status_filename + "doesn't exist")

    if aFile
      file_contents = IO.readlines(@local_file_store_path + @job_status_filename).join()
      #puts "FILE CONTENTS = " + file_contents

      if (file_contents.rindex(message) == nil)
        assert(false, "File doesn't contain correct processing message")
      end
      aFile.close()
    else
       raise "File " + @job_status_filename + "can't be opened"
    end
  end
end

Then /^I should see a log file in same directory$/ do

  @log_file = ""
  if(INGESTION_MODE == 'remote')
    #offline tool doesn't need remote service
    #the processing is the same as local for now
    Dir.foreach(@local_file_store_path) do |file|
      if /#{@source_file_name}.*.log$/.match file
        puts "Found " + file
        @log_file = file
      end
    end
    aFile = File.new(@local_file_store_path + "/" + @log_file, "r")
    assert(aFile != nil, "Log file " + @log_file + " dosen't exist")
    aFile.close()
  else
    Dir.foreach(@local_file_store_path) do |file|
      if /#{@source_file_name}.*.log$/.match file
        puts "Found " + file
        @log_file = file
      end
    end
    aFile = File.new(@local_file_store_path + "/" + @log_file, "r")
    assert(aFile != nil, "Log file " + @log_file + " dosen't exist")
    aFile.close()
  end
end

Then /^I should see "([^"]*)" in the resulting log file$/ do |message|
  prefix = @source_file_name + "-"
  checkForContentInLogFile(message, prefix)
end

After do
  if OFFLINE_TOOL_FLAG == true
      #deleting the log files
      Dir.foreach(@local_file_store_path) do |file|
        if /#{@source_file_name}.*.log$/.match file
          puts "Deleting " + file
          FileUtils.rm @local_file_store_path + "/" + file
        end
      end

      #Recovering local_file_store_path for future tests
      if (@pre_local_file_store_path != nil)
        @local_file_store_path = @pre_local_file_store_path
        @pre_local_file_store_path = nil
      end

      #cleaning up unzip files
      if Dir.exist?(@local_file_store_path + "/" + "unzip")
        FileUtils.rm_r @local_file_store_path + "/" + "unzip"
      end
  end
end
############################################################
# END
############################################################
