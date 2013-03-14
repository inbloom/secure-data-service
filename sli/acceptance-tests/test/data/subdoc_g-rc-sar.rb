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

require 'json'
require 'digest/sha1'

if ARGV.length < 3 or ARGV.include? "?" or ARGV.include? "-h" or ARGV.include? "--help"
  puts "Usage: ruby #{$0} <grade_fixture_file>|nil <repordCard_fixture_file>|nil <studentAcademicRecord_fixture_file>|nil"
  exit 1
end

def deep_copy(o)
  Marshal.load(Marshal.dump(o))
end

def get_all_school_years
  school_years = []
  school_years.concat @grades.map { |doc| doc["body"]["schoolYear"] }
  school_years.concat @rcs.map { |doc| doc["body"]["schoolYear"] }
  school_years.concat @sars.map { |doc| doc["body"]["schoolYear"] }
  school_years.uniq
end

def get_all_student_ids
  student_ids = []
  student_ids.concat @grades.map { |doc| doc["body"]["studentId"] }
  student_ids.concat @rcs.map { |doc| doc["body"]["studentId"] }
  student_ids.concat @sars.map { |doc| doc["body"]["studentId"] }
  student_ids.uniq
end

def select_doc_from(docs, school_year, student_id)
  docs.select { |doc| doc["body"]["schoolYear"] == school_year and doc["body"]["studentId"] == student_id }
end

def append_parent_id(parent_id, docs)
  docs.each do |doc|
    doc["_id"] = parent_id + doc["_id"]
  end
  docs
end

grade_fixture = ARGV[0]
rc_fixture = ARGV[1]
sar_fixture = ARGV[2]

template = {
  "_id" => "",
  "body" => {
    "schoolYear" => "",
    "studentId" => ""
  },
  "grade" => [],
  "reportCard" => [],
  "studentAcademicRecord" => []
}

@grades = []
@rcs = []
@sars = []
File.open(grade_fixture).each_line do |line|
  @grades << JSON.parse(line)
end unless grade_fixture == "nil"
File.open(rc_fixture).each_line do |line|
  @rcs << JSON.parse(line)
end unless rc_fixture == "nil"
File.open(sar_fixture).each_line do |line|
  @sars << JSON.parse(line)
end unless sar_fixture == "nil"

@yearly_transcripts = []
get_all_school_years.each do |school_year|
  get_all_student_ids.each do |student_id|
    doc = deep_copy template
    id_hash = ""
    id_hash << school_year << student_id
    doc["_id"] = Digest::SHA1.hexdigest id_hash + "_id"
    doc["_id"] += "_id"
    doc["body"]["schoolYear"] = school_year
    doc["body"]["studentId"] = student_id
    @yearly_transcripts << doc
  end
end

File.open("#{File.absolute_path(File.dirname $0)}/yearlyTranscript_fixture.json", "w") do |f|
  @yearly_transcripts.each do |yearly_transcript|
    id = yearly_transcript["_id"]
    school_year = yearly_transcript["body"]["schoolYear"]
    student_id = yearly_transcript["body"]["studentId"]
    grades = append_parent_id id, select_doc_from(@grades, school_year, student_id)
    rcs = append_parent_id id, select_doc_from(@rcs, school_year, student_id)
    sars = append_parent_id id, select_doc_from(@sars, school_year, student_id)
    yearly_transcript["grade"].concat grades
    yearly_transcript["reportCard"].concat rcs
    yearly_transcript["studentAcademicRecord"].concat sars
    f.puts yearly_transcript.to_json
  end
end
