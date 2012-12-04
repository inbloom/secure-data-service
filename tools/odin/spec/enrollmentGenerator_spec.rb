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

describe 'EnrollmentGenerator' do
  let(:interchange) {StringIO.new('', 'w')}
  let(:generator) {EnrollmentGenerator.new(get_spec_scenario(), interchange)}
  let(:school_assoc) {StudentSchoolAssociation.new(42, 64, 2004, :FIRST_GRADE)}
  let(:section_assoc) {StudentSectionAssociation.new(42, {'id' => 128}, 64, 2004, :FIRST_GRADE)}
  describe '<<' do
    it 'will write a studentSchoolAssociation to edfi' do
      generator << school_assoc
      interchange.string.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      interchange.string.match('<StateOrganizationId>elem-0000000064</StateOrganizationId>').should_not be_nil
      interchange.string.match('<EntryDate>2004-09-01</EntryDate>').should_not be_nil
      interchange.string.match('<EntryGradeLevel>First grade</EntryGradeLevel>').should_not be_nil
    end

    it 'will write a studentSectionAssociation to edfi' do
      generator << section_assoc
      interchange.string.match('<StudentUniqueStateId>42</StudentUniqueStateId>').should_not be_nil
      interchange.string.match('<StateOrganizationId>elem-0000000064</StateOrganizationId>').should_not be_nil
      interchange.string.match('<BeginDate>2004-09-01</BeginDate>').should_not be_nil
      interchange.string.match('<UniqueSectionCode>128</UniqueSectionCode>').should_not be_nil
    end
  end
end
