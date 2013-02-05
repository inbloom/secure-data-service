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
require_relative '../lib/Shared/EntityClasses/enum/AcademicSubjectType.rb'
require_relative '../lib/Shared/EntityClasses/enum/GradeLevelType.rb'
require_relative '../lib/Shared/EntityClasses/enum/ProgramAssignmentType.rb'
require_relative '../lib/OutputGeneration/XML/staffAssociationGenerator'
require_relative '../lib/OutputGeneration/XML/validator'

# specifications for staff association interchange generator
describe 'StaffAssociationInterchangeGenerator' do
  before (:all) do
    @path = File.join( "#{File.dirname(__FILE__)}/", "../generated/InterchangeStaffAssociation.xml" )
    interchange = File.open(@path, 'w')
    @generator = StaffAssociationGenerator.new(get_spec_scenario(), interchange)
    @generator.start
    @generator << Staff.new(1, 1969)
    @generator << Staff.new("cgray", 1973, "Charles Gray")
    @generator << StaffEducationOrgAssignmentAssociation.new(1, 1, :PRINCIPAL, "Principal", Date.new(2009, 9, 4))
    @generator << Teacher.new(1, 1974)
    @generator << Teacher.new("linda.kim", 1972, "Linda Kim")
    @generator << TeacherSchoolAssociation.new("linda.kim", 2, :REGULAR_EDUCATION, [:EIGHTH_GRADE], [:SCIENCE])
    @generator.finalize
    @staff_association = File.open("#{File.dirname(__FILE__)}/../generated/InterchangeStaffAssociation.xml", "r") { |file| file.read }
  end

  describe '--> ed-fi xml interchange creation' do
    it 'will pass validation' do
      validate_file( @path ).should be true
    end
  end

  describe '--> request to create staff using numeric id' do
    it 'will write a staff member to ed-fi xml interchange' do
      @staff_association.match('<StaffUniqueStateId>stff-0000000001</StaffUniqueStateId>').should_not be_nil
      @staff_association.match('<BirthDate>1969-05-25</BirthDate>').should_not be_nil
    end
  end

  describe '--> request to create staff using string id and name' do
    it 'will write a staff member to ed-fi xml interchange' do
      @staff_association.match('<StaffUniqueStateId>cgray</StaffUniqueStateId>').should_not be_nil
      @staff_association.match('<BirthDate>1973-07-28</BirthDate>').should_not be_nil
    end
  end

  describe '--> request to create staff education organization assignment asssociation' do
    it 'will write a staff education organization assignment association to ed-fi xml interchange' do
      @staff_association.match('<StaffEducationOrgAssignmentAssociation>').should_not be_nil
      @staff_association.match('<StateOrganizationId>1</StateOrganizationId>').should_not be_nil
      @staff_association.match('<StaffClassification>Principal</StaffClassification>').should_not be_nil
      @staff_association.match('<PositionTitle>Principal</PositionTitle>').should_not be_nil
      @staff_association.match('<BeginDate>2009-09-04</BeginDate>').should_not be_nil
    end
  end

  describe '--> request to create teacher using numeric id' do
    it 'will write a teacher to ed-fi xml interchange' do
      @staff_association.match('<StaffUniqueStateId>tech-0000000001</StaffUniqueStateId>').should_not be_nil
      @staff_association.match('<BirthDate>1974-05-25</BirthDate>').should_not be_nil
    end
  end

  describe '--> request to create teacher using string id and name' do
    it 'will write a teacher to ed-fi xml interchange' do
      @staff_association.match('<StaffUniqueStateId>linda.kim</StaffUniqueStateId>').should_not be_nil
      @staff_association.match('<BirthDate>1972-02-18</BirthDate>').should_not be_nil
    end
  end

  describe '--> request to create teacher school association' do
    it 'will write a teacher school association to ed-fi xml interchange' do
      @staff_association.match('<TeacherSchoolAssociation>').should_not be_nil
      @staff_association.match('<StateOrganizationId>2</StateOrganizationId>').should_not be_nil
      @staff_association.match('<ProgramAssignment>Regular Education</ProgramAssignment>').should_not be_nil
      @staff_association.match('<GradeLevel>Eighth grade</GradeLevel>').should_not be_nil
      @staff_association.match('<AcademicSubject>Science</AcademicSubject>').should_not be_nil
    end
  end
end

