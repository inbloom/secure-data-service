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
def update_file file_name
  file = read_file file_name
  doc = get_doc file

  add_school_year doc, '//xmlns:ReportCard','xmlns:GradeReference/xmlns:GradeIdentity'

  write_file file,doc
end

def  add_school_year doc,xpath_ref,xpath_to_modify
  doc.xpath(xpath_ref).each do |node|
    node.xpath(xpath_to_modify).each do |inner_node|
      school_year = XML::Element.new('SchoolYear', doc)
      school_year.content = node.xpath('xmlns:SchoolYear').text
      inner_node.add_child(school_year)
    end
  end
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
