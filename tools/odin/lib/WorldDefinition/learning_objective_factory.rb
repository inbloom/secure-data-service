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

require_relative '../Shared/data_utility'
require_relative '../Shared/EntityClasses/learning_objective'
require_relative '../Shared/EntityClasses/enum/AcademicSubjectType'

# factory for creating learning objectives
class LearningObjectiveFactory

  def initialize(scenario)
    @scenario = scenario
  end

  # builds a map of the learning objectives for the specified grade, keyed by academic subject
  # -> returns a hash, in the form { academic subject --> learning objectives }
  def build_learning_objectives(grade)
    learning_objecties_by_subject = {}
    AcademicSubjectType.get_academic_subjects(grade).each do |academic_subject|
      learning_objecties_by_subject[academic_subject] = build_objectives_for_subject(grade, academic_subject)
    end
    learning_objecties_by_subject
  end

  def build_objectives_for_subject(grade, academic_subject)
    count = (@scenario["NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE"] or 2)
    (1..count).collect{|x| LearningObjective.new("#{AcademicSubjectType.to_string(academic_subject)} Objective #{x}", academic_subject, grade)}
  end
end