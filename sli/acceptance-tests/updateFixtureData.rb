#!/usr/bin/env ruby

require 'json'
require 'fileutils'
require 'logger'

# To use this script find all directories with student fixture data and put in a file
# find `pwd` -name *student_*.json -or -name *student.json | xargs -L1 dirname | sort -u > fixture_directories
# run using that file: ruby updateFixtureData.rb fixture_directories
# scripts assumes new line at end of each line, and each line is unique

$stdout.sync = true
@log = Logger.new($stdout)
@log.level = Logger::INFO

if ARGV.length != 1
  @log.error "Usage: updateFixtureData.rb {directory_file}"
  exit 1
end

directory_file=ARGV[0]
@log.info "File containing directories: "+directory_file

File.open(directory_file).each do |directory|
    #remove newline
    directory=directory[0..-2]
    @log.info "Directory: "+directory
    Dir.chdir(directory) do
      #get data to move and delete from student.json
      Dir.glob('*student{.json,_*.json}').each do |students_file|
        @log.info "Student file: "+students_file
        migrated_data = Hash.new
        FileUtils.rm_f('updated_students.json')
        File.open(students_file).each do |line|
          #where fixture data does not parse as json
          begin
            json = JSON.parse(line)
          rescue Exception => e
            next
          end
          if json.has_key?("schools")
            json["schools"].each do |schools_item|
              if schools_item.has_key?("edOrgs")
                migrated_data[schools_item["_id"]]=schools_item["edOrgs"]
                schools_item.delete("edOrgs")
              end
            end
          end
          File.open('updated_students.json', 'a') do |file|
            file.puts(json.to_json)
          end
        end
        FileUtils.mv('updated_students.json', students_file) if File.exist?('updated_students.json')
          #update associated educationOrganization.json
          Dir.glob('*educationOrganization{.json,_*.json}').each do |edOrgs_file|
          @log.info "EducationOrganization file: "+edOrgs_file
            FileUtils.rm_f('updated_edOrgs.json')
            File.open(edOrgs_file).each do |line|
              #where fixture data does not parse as json
              begin
                json = JSON.parse(line)
              rescue Exception => e
                next
              end
                if migrated_data.has_key?(json["_id"])
                  if migrated_data.has_key?(json["metadata"])
                    json["metaData"].merge!(Hash["edOrgs"=>migrated_data[json["_id"]]])
                  #edge case where no metaData
                  else
                    json["metaData"]=Hash["edOrgs"=>migrated_data[json["_id"]]]
                  end
                else
                  if migrated_data.has_key?(json["metadata"])
                    json["metaData"].merge!(Hash["edOrgs"=>json["_id"]]) if json["type"]=="school"
                  #edge case where no metaData
                  else
                    json["metaData"]=Hash["edOrgs"=>json["_id"]]
                  end
                end
              File.open('updated_edOrgs.json', 'a') do |file|
                file.puts(json.to_json)
              end
            end
            FileUtils.mv('updated_edOrgs.json', edOrgs_file) if File.exist?('updated_edOrgs.json')
          end
      end
    end
end