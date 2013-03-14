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

if ARGV.length < 2 or ARGV.include? "?" or ARGV.include? "-h" or ARGV.include? "--help"
  puts "Usage: ruby #{$0} <fixture_data_directory> <yearly_transcript_fixture>"
  exit 1
end

fixture_dir = ARGV[0]
yearly_transcript_fixture = ARGV[1]
@yearly_transcripts = []
File.open("#{fixture_dir}/#{yearly_transcript_fixture}").each_line do |line|
  @yearly_transcripts << JSON.parse(line)
end

def replace_grade_ids(doc, file)
  if file.include? "yearlyTranscript"
    report_cards = doc["reportCard"]
    report_cards.each do |report_card|
      grades = report_card["body"]["grades"]
      unless grades.nil?
        grades_replaced = []
        grades.each do |grade_id|
          grades_replaced << find_and_replace_id("grade", grade_id)
        end
        report_card["grades"] = grades_replaced
      end
    end
  end
  doc
end

def replace_report_card_ids(doc, file)
  if file.include? "yearlyTranscript"
    student_academic_records = doc["studentAcademicRecord"]
    student_academic_records.each do |sar|
      report_cards = sar["body"]["reportCards"]
      unless report_cards.nil?
        report_cards_replaced = []
        report_cards.each do |report_card_id|
          report_cards_replaced << find_and_replace_id("reportCard", report_card_id)
        end
        report_card["report_cards"] = report_cards_replaced
      end
    end
  end
  doc
end

def replace_student_academic_record_ids(doc, file)
  if file.include? "courseTranscript"
    sar_id = doc["body"]["studentAcademicRecordId"]
    doc["body"]["studentAcademicRecordId"] = find_and_replace_id("studentAcademicRecord", sar_id) unless sar_id.nil?
    doc
  end
end

def find_and_replace_id(type, old_id)
  new_id = ""
  if type == "grade"
    @yearly_transcripts.each do |yearly_transcript|
      grades = yearly_transcript["grade"].select { |doc| doc["_id"][43..-1] == old_id }
      new_id = grades[0]["_id"] unless grades.empty?
    end
  elsif type == "reportCard"
    @yearly_transcripts.each do |yearly_transcript|
      report_cards = yearly_transcript["reportCard"].select { |doc| doc["_id"][43..-1] == old_id }
      new_id = report_cards[0]["_id"] unless report_cards.empty?
    end
  else
    @yearly_transcripts.each do |yearly_transcript|
      sars = yearly_transcript["studentAcademicRecord"].select { |doc| doc["_id"][43..-1] == old_id }
      new_id = sars[0]["_id"] unless sars.empty?
    end
  end
  new_id
end

Dir.glob("#{fixture_dir}/*.json").each do |file|
  next unless file.include? "yearlyTranscript" or file.include? "courseTranscript"
  docs = []
  File.open(file).each_line do |line|
    doc = JSON.parse(line)
    doc = replace_grade_ids doc, file
    doc = replace_report_card_ids doc, file
    doc = replace_student_academic_record_ids doc, file
    docs << doc
  end
  File.open(file, "w") do |f|
    docs.each do |d|
      f.puts d.to_json
    end
  end
end
