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
require_relative '../lib/EntityCreation/assessment_factory'

describe "AssessmentFactory" do
  describe "#assessments" do
    context "with a scenario calling for 5 grade wide assessments" do
      let(:scenario) {{'ASSESSMENTS_PER_GRADE' => 5}}
      let(:factory) {AssessmentFactory.new scenario}

      it "will return 5 unique assessments for the third grade in 2012" do
        assessments = factory.assessments(grade: :THIRD_GRADE, year: 2012)
        assessments.should have(5).items
        Set.new(assessments.map{|a| a.assessmentTitle}).should have(5).items
        assessments.each{|a|
          a.year_of.should eq 2012
          a.gradeLevelAssessed.should eq "Third grade"
        }
      end

      it "will return a different set of assessments for different grades" do
        titles = Set.new
        GradeLevelType.get_ordered_grades.each{|g|
          titles += factory.assessments(grade: g, year: 2012).map{|a| a.assessmentTitle}
        }
        titles.should have(5*GradeLevelType.get_ordered_grades.count).items
      end

      it "will return a different set of assessments for different years" do
        titles = Set.new
        years = 2001..2012
        years.each{|y|
          titles += factory.assessments(grade: :THIRD_GRADE, year: y).map{|a| a.assessmentTitle}
        }
        titles.should have(5*years.count).items
      end
    end
  end
end
