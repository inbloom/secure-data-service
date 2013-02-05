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

# creates learning objective
class LearningObjective < BaseEntity

  attr_accessor :objective, :academic_subject, :objective_grade_level

  def initialize(objective, academic_subject, objective_grade_level)
    @objective = objective
    @academic_subject = academic_subject
    @objective_grade_level = objective_grade_level
  end

  def self.build_learning_objectives(count, academic_subject, objective_grade_level)
    (1..count).collect{|x| LearningObjective.new("Generic Learning Objective #{x}", academic_subject, objective_grade_level)}
  end
end
