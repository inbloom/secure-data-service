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
require_relative 'enum/GradeLevelType'
require_relative '../data_utility'

# creates student school association
class StudentSchoolAssociation < BaseEntity
  attr_accessor :student, :school, :start_date, :grade, :exit_date, :exit_type, :gradPlan

  def initialize(student, schoolId, start_date, grade, gradPlan = nil, exit_withdraw_date = nil, exit_type = nil)
    exit -1 if grade.nil?

    @student    = student
    @school     = DataUtility.get_school_id(schoolId, GradeLevelType.school_type(grade))
    @start_date = start_date
    @grade      = GradeLevelType.to_string(grade)
    @exit_date  = exit_withdraw_date
    @exit_type  = exit_type
    @gradPlan   = gradPlan
  end
end
