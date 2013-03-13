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

require 'nokogiri'
require 'date'

include Nokogiri

def get_doc file
  Nokogiri::XML(file) { |x| x.noblanks }
end

def read_file file
  File.open(file)
end

def add_school_year doc, element_xpath, date_xpath
  doc.xpath(element_xpath).each do |element|
    string_date = element.xpath(date_xpath).text
    if string_date.empty?
      date = Date.parse('2010-01-01')
    else
      date = Date.parse(element.xpath(date_xpath).text)
    end
    school_year = XML::Element.new('SchoolYear', doc)
    school_year.content = calculate_year(date)
    element.children.last.add_next_sibling(school_year)
  end
end

def calculate_year date
  if date.month < 8
    date.prev_year.year.to_s + "-" + date.year.to_s
  else
    date.year.to_s + "-" + date.next_year.year.to_s
  end
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

def main
  get_files("InterchangeStudentGrade.xml").each do |file_name|
    file = read_file file_name
    doc = get_doc(file)
    grading_period_date_xpath = 'xmlns:GradingPeriodReference/xmlns:GradingPeriodIdentity/xmlns:BeginDate'
    add_school_year doc, '//xmlns:ReportCard', grading_period_date_xpath
    add_school_year doc, '//xmlns:Grade', 'xmlns:StudentSectionAssociationReference/xmlns:StudentSectionAssociationIdentity/xmlns:BeginDate'  
    add_school_year doc, '//xmlns:StudentAcademicRecord', 'xmlns:ReportCardReference/xmlns:ReportCardIdentity/' + grading_period_date_xpath
    write_file file, doc
  end
end

main

