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

# specifications for ODIN
describe "Odin" do
  context "with a 10 student configuration" do
    let(:odin) {Odin.new}
    before(:all) {odin.generate "10students"}
    describe "#validate" do
      it "generates valid XML for the default scenario" do
        odin.validate().should be true
      end
    end

    describe "#baseline" do
      it "will compare the output to baseline" do
        for f in Dir.entries(File.new "#{File.dirname(__FILE__)}/../generated") do
          if (f.end_with?(".xml") || f.end_with?(".ctl"))
            doc = Nokogiri.XML( File.open( File.new "#{File.dirname(__FILE__)}/../generated/#{f}" ) )

            # ensure there are no extra generated files without a corresponding baseline
            # as a hedge against adding files without a corresponding baseline.
            File.exists?("#{File.dirname(__FILE__)}/test_data/baseline/#{f}").should be_true

            baseline = Nokogiri.XML( File.open( File.new "#{File.dirname(__FILE__)}/test_data/baseline/#{f}" ) )
            #doc.should be_equivalent_to(baseline)
            EquivalentXml.equivalent?(doc, baseline) { |n1, n2, result| 
              puts "\t failed to reproduce baseline document: #{f}" if result == false
              result.should be_true
            }
          end
        end
        # ensure there are no missing generated files.
        for f in Dir.entries(File.new "#{File.dirname(__FILE__)}/test_data/baseline") do
          if (f.end_with?(".xml") || f.end_with?(".ctl"))
            File.exists?("#{File.dirname(__FILE__)}/../generated/#{f}").should be TRUE
          end
        end
      end
    end

    describe "#md5" do
      it "generates the same data for each run" do sha1 = odin.md5()
        odin.generate( "10students" )
        odin.md5().should eq sha1
      end
    end

    context "with the interchanges" do
      let(:student) {File.new "#{File.dirname(__FILE__)}/../generated/InterchangeStudentParent.xml"}
      let(:ctlFile) {File.new "#{File.dirname(__FILE__)}/../generated/ControlFile.ctl"}
      let(:lines) {ctlFile.readlines}
      let(:zipFile) {File.new "#{File.dirname(__FILE__)}/../generated/OdinSampleDataSet.zip"}
      
      before(:all) do
        @interchanges = Hash.new
        lines.each do |line|
          @interchanges[line.split(',')[1]] = line
        end
      end

      it "will generate a valid control file with the correct number of interchanges" do     
        @interchanges.length.should eq(13)
      end
      
      it "will generate a valid control file with Student as a type" do
        @interchanges["StudentParent"].should match(/StudentParent.xml/)
      end
      
      it "will generate a valid control file with EducationOrganization as a type" do
        @interchanges["EducationOrganization"].should match(/EducationOrganization.xml/)
      end
      
      it "will generate a valid control file with EducationOrgCalendar as a type" do
        @interchanges["EducationOrgCalendar"].should match(/EducationOrgCalendar.xml/)
      end
      
      describe "#generate" do
        it "will generate lists of 10 students" do
          student.readlines.select{|l| l.match("<Student>")}.length.should eq(10)
        end
        
        it "will generate a valid control file with the correct number of interchanges" do     
          @interchanges.length.should eq(13)
        end
        
        it "will generate a valid control file with Student as a type" do
          @interchanges["StudentParent"].should match(/StudentParent.xml/)
        end
        
        it "will generate a valid control file with EducationOrganization as a type" do
          @interchanges["EducationOrganization"].should match(/EducationOrganization.xml/)
        end
        
        it "will generate a valid control file with EducationOrgCalendar as a type" do
          @interchanges["EducationOrgCalendar"].should match(/EducationOrgCalendar.xml/)
        end
        
        it "will generate a valid control file with MasterSchedule as a type" do
          @interchanges["MasterSchedule"].should match(/MasterSchedule.xml/)
        end

        it "will generate a valid control file with StaffAssociation as a type" do
          @interchanges["StaffAssociation"].should match(/StaffAssociation.xml/)
        end

        it "will generate a valid control file with StudentEnrollment as a type" do
          @interchanges["StudentEnrollment"].should match(/StudentEnrollment.xml/)
        end

        it "will generate a valid control file with StudentGrades as a type" do
          @interchanges["StudentGrades"].should match(/StudentGrades.xml/)
        end

        it "will generate a valid control file with Attendance as a type" do
          @interchanges["Attendance"].should match(/Attendance.xml/)
        end
        
        it "will generate a zip file of the included interchanges" do
          # Make sure the zipfile exists in the dir we expect
          zipDir = "#{File.dirname(__FILE__)}/../generated"
          zipFile.should_not be_nil
          
          # Unzip the file in odin/generated/
          genDataUnzip(zipDir, "OdinSampleDataSet.zip", "OdinSampleDataSet") 
          # Verify the dumb number of files matches expected values
          # --> always add 2 files to your expected count for . and ..      
          #Dir.entries(File.new "#{zipDir}/OdinSampleDataSet").length.should eq(10)   
          # --> not sure this is a valid test.. what are we really testing with this? we already check the number of interchanges above.

          # Re-zip the data to prep for Acceptance Tests
          # --> this also feels wrong
          # --> acceptance testings can zip the generated/ directory (or OdinSampleDataSet/ sub-directory).. so why are we doing this?
          genDataZip("#{zipDir}/OdinSampleDataSet", "OdinSampleDataSet.zip", "#{zipDir}/OdinSampleDataSet")        
        end
        
      end
    end
    context "with the data set's manifest" do
      let(:manifest) {JSON.parse(File.new("#{File.dirname(__FILE__)}/../generated/manifest.json").read)}
      let(:interchanges) {read_interchanges}
      it "will show 10 students" do
        manifest['Student'].should eq 10
      end

      it "will have the number of ed-fi entities specified in the manifest" do
        manifest.each{|type, count|
          entity_count = count_entities(interchanges, type)
          entity_count.should eq(count), "expected #{count} of type #{type}, but got #{entity_count}"
        }
      end
    end
  end

  # context "with a 1000 student configuration" do
  #   let(:odin) {Odin.new}
  #   before(:all) {odin.generate("1000students")}
  #   let(:student) {File.new "#{File.dirname(__FILE__)}/../generated/InterchangeStudentParent.xml"}

  #   describe "#generate" do
  #     it "will generate lists of 1000 students" do
  #       student.readlines.select{|l| l.match("<Student>")}.length.should eq(1000)
  #     end
  #   end
  # end
  # --> this test takes too long when generating all entities

  context "with a configuration with only students whitelisted" do
    before(:all) { 
      odin = Odin.new
      odin.generate("1000studentsOnly") 
      @interchanges = read_interchanges
    }

    before(:each) {
      @student_parent_interchange = File.open("#{File.dirname(__FILE__)}/../generated/InterchangeStudentParent.xml", "r")
    }

    after(:each) {
      @student_parent_interchange.close
    }

    describe "#generate" do
      it "will generate lists of 1000 students" do
        num_students = 0
        while (line = @student_parent_interchange.gets)
          num_students += 1 if line.include?('<Student>')
        end
        num_students.should eq(1000)
      end
      it "will not generate any other entity" do
        num_parents = 0
        while (line = @student_parent_interchange.gets)
          num_parents += 1 if line.include?('<Parent>')
        end
        num_parents.should eq(0)
        @interchanges.should have(1).items
      end
    end
  end

  context "generate 10 students scenario with optional fields turned on" do
    before(:all) {
    odin = Odin.new
    odin.generate("10students_optional")
    @docs = xml_to_doc
    }

    describe "#baseline" do
      it "will generate optional fields for course" do
        edorg_doc =  @docs["InterchangeEducationOrganization"].root
        course_count = edorg_doc.xpath("//*[local-name()='Course']").count
        learningObjectiveRefs = edorg_doc.xpath("//*[local-name()='LearningObjectiveReference']")
        competencyLevelRefs = edorg_doc.xpath("//*[local-name()='CompetencyLevels']")
        #verify each course entity has 5 learningObjective ref
        learningObjectiveRefs.count.should eq(course_count*5)
        
        #verify each course entity has 4 competency leve ref
        competencyLevelRefs.count.should eq(course_count*4)

      end

      it "will generate optional fields for attendance" do
        attendance_doc = @docs['InterchangeAttendance'].root
        attendanceEvent_count = attendance_doc.xpath("//*[local-name()='AttendanceEvent']").count
        sessionRefs = attendance_doc.xpath("//*[local-name()='SessionReference']")
        sectionRefs = attendance_doc.xpath("//*[local-name()='SectionReference']")

        #verify every attendanceEvent will have one sessionRef and sectionRef
        sessionRefs.count.should eq(attendanceEvent_count)
        sectionRefs.count.should eq(attendanceEvent_count)
      end
    end
  end

end

def read_interchanges
  directory = "#{File.dirname(__FILE__)}/../generated/"
  Dir.entries(directory).select{|f| f.match(/xml$/)}.map{|f| File.new(directory + f).readlines}
end


def count_entities(interchanges, entity)
  interchanges.map{|i| i.select{|l| l.match("<#{entity}>$")}.count}.inject(:+)
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
