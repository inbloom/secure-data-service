require "rexml/document"
require 'digest/sha1'

#Hold the command line options
options = {}

if ARGV.size < 3
    puts "Usage: #{__FILE__} staffAssociationInterchange"
end
@options = {}
@options[:input] = ARGV[0]

puts "Opening file streams to #{@options[:input]}"
input_file = File.new(@options[:input], 'r+')
document = REXML::Document.new(input_file)
SEA = "\n\t<StaffEducationOrgAssignmentAssociation>
        <StaffReference>
            <StaffIdentity>
                <StaffUniqueStateId>$STAFF</StaffUniqueStateId>
            </StaffIdentity>
        </StaffReference>
        <EducationOrganizationReference>
            <EducationalOrgIdentity>
                <StateOrganizationId>$EDORG</StateOrganizationId>
            </EducationalOrgIdentity>
        </EducationOrganizationReference>
        <StaffClassification>Teacher</StaffClassification>
        <BeginDate>2007-07-07</BeginDate>
    </StaffEducationOrgAssignmentAssociation>"
#Get the TSAs
count = 0
document.elements.each('InterchangeStaffAssociation/TeacherSchoolAssociation') do |tsa|
    #Get the teacher staffUniqueStateId
    school = nil
    teacher = nil
    tsa.elements.each('SchoolReference/EducationalOrgIdentity/StateOrganizationId') {|id| school = id.text}
    tsa.elements.each('TeacherReference/StaffIdentity/StaffUniqueStateId') {|id| teacher = id.text}
    puts "Adjusting #{teacher}@#{school}"
    #Create and write a SEA
    sea = String.new(SEA).sub(/\$STAFF/, teacher)
    sea = sea.sub(/\$EDORG/, school)
    sea = REXML::Document.new sea
    document.root.insert_after tsa, sea
    count += 1
end
puts "Converted #{count} teacherSchoolAssociations to staffEdorgAssignmentAssociations"
input_file.close
input_file = File.new(@options[:input], 'w')
document.write(input_file)
input_file.close
