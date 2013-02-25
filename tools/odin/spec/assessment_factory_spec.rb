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
require_relative '../lib/WorldDefinition/assessment_work_order'
require_relative '../lib/Shared/EntityClasses/assessment_family'

describe "AssessmentFactory" do
  describe "#assessments" do
    context "with a scenario calling for 5 grade wide assessments" do
      let(:scenario) {{'ASSESSMENTS_PER_GRADE' => 5, 'ASSESSMENT_ITEMS_PER_ASSESSMENT' =>  {'GRADE_WIDE_ASSESSMENTS' => 4}}}
      let(:factory) {AssessmentFactory.new scenario}

      it "will return 5 unique assessments for the third grade in 2012" do
        assessments = []
        e = Enumerator.new do |y|
          factory.gen_assessments(y, grade: :THIRD_GRADE, year: 2012)
        end

        e.each do |work_order|
            assessments << work_order
        end

        assessments.should have(5).items
        Set.new(assessments.map{|a| a.id}).should have(5).items
        assessments.each{|a|
          a.year_of.should eq 2012
          a.gradeLevelAssessed.should eq "Third grade"
        }
      end

      it "will return a different set of assessments for different grades" do
        titles = Set.new
        GradeLevelType.get_ordered_grades.each{|g|
          e = Enumerator.new do |y|
            factory.gen_assessments(y, grade: g, year: 2012)
          end

          e.each do |work_order|
            titles << work_order.assessmentTitle
          end
        }
        titles.should have(5*GradeLevelType.get_ordered_grades.count).items
      end

      it "will return a different set of assessments for different years" do
        titles = Set.new
        years = 2001..2012
        years.each{|year|
          e = Enumerator.new do |y|
            factory.gen_assessments(y, grade: :FIRST_GRADE,  year: year)
          end

          e.each do |assessment|
            titles << assessment.assessmentTitle
          end
        }
        titles.should have(5*years.count).items
      end
    end
  end
  describe "GradeWideAssessmentWorkOrder" do
    describe "#build" do
      let(:scenario) {{'ASSESSMENTS_PER_GRADE' => 5, 'ASSESSMENT_ITEMS_PER_ASSESSMENT' =>  {'GRADE_WIDE_ASSESSMENTS' => 4}}}
      let(:factory) {AssessmentFactory.new scenario}
      let(:order) {GradeWideAssessmentWorkOrder.new(:THIRD_GRADE, 2002, true, factory)}
      let(:entities) {order.build.group_by(&:class)}
      it "will generate the parent family" do
        family = entities[AssessmentFamily][0]
        family.assessmentFamilyTitle.should eq "2002 Standard"
        family.year_of.should eq 2002
      end
      it "will generate the year/grade family" do
        family = entities[AssessmentFamily][1]
        family.assessmentFamilyTitle.should eq "2002 Third grade Standard"
        family.assessmentFamilyReference.assessmentFamilyTitle.should eq "2002 Standard"
        family.year_of.should eq 2002
      end
      it "will generate the configured number of assessments" do
        entities[Assessment].should have(5).items
      end
      it "will generate assessments with the correct grade level assessed" do
        entities[Assessment].each{|assessment| assessment.gradeLevelAssessed.should eq "Third grade"}
      end
      it "will generate the configured number of assessment items for each assessment" do
        entities[AssessmentItem].should have(4 * entities[Assessment].count).items
      end
      it "will generate the configured number of objective assessments for each assessment" do
        assessment = entities[Assessment][0]
        assessment.referenced_objective_assessments.should have(2).items
        assessment.referenced_objective_assessments[0].learning_objectives.should have(5).items
      end
      it "will generate objective assessments that together make up 100% of the assessment" do
        assessment = entities[Assessment][0]
        assessment.referenced_objective_assessments.should have(2).items
        total = assessment.referenced_objective_assessments.inject(0) {|s, obj_assessment| s += obj_assessment.percentOfAssessment}
        total.should eq 100
      end
    
    end
  end
end
