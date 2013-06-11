require 'net/http'
require 'rexml/document'

def getType(elem)
  puts "inspecting #{elem.name}"
  if elem.name == "complexType"
    elem
  else
    getType(elem.parent)
  end
end

studentEntities = ['studentAssessment', 'studentObjectiveAssessment', 'reportCard', 'studentAcademicRecord', 'courseTranscript', 'gradebookEntry', 'grade', 'gradebookEntry',
                   'studentGradebookEntry', 'studentSchoolAssociation', 'cohort', 'studentAssessmentItem', 'attendance','studentCompetency']

publicEntities = ["assessment", "learningObjective", "learningStandard", "school", "educationOrganization"]

# extract event information
f = File.open("/Users/dkornishev/Documents/git/sli/sli/domain/target/classes/sliXsd/ComplexTypes.xsd")
doc = REXML::Document.new(f)

doc.elements.each('//xs:element') do |elem|
  appinfoArray = elem.get_elements('xs:annotation/xs:appinfo')
  puts "#{elem.attribute("name")} -> #{appinfoArray.size}"

  if appinfoArray.size>0
    appinfo = appinfoArray[0]
  else
    puts "adding appinfo"
    appinfo = REXML::Element.new("xs:appinfo", elem.get_elements("xs:annotation")[0])
  end

  if appinfo.get_elements("sli:ReadEnforcement").empty?
    puts "adding read enforcement"
    readEnforcement = REXML::Element.new("sli:ReadEnforcement", appinfo)
    readEnforcement.add_text("READ_GENERAL")
  end

=begin
  if appinfo.get_elements("sli:WriteEnforcement").empty?
    puts "adding write enforcement"
    writeEnforcement = REXML::Element.new("sli:WriteEnforcement", appinfo)
    writeEnforcement.add_text("WRITE_GENERAL")
  end

  puts appinfo.get_elements("sil:WriteEnforcement").size

  puts type.attribute("name")
  if publicEntities.include? type.attribute("name").to_s
  appinfo.get_elements("sli:WriteEnforcement")[0].text=("WRITE_PUBLIC")
  appinfo.get_elements("sli:ReadEnforcement")[0].text=("READ_PUBLIC")
  end
=end


  type = getType(elem)
  if studentEntities.include? type.attribute("name").to_s
    if appinfo.get_elements("sli:ReadEnforcement[sli:allowedBy='READ_STUDENT_DATA']").empty?
      puts "modifying  #{type.attribute('name')}"
      e = REXML::Element.new 'sli:allowedBy'
      e.add_text 'READ_STUDENT_DATA'

      appinfo.get_elements("sli:ReadEnforcement")[0].add_element e
    end
  end
end

f = File.open("ComplexTypes2.xsd", "w")
f.write(doc.to_s)
f.close()

puts doc.to_s
puts "ALL DONE"