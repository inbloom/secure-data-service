=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

  Licensed under the Apache License, Version 2.0 (the 'License');
you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an 'AS IS' BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
limitations under the License.

=end

require "date"
require "nokogiri"
require "pp"

include Nokogiri

def get_doc file
  Nokogiri::XML(file) { |x| x.noblanks }
end

def read_file file
  File.open(file)
end
def write_file file, doc
  File.open(file, 'w') { |f| f << doc.to_xml }
end

def get_files file_name, dir=default_directory
  file_to_search = dir + "/**//"+ file_name
  Dir[file_to_search]
end
def default_directory
  top_level = `git rev-parse --show-toplevel`.chomp!
  File.join(top_level, "sli", "acceptance-tests", "test", "features", "ingestion", "test_data")
end

def  add_school_year doc,xpath_ref,ref_to_replace,school_year_path,criteria_list
  node_to_search = doc.xpath(school_year_path)
  doc.xpath(xpath_ref).each do |node|
    node_to_replace = node.xpath(ref_to_replace)
    node_to_search.each do |child_node|
      node_identified = true
      criteria_list.each do |key|
        unless node_to_replace.xpath(key).text == child_node.xpath(key).text
          node_identified = false
          break
        end
      end
      if node_identified
        school_year = XML::Element.new('SchoolYear', doc)
        school_year.content = child_node.xpath('xmlns:SchoolYear').text
        node_to_replace.each do |inner_node|
          inner_node.children.last.add_next_sibling(school_year)
        end
        break
      end
    end
  end
end
def update_file file_name

  file = read_file file_name
  doc = get_doc file

  course_transcript_criteria_list = Array.new
  course_transcript_criteria_list<<'xmlns:StudentReference/xmlns:StudentIdentity/xmlns:StudentUniqueStateId'
  course_transcript_criteria_list<<'xmlns:SessionReference/xmlns:SessionIdentity/xmlns:EducationalOrgReference/xmlns:EducationalOrgIdentity/xmlns:StateOrganizationId'
  course_transcript_criteria_list<<'xmlns:SessionReference/xmlns:SessionIdentity/xmlns:SessionName'

  add_school_year doc, '//xmlns:CourseTranscript','xmlns:StudentAcademicRecordReference/xmlns:StudentAcademicRecordIdentity','//xmlns:StudentAcademicRecord',course_transcript_criteria_list

  sar_criteria_list = Array.new
  sar_criteria_list<<'xmlns:StudentReference/xmlns:StudentIdentity/xmlns:StudentUniqueStateId'
  sar_criteria_list<<'xmlns:GradingPeriodReference/xmlns:GradingPeriodIdentity/xmlns:EducationalOrgReference/xmlns:EducationalOrgIdentity/xmlns:StateOrganizationId'
  sar_criteria_list<<'xmlns:GradingPeriodReference/xmlns:GradingPeriodIdentity/xmlns:GradingPeriod'
  sar_criteria_list<<'xmlns:GradingPeriodReference/xmlns:GradingPeriodIdentity/xmlns:BeginDate'

  add_school_year doc, '//xmlns:StudentAcademicRecord','xmlns:ReportCardReference/xmlns:ReportCardIdentity', '//xmlns:ReportCard',sar_criteria_list

  write_file file,doc
end
def main
  if(ARGV[0].nil?)
    name_of_file = "InterchangeStudentGrade.xml" 
    get_files(name_of_file).each do |file_name|
      update_file file_name
    end
  else
    name_of_file = ARGV[0]
    update_file name_of_file
  end
end

main
