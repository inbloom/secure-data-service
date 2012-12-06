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
require_relative '../lib/OutputGeneration/XML/studentAssessmentGenerator'

describe "StudentAssessmentGenerator" do
  let(:scenario) {{}}
  let(:output) {StringIO.new('', 'w')}
  let(:generator) {StudentAssessmentGenerator.new(scenario, output)}
  describe "<<" do
    it "will output a Student Assessment to edfi" do
      assessment = StudentAssessment.new("student42", "assessment64", Date.new(2012, 12, 21))
      generator.start
      generator << assessment
      generator.finalize
      output.string.should match(/<StudentUniqueStateId>student42<\/StudentUniqueStateId>/)
      output.string.should match(/<AssessmentTitle>assessment64<\/AssessmentTitle>/)
      output.string.should match(/<Result>[0-9]*<\/Result>/)
    end
  end
end

