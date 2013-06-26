require 'net/http'
require 'rexml/document'

def getType(elem)
  #puts "inspecting #{elem.name}"
  if elem.name == "complexType"
    elem
  else
    getType(elem.parent)
  end
end

def createNode (name, value)
  e = REXML::Element.new name
  e.add_text value
  e
end

REGULAR_CONTEXT_RIGHT = "STUDENT_GENERAL"
SELF_CONTEXT_RIGHT = "STUDENT_OWNED"

studentEntities = ['studentAssessment', 'studentObjectiveAssessment', 'reportCard', 'studentAcademicRecord', 'courseTranscript', 'gradebookEntry', 'grade', 'gradebookEntry',
                   'studentGradebookEntry', 'studentSchoolAssociation', 'cohort', 'studentAssessmentItem', 'attendance', 'studentCompetency', 'studentParentAssociation']

studentPartials = {
    'staffCohortAssociation' => Proc.new { |fieldName|
      if ['staffId', 'cohortId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        :NOOP
      end },
    'staffProgramAssociation' => Proc.new { |fieldName|
      if ['staffId', 'programId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        :NOOP
      end },
    'teacherSectionAssociation' => Proc.new { |fieldName|
      if ['teacherId', 'sectionId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        :NOOP
      end },
    'teacherSchoolAssociation' => Proc.new { |fieldName|
      if ['teacherId', 'schoolId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        :NOOP
      end },
    'staffEducationOrganizationAssociation' => Proc.new { |fieldName|
      if ['staffReference', 'educationOrganizationReference'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        :NOOP
      end },
    'student' => Proc.new { |fieldName|
      if ['name'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      elsif ['economicDisadvantaged','schoolFoodServicesEligibility'].include? fieldName
        "STUDENT_RESTRICTED"
      else
        SELF_CONTEXT_RIGHT
      end },
    'studentCohortAssociation' => Proc.new { |fieldName|
      if ['studentId', 'cohortId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        SELF_CONTEXT_RIGHT
      end },
    'studentProgramAssociation' => Proc.new { |fieldName|
      if ['studentId', 'programId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        SELF_CONTEXT_RIGHT
      end },
    'studentSectionAssociation' => Proc.new { |fieldName|
      if ['studentId', 'sectionId'].include? fieldName
        REGULAR_CONTEXT_RIGHT
      else
        SELF_CONTEXT_RIGHT
      end }
}


publicEntities = ["assessment", "learningObjective", "learningStandard", "school", "educationOrganization"]

# extract event information
f = File.open("../../domain/src/main/resources/sliXsd/ComplexTypes.xsd")
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

  if appinfo.get_elements("sli:WriteEnforcement").empty?
    puts "adding write enforcement"
    writeEnforcement = REXML::Element.new("sli:WriteEnforcement", appinfo)
    writeEnforcement.add_text("WRITE_GENERAL")
  end

  type = getType(elem)

=begin
  if publicEntities.include? type.attribute("name").to_s
    appinfo.get_elements("sli:WriteEnforcement")[0].text=("WRITE_PUBLIC")
    appinfo.get_elements("sli:ReadEnforcement")[0].text=("READ_PUBLIC")
  end
=end

  typeName = type.attribute("name").to_s
  fieldName = elem.attribute('name').to_s
  if studentEntities.include? type.attribute("name").to_s

    if appinfo.get_elements("sli:ReadEnforcement[sli:allowedBy='READ_STUDENT_GENERAL']").empty?
      puts "modifying #{typeName}.#{fieldName}"
      e = createNode 'sli:allowedBy', 'READ_STUDENT_GENERAL'
      appinfo.get_elements("sli:ReadEnforcement")[0].add_element e
    end

    if appinfo.get_elements("sli:WriteEnforcement[sli:allowedBy='WRITE_STUDENT_GENERAL']").empty?
      puts "modifying #{typeName}.#{fieldName}"
      e = createNode 'sli:allowedBy', 'WRITE_STUDENT_GENERAL'
      appinfo.get_elements("sli:WriteEnforcement")[0].add_element e
    end
  elsif studentPartials.has_key? typeName and studentPartials[typeName].call(fieldName) != :NOOP
    right = studentPartials[typeName].call(fieldName)
    puts "Partial entity for student access: #{typeName}.#{fieldName} -> #{right}"

    if appinfo.get_elements("sli:ReadEnforcement[sli:allowedBy='READ_#{right}']").empty?
      e = createNode 'sli:allowedBy', "READ_#{right}"
      appinfo.get_elements("sli:ReadEnforcement")[0].add_element e
    end

    if appinfo.get_elements("sli:WriteEnforcement[sli:allowedBy='WRITE_#{right}']").empty?
      e = createNode 'sli:allowedBy', "WRITE_#{right}"
      appinfo.get_elements("sli:WriteEnforcement")[0].add_element e
    end
  end
end

f = File.open("ComplexTypes2.xsd", "w")
f.write(doc.to_s)
f.close()

#puts doc.to_s
puts "ALL DONE"
