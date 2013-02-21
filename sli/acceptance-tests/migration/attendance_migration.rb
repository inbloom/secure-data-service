#!/usr/bin/env ruby

require 'json'
require 'securerandom'

class Migration
  def self.migrate json
    student_id = json['body']['studentId']
    school_id = json['body']['schoolId']
    school_year_key = 'schoolYearAttendance'
    school_year_attendance = json['body'][school_year_key]
    
    base_doc = json.clone
    base_doc['body'].delete school_year_key

    if school_year_attendance.length == 0
      #Add the default school year if we have nowhere to pull it from
      json['body']['schoolYear'] = "2011-2012"
      json
    elsif school_year_attendance.length == 1
      base_doc['body']['schoolYear'] = school_year_attendance[0]['schoolYear']
      base_doc['body']['attendanceEvent'] = school_year_attendance[0]['attendanceEvent']
      base_doc
    else
      split_school_year_attendance base_doc, school_year_attendance
    end
  end
  
  def self.split_school_year_attendance base_doc, school_year_array
    puts "-" * 80
    puts "this document has the following school years"
    school_year_array.each do |school_year|
      puts school_year['schoolYear']
    end
    puts "-" * 80
  end

  # For real generation of ids, we would need to change this!
  def self.gen_id natural_key_map
    SecureRandom.uuid
  end

  def self.start_migration input_file
    migrated_records = []
    File.readlines(input_file).each do |line|
      migrated_records << migrate(JSON.parse(line)).to_json
    end
    
    File.open(input_file, "w") do |file|
      migrated_records.each do |record|
        file.puts record
      end
    end
  end
end

if __FILE__ == $0
  unless ARGV.length == 1
    puts "Usage: " + $0 + "<attendance.json fixture to migrate>"
    exit(1)
  end
  
  Migration.start_migration ARGV[0]
end
