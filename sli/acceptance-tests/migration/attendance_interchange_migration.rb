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

