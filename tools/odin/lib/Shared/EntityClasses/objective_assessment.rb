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

require_relative 'baseEntity'
require_relative 'enum/AssessmentReportingMethodType'

class ObjectiveAssessment < BaseEntity

  attr_accessor :identificationCode, :maxRawScore, :assessmentPerformanceLevel, :percentOfAssessment, 
      :nomenclature, :learning_objectives, :child_objective_assessment

  def initialize(identificationCode, maxRawScore, grade_assessed = :UNGRADED, generate_child = false, academic_subject = nil)
    @identificationCode = identificationCode
    @rand = Random.new(int_value(@identificationCode))
    @percentOfAssessment = @maxRawScore = maxRawScore
    @nomenclature = "Nomenclature"
    @assessmentPerformanceLevel = [{codeValue: "code1", 
      performanceLevelMet: "true",
      assessmentReportingMethod: AssessmentReportingMethodType.to_string(:NUMBER_SCORE),
      minScore: 0,
      maxScore: @maxRawScore
    }]

    unless grade_assessed == :UNGRADED
      num_objectives = (@@scenario["NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE"] or 2)
      subject = academic_subject || choose(AcademicSubjectType.get_academic_subjects(grade_assessed))
      @learning_objectives = LearningObjective.build_learning_objectives(num_objectives, subject, grade_assessed)
    end

    if (generate_child) 
      @child_objective_assessment = ObjectiveAssessment.new("#{@identificationCode} Sub", @maxRawScore, grade_assessed, subject)
    end 
  end

end
