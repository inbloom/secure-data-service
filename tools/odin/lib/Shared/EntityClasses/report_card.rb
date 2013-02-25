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

# creates report card
class ReportCard < BaseEntity

  attr_accessor :student, :grades, :grading_period, :gpa_given_grading_period, :gpa_cumulative, :student_competencies,
                :numberOfDaysAbsent, :numberOfDaysInAttendance, :numberOfDaysTardy

  def initialize(student, grades, grading_period, student_competencies)
    @rand = Random.new(student.hash + grading_period.hash)
    @student = student
    @grades = grades
    @grading_period = grading_period
    @gpa_given_grading_period = get_gpa(grades)
    @gpa_cumulative = @gpa_given_grading_period
    @student_competencies = student_competencies
  
    optional {@numberOfDaysAbsent = @rand.rand(30)}
    optional {@numberOfDaysInAttendance = 50 + @rand.rand(100)}
    optional {@numberOfDaysTardy = @rand.rand(30)}

  end
  
  def get_gpa(grades)
    return 0.0 if grades.empty?
    gpa = 1.0 * grades.collect{|grade| get_points(grade.letter_grade[0])}.inject(:+) / grades.size
    gpa.round(1)
  end

  def get_points(letter_grade)
    {
      "A"  => 4,
      "B"  => 3,
      "C"  => 2,
      "D"  => 1,
      "F"  => 0
    }[letter_grade]
  end
end
