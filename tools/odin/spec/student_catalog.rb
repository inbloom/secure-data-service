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
require 'equivalent-xml'
require_relative 'spec_helper'
require_relative '../lib/odin.rb'
require_relative '../lib/Shared/util.rb'
require_relative '../lib/Shared/EntityClasses/student.rb'

describe "Student" do
  context "create a new instance of Student with an string id" do
    let(:id) {"tommynoble"}
    let(:int_id) {800000121}
    let(:birthday) {Date.new(1992, 2, 28)}
    let(:student) {Student.new(id, int_id, birthday)}
    
    it "Can access the init objects of the student_builder instance" do
      student.id.should eq("tommynoble")
      student.int_id.should eq(800000121)
      student.year_of.should eq(Date.new(1992, 2, 28))
      student.rand.should_not be_nil
    end
    
    it "Can access demographics.state class variable" do
      student.state.should eq("IL")
    end
    
    it "will choose gender" do
      student.sex.should match(/^Male|^Female/)
    end
      
    it "will match the city in choices.yml" do
      student.city.should eq("Chicago")
    end

    it "will match the HARDCODED zip code"  do
      student.postalCode.should eq("60601")
    end

    it "should generate a first name" do
      student.firstName.should_not be_nil
    end
    
    it "should have a valid email that ends in fakemail.com" do
      student.email.should match(/.*?@fakemail.com/)
    end
    
    it "should have a login id that ends in fakemail.com" do
      student.loginId.should match(/.*?@fakemail.com/)
    end
    
    it "should have a valid address in demographics" do
      student.address.should match(/[0-9]+\s[a-z|A-z]+/)
    end

    it "should return a boolean value for disability" do
      student.disability.should_not be_nil
    end
  end

  context "generate 10 students scenario with optional fields turned on" do
    before(:all) do
      odin = Odin.new
      odin.generate("10students_catalog")
      @docs = xml_to_doc
      @generated = "#{File.dirname(__FILE__)}/../generated"
    end

    describe "#baseline" do
      it "will generate catalog fields for student parent" do
        student_doc =  @docs["InterchangeStudentParent"].root
        student_count = student_doc.xpath("//*[local-name()='Student']").count
        student_count.should eq(11)

        student_ids = student_doc.xpath("//*[local-name()='StudentUniqueStateId']")
        student_ids.to_s.should include("msollars")
      end

      it "will generate catalog fields for student enrollment" do 
        student_enrollment =  parse_valid_xml(@generated, "InterchangeStudentEnrollment.xml")
        # verify msollars is associated with the catalog school for each enrollment
        student_count = 0
        school_count = 0
        section_count = 0
        section_include_list = ["1", "2", "3", "4", "5", "6", "7", "8", "13", "14", "15", "16"]

        student_enrollment.xpath("//StudentSchoolAssociation//StudentUniqueStateId").each do |susid|
          student_count += 1 if susid.to_s.include?("msollars")
        end
        student_count.should eq(3)

        student_enrollment.root.children.each do |node|
          node.children.each do |element|
            # Verify studentSchoolAssociations
            unless element.name == "StudentSectionAssociation"
              if (element/"./StudentReference/StudentIdentity/StudentUniqueStateId").text == "msollars"
                school_count += 1
                (element/"./SchoolReference/EducationalOrgIdentity/StateOrganizationId").text.should eq("Daybreak Central High")
              end
            end
            # Verify studentSectionAssociations
            unless element.name == "StudentSchoolAssociation"
              if (element/"./StudentReference/StudentIdentity/StudentUniqueStateId").text == "msollars"
                section_count += 1
                (element/"./SectionReference/SectionIdentity/EducationalOrgReference/EducationalOrgIdentity/StateOrganizationId").text.should eq("Daybreak Central High")
                section_include_list.include?((element/"./SectionReference/SectionIdentity/UniqueSectionCode").text)
              end
            end
          end
        end
        school_count.should eq(3)
        section_count.should eq(12)
      end

      it "will generate catalog fields for student assessment" do 
        student_assessment =  @docs["InterchangeStudentAssessment"].root
        count = 0 
        student_assessment.xpath("//*[local-name()='StudentUniqueStateId']").each do |student|
          count += 1 if student.to_s.include?("msollars")
        end
        count.should eq(108)
      end

      it "will generate catalog fields for student cohort" do 
        student_cohort =  @docs["InterchangeStudentCohort"].root
        count = 0 
        student_cohort.xpath("//*[local-name()='StudentUniqueStateId']").each do |student|
          count += 1 if student.to_s.include?("msollars")
        end
        count.should eq(3)
      end

      it "will generate catalog fields for student attendance" do 
        attendance = @docs["InterchangeAttendance"].root
        count = 0 
        attendance.xpath("//*[local-name()='StudentUniqueStateId']").each do |student|
          count += 1 if student.to_s.include?("msollars")
        end
        count.should eq(58)
      end

      it "will generate catalog fields for student discipline" do 
        student_discipline =  @docs["InterchangeStudentDiscipline"].root
        count = 0 
        student_discipline.xpath("//*[local-name()='StudentUniqueStateId']").each do |student|
          count += 1 if student.to_s.include?("msollars")
        end
        count.should eq(24)
      end

      it "will generate catalog fields for student grades" do 
        student_grades =  @docs["InterchangeStudentGrades"].root
        count = 0 
        student_grades.xpath("//*[local-name()='StudentUniqueStateId']").each do |student|
          count += 1 if student.to_s.include?("msollars")
        end
        count.should eq(442)
      end

      it "will generate catalog fields for student program" do 
        student_program =  @docs["InterchangeStudentProgram"].root
        count = 0 
        student_program.xpath("//*[local-name()='StudentUniqueStateId']").each do |student|
          count += 1 if student.to_s.include?("msollars")
        end
        count.should eq(8)
      end
    end
  end
end

def xml_to_doc
  docs = {}
    for f in Dir.entries(File.new "#{File.dirname(__FILE__)}/../generated") do
      if (f.end_with?(".xml"))
        doc = Nokogiri.XML( File.open( File.new "#{File.dirname(__FILE__)}/../generated/#{f}" ) )
        docs[File.basename(f,".xml")] = doc
      end
    end
   docs
end

def parse_valid_xml(dirname, xml_file)
  newfile = "#{dirname}/#{xml_file}.new"
  File.open(newfile, 'w') do |fo|
    fo.puts "<root>"
    fo.puts "<#{xml_file.split(".").first}>"
    line_count = 0
    File.foreach("#{dirname}/#{xml_file}") do |line|
      line_count += 1
      # get rid of the ed-fi elements that Nokogiri hates
      next if line_count == 0 || line_count == 1 || line_count == 2
      fo.puts line
    end
    fo.puts "</root>"
  end
  return Nokogiri::XML(File.open(newfile))
end
