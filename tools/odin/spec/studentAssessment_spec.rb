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
require_relative '../lib/OutputGeneration/XML/studentAssessmentGenerator'

describe "StudentAssessmentGenerator" do
  let(:scenario) {{}}
  let(:output) {StringIO.new('', 'w')}
  let(:generator) {StudentAssessmentGenerator.new(scenario, output)}
  let(:assessment) {FactoryGirl.build(:assessment)}
  let(:assessment_item) {FactoryGirl.build(:assessment_item)}
  let(:student_assessment) {StudentAssessment.new("student42", assessment, Date.new(2012, 12, 21))}
  let(:student_assessment_item) {StudentAssessmentItem.new(true, student_assessment, assessment_item)}
  describe "<<" do
    it "will output a Student Assessment to edfi" do
      generator.start
      generator << student_assessment
      generator.finalize
      output.string.should match(/<AdministrationDate>2012-12-21<\/AdministrationDate>/)
      output.string.should match(/<SchoolYear>2012-2013<\/SchoolYear>/)
      output.string.should match(/<StudentUniqueStateId>student42<\/StudentUniqueStateId>/)
      output.string.should match(/<AssessmentTitle>SAT II - US History<\/AssessmentTitle>/)
      output.string.should match(/<GradeLevelAssessed>Twelfth grade<\/GradeLevelAssessed>/)
      output.string.should match(/<Result>[0-9]*<\/Result>/)
    end
    it "will output a student assessment item to edfi" do
      generator.start
      generator << student_assessment_item
      generator.finalize
      output.string.should match(/<AssessmentResponse>true<\/AssessmentResponse>/)
      output.string.should match(/<AssessmentItemResult>Incorrect<\/AssessmentItemResult>/)
      output.string.should match(/<AdministrationDate>2012-12-21<\/AdministrationDate>/)
      output.string.should match(/<SchoolYear>2012-2013<\/SchoolYear>/)
      output.string.should match(/<StudentUniqueStateId>student42<\/StudentUniqueStateId>/)
      output.string.should match(/<AssessmentTitle>SAT II - US History<\/AssessmentTitle>/)
      output.string.should match(/<GradeLevelAssessed>Twelfth grade<\/GradeLevelAssessed>/)
    end
  end
end

