=begin

Copyright 2012 Shared Learning Collaborative, LLC

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
require_relative 'spec_helper'
require_relative '../lib/OutputGeneration/XML/enrollmentGenerator'

# specifications for student enrollment interchange generator
describe 'EnrollmentGenerator' do

  before (:all) do
    path = File.join( "#{File.dirname(__FILE__)}/", "../generated/InterchangeStudentEnrollment.xml" )
    interchange = File.open(path, 'w')
    @generator = EnrollmentGenerator.new(get_spec_scenario(), interchange)
    @generator.start
    @generator.create_student_school_association(42, 64, 2004, :FIRST_GRADE)
    @generator.create_student_section_association(43, 128, 65, 2005, :SECOND_GRADE)
    @generator.finalize
    @student_enrollment = File.open("#{File.dirname(__FILE__)}/../generated/InterchangeStudentEnrollment.xml", "r") { |file| file.read }
  end

  describe '--> creating student school association' do
    it 'will write a StudentSchoolAssociation to ed-fi xml interchange' do  
      @student_enrollment.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      @student_enrollment.match('<StateOrganizationId>elem-0000000064</StateOrganizationId>').should_not be_nil
      @student_enrollment.match('<EntryDate>2004-09-01</EntryDate>').should_not be_nil
      @student_enrollment.match('<EntryGradeLevel>First grade</EntryGradeLevel>').should_not be_nil
    end
  end

  describe '--> creating student section association' do
    it 'will write a StudentSectionAssociation to ed-fi xml interchange' do
      @student_enrollment.match('<StudentUniqueStateId>43</StudentUniqueStateId>').should_not be_nil
      @student_enrollment.match('<StateOrganizationId>elem-0000000065</StateOrganizationId>').should_not be_nil
      @student_enrollment.match('<BeginDate>2005-09-01</BeginDate>').should_not be_nil
      @student_enrollment.match('<UniqueSectionCode>128</UniqueSectionCode>').should_not be_nil
    end
  end
end
