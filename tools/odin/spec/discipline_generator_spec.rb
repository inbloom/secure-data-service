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
require_relative '../lib/OutputGeneration/XML/disciplineGenerator'
require_relative '../lib/OutputGeneration/XML/validator'
require 'factory_girl'

describe 'DisciplineGenerator' do
  let(:path) { File.join( "#{File.dirname(__FILE__)}/", "../generated/InterchangeStudentDiscipline.xml" ) }
  let(:interchange) { File.open( path, 'w')}
  let(:generator) { DisciplineGenerator.new(get_spec_scenario(), interchange) }
  let(:student_incident) { FactoryGirl.build(:student_discipline_incident_association) }
  let(:incident) { FactoryGirl.build(:discipline_incident) }
  let(:discipline_action) { FactoryGirl.build(:discipline_action) }

  describe '<<' do
    it 'will write a student discipline interchange to edfi' do

      generator.start()

      generator << incident
      generator << student_incident
      generator << discipline_action

      generator.finalize()

      validate_file( path ).should be true

    end
  end
end

