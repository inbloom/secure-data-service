#!/usr/bin/env ruby

require 'json'
require 'fileutils'
require 'logger'

# To use this script find all directories with student fixture data and put in a file
# find `pwd` -name *student_*.json -or -name *student.json | xargs -L1 dirname | sort -u > fixture_directories
# run using that file: ruby updateFixtureData.rb fixture_directories
# scripts assumes new line at end of each line, and each line is unique

  def process_edOrg_file(edOrgs_file_name, migrated_data)
    # migrated_data is a hash of school id and edOrgs (representing edOrg lineage)
    @log.info "EducationOrganization file: " + edOrgs_file_name
    if File.exists?('updated_edOrgs.json')
      @log.error "updated_edOrgs.json already exists"
      exit 1
    end
    File.open(edOrgs_file_name).each do |line|
      # where fixture data does not parse as json
      begin
        edOrg_json = JSON.parse(line)
      rescue Exception => e
        @log.error "Unable to parse line as json: " + line
        e.backtrace.each { |trace_line| @log.error trace_line }
        next
      end
      # only create edOrgs for schools
      if  edOrg_json["type"] == "school"
        if migrated_data.has_key?(edOrg_json["_id"])
          if migrated_data.has_key?(edOrg_json["metaData"])
            edOrg_json["metaData"].merge!(Hash["edOrgs" => migrated_data[edOrg_json["_id"]]])
          #edge case where no metaData
          else
            edOrg_json["metaData"] = Hash["edOrgs" => migrated_data[edOrg_json["_id"]]]
          end
        else
          if migrated_data.has_key?("metaData")
            edOrg_json["metaData"].merge!(Hash["edOrgs" => [edOrg_json["_id"]]])
          #edge case where no metaData
          else
            edOrg_json["metaData"] = Hash["edOrgs" => [edOrg_json["_id"]]]
          end
        end
      end
      File.open('updated_edOrgs.json', 'a') do |file|
        file.puts(edOrg_json.to_json)
      end
    end
    FileUtils.mv('updated_edOrgs.json', edOrgs_file_name) if File.exist?('updated_edOrgs.json')
  end

  def process_student_file(students_file_name)
    @log.info "Student file: " + students_file_name
    migrated_data = Hash.new
    if File.exists?('updated_students.json')
      @log.error "updated_students.json already exists"
      exit 1
    end
    File.open(students_file_name).each do |line|
      # where fixture data does not parse as json
      begin
        student_json = JSON.parse(line)
      rescue Exception => e
        @log.error "Unable to parse line as json: " + line
        e.backtrace.each { |trace_line| @log.error trace_line }
        next
      end
      if student_json.has_key?("schools")
        student_json["schools"].each do |schools_item|
          if schools_item.has_key?("edOrgs")
            migrated_data[schools_item["_id"]] = schools_item["edOrgs"]
            schools_item.delete("edOrgs")
          end
        end
      end
      File.open('updated_students.json', 'a') do |file|
        file.puts(student_json.to_json)
      end
    end
    FileUtils.mv('updated_students.json', students_file_name) if File.exist?('updated_students.json')
    # update associated educationOrganization.json
    Dir.glob('*educationOrganization{.json,_*.json}').each do |edOrgs_file_name|
      process_edOrg_file(edOrgs_file_name, migrated_data)
    end
  end

$stdout.sync = true
@log = Logger.new($stdout)
@log.level = Logger::INFO

if ARGV.length != 1
  @log.error "Usage: updateFixtureData.rb {directory_file}"
  exit 1
end

directory_file = ARGV[0]
@log.info "File containing directories: " + directory_file

  File.open(directory_file).each do |directory|
    # remove newline
    directory = directory[0..-2]
    @log.info "Directory: " + directory
    Dir.chdir(directory) do
      # get data to move and delete from student.json
      Dir.glob('*student{.json,_*.json}').each do |students_file_name|
        process_student_file(students_file_name)
      end
    end
  end



