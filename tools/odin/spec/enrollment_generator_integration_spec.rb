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

require_relative 'spec_helper'
require_relative '../lib/OutputGeneration/XML/enrollmentGenerator'

# specifications for student enrollment interchange generator
describe 'EnrollmentGenerator' do

  before (:all) do
    path = File.join( "#{File.dirname(__FILE__)}/", "../generated/InterchangeStudentEnrollment.xml" )
    interchange = File.open(path, 'w')
    @generator = EnrollmentGenerator.new(get_spec_scenario(), interchange)
    @generator.start
    @generator << StudentSchoolAssociation.new(42, "elem-0000000064", Date.new(2004, 9, 3), :FIRST_GRADE)
    @generator << StudentSectionAssociation.new(43, "sctn-0025600128", "elem-0000000065", Date.new(2005, 9, 6), :SECOND_GRADE)
    @generator << GraduationPlan.new("Standard", {"English" => 9, "Science" => 12, "Math" => 15}, "elem-0000000064")
    @generator.finalize
    @student_enrollment = File.open("#{File.dirname(__FILE__)}/../generated/InterchangeStudentEnrollment.xml", "r") { |file| file.read }
  end

  describe '--> creating student school association' do
    it 'will write a StudentSchoolAssociation to ed-fi xml interchange' do  
      @student_enrollment.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      @student_enrollment.match('<StateOrganizationId>elem-0000000064</StateOrganizationId>').should_not be_nil
      @student_enrollment.match('<EntryDate>2004-09-03</EntryDate>').should_not be_nil
      @student_enrollment.match('<EntryGradeLevel>First grade</EntryGradeLevel>').should_not be_nil
    end
  end

  describe '--> creating student section association' do
    it 'will write a StudentSectionAssociation to ed-fi xml interchange' do
      @student_enrollment.match('<StudentUniqueStateId>43</StudentUniqueStateId>').should_not be_nil
      @student_enrollment.match('<StateOrganizationId>elem-0000000065</StateOrganizationId>').should_not be_nil
      @student_enrollment.match('<BeginDate>2005-09-06</BeginDate>').should_not be_nil
      @student_enrollment.match('<UniqueSectionCode>sctn-0025600128</UniqueSectionCode>').should_not be_nil
    end
  end

  describe '--> creating graduation plans' do
    it 'will write a GraduationPlan to ed-fi xml interchange' do
      @student_enrollment.match('<GraduationPlanType>Standard</GraduationPlanType>').should_not be_nil
      @student_enrollment.match('<Credit>36</Credit>').should_not be_nil
      @student_enrollment.match('<Credit>9</Credit>').should_not be_nil
      @student_enrollment.match('<Credit>12</Credit>').should_not be_nil
      @student_enrollment.match('<Credit>15</Credit>').should_not be_nil
      @student_enrollment.match('<SubjectArea>English</SubjectArea>').should_not be_nil
      @student_enrollment.match('<SubjectArea>Science</SubjectArea>').should_not be_nil
      @student_enrollment.match('<SubjectArea>Math</SubjectArea>').should_not be_nil
    end
  end
end
