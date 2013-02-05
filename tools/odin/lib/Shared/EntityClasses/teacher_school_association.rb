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

require_relative 'baseEntity.rb'
require_relative '../data_utility'
require_relative 'enum/AcademicSubjectType.rb'
require_relative 'enum/GradeLevelType.rb'
require_relative 'enum/ProgramAssignmentType.rb'

# creates teacher school association
class TeacherSchoolAssociation < BaseEntity

  attr_accessor :teacher_id, :school_id, :program_assignment, :grades, :subjects

  def initialize(teacher_id, school_id, program_assignment, grades, subjects)
    @teacher_id = teacher_id
    @school_id = school_id
    @program_assignment = ProgramAssignmentType.to_string(program_assignment)
    @grades = grades.collect { |grade| GradeLevelType.to_string(grade) }
    @subjects = subjects.collect { |subject| AcademicSubjectType.to_string(subject) }
  end
  
end
