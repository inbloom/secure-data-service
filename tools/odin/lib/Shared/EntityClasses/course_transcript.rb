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

require_relative 'baseEntity.rb'
require_relative '../data_utility'
require_relative 'enum/MethodCreditEarnedType.rb'
require_relative 'enum/CourseRepeatCodeType.rb'

# creates course transcript
class CourseTranscript < BaseEntity
  attr_accessor :student_id, :ed_org_id, :result, :credits_earned, :course_id, 
    :session, :grade_level, :final_letter_grade, :final_numeric_grade, :course_ed_org_id,
    :method_credit_earned, :course_repeat_code, :credits_attempted, :additional_credits_earned

  def initialize(student_id, school_id, course_id, session, grade, final_grade = nil, result = "Pass", credits_earned = 3)
    @student_id = student_id
    @ed_org_id = school_id
    @result = result
    @credits_earned = credits_earned
    @course_id = DataUtility.get_course_unique_id course_id
    @rand = Random.new(@student_id ^ @course_id)
    @course_ed_org_id = (@@scenario['COURSES_ON_SEA'] && session['edOrgId']) || school_id
    @session = session

    if (optional?)
      @credits_attempted = @result == "Pass" ? @credits_earned : 3
      @additional_credits_earned = 0
      @grade_level = GradeLevelType.to_string(grade)
      @method_credit_earned = MethodCreditEarnedType.to_string(:CLASSROOM_CREDIT)
      @final_numeric_grade = final_grade && final_grade.number_grade ? final_grade.number_grade : 87
      @final_letter_grade = final_grade && final_grade.letter_grade ? final_grade.letter_grade : "B+"
      @course_repeat_code = CourseRepeatCodeType.to_string(:REPEAT_COUNTED)
    end
  end

end
