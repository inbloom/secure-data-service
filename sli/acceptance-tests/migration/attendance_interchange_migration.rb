#!/usr/bin/env ruby

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

require "rexml/document"
require "date"

include REXML
if(ARGV[0].nil?)
  file_to_search = "InterchangeAttendance.xml"
else
  file_to_search = ARGV[0]
end
  dir = Dir.pwd
file_to_search = dir.to_s + "/**//"+ file_to_search
Dir[file_to_search].each do |file_name|

  file = File.new(file_name)
  doc = REXML::Document.new file

  doc.root.each_element('//AttendanceEvent//EventDate') do |e|
    school_year = Element.new('SchoolYear')
    date = Date.parse(e.text)
    school_year_text = String.new
    if (date.month < 9) 
      school_year_text = date.prev_year.year.to_s + "-" +date.year.to_s
    else
      school_year_text = date.year.to_s + "-" +date.next_year.year.to_s
    end
    school_year.add_text(school_year_text)
    e.previous_sibling = school_year
  end 
  formatter = REXML::Formatters::Pretty.new
  formatter.compact = true
  File.open(file_name, 'w') {|f| formatter.write(doc, f)}
  puts 'Added schoolyear To file : ' + file_name
end

