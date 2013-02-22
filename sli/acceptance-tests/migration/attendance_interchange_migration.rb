require "rexml/document"
require "date"

include REXML
if(ARGV[0].nil?)
  dir = Dir.pwd
else
  dir = ARGV[0]
end
file_to_search = dir.to_s + "/**/InterchangeAttendance.xml"
Dir[file_to_search].each do |file_name|

  file = File.new(file_name)
  doc = REXML::Document.new file

  subdoc = '<schoolYear>2011-2012</schoolYear>'
  subdoc = REXML::Document.new(p)
  doc.root.each_element('//AttendanceEvent//EventDate') do |e|
    school_year = Element.new('SchoolYear')
    date = Date.parse(e.text)
    school_year_text = date.year.to_s + "-" +date.next_year.year.to_s
    school_year.add_text(school_year_text)
    e.previous_sibling = school_year
  end 
  formatter = REXML::Formatters::Pretty.new
  formatter.compact = true
  File.open(file_name, 'w') {|f| formatter.write(doc, f)}
end

