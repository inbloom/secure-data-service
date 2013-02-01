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

require_relative "baseEntity.rb"

# creates student grade book entry
class StudentGradebookEntry < BaseEntity

  attr_accessor :date_fulfilled, :letter_grade_earned, :numeric_grade_earned, :student_section_association, :gradebook_entry
  
  def initialize(date_fulfilled, letter_grade_earned, numeric_grade_earned, student_section_association, gradebook_entry)
    @date_fulfilled              = date_fulfilled
    @letter_grade_earned         = letter_grade_earned
    @numeric_grade_earned        = numeric_grade_earned
    @student_section_association = student_section_association
    @gradebook_entry             = gradebook_entry
  end
end
