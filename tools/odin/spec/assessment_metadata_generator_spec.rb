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
require_relative '../lib/OutputGeneration/XML/assessment_metadata_generator'
require_relative '../lib/OutputGeneration/XML/validator'
require 'factory_girl'

describe 'AssessmentMetadataGenerator' do
  let(:path) { File.join( "#{File.dirname(__FILE__)}/", "../generated/InterchangeAssessmentMetadata.xml" ) }
  let(:interchange) { File.open( path, 'w')}
  let(:generator) {AssessmentMetadataGenerator.new(get_spec_scenario(), interchange)}
  let(:assessment) {FactoryGirl.build(:assessment)}
  let(:assessment_item) {FactoryGirl.build(:assessment_item)}

  describe '<<' do
    it 'will write an assessment to edfi' do

      generator.start()

      generator << assessment

      generator << assessment_item
      generator.finalize()

      validate_file( path ).should be true

    end
  end
end

