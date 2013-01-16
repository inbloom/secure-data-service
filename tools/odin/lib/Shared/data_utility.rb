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

require "logger"

require_relative "EntityClasses/enum/AcademicSubjectType.rb"
require_relative "EntityClasses/enum/GradeLevelType.rb"

# Data Utility class
# -> helper class for ed-fi entity and interchange generators
class DataUtility

  #-------   INTERCHANGE: EDUCATION ORGANIZATION   --------
  # create state education agency's state organization id
  def self.get_state_education_agency_id(id)
    id
  end

  # create local education agency's state organization id
  def self.get_local_education_agency_id(id)
    id
  end

  # create elementary school's state organization id
  def self.get_elementary_school_id(id)
    id
  end

  # create middle school's state organization id
  def self.get_middle_school_id(id)
    id
  end

  # create high school's state organization id
  def self.get_high_school_id(id)
    id
  end

  # create course's unique id
  def self.get_course_unique_id(id)
    id
  end

  # get course title
  def self.get_course_title(grade, subject)
    GradeLevelType.to_string(grade) + " " + subject
  end

  #-------   INTERCHANGE: EDUCATION ORGANIZATION   --------

  #----------   INTERCHANGE: STAFF ASSOCIATION   ----------
  # create a staff unique state id
  def self.get_staff_unique_state_id(id)
    return id if id.kind_of? String
    "stff-" + pad_id_with_zeroes(id, 10)
  end

  # create a teacher unique state id
  def self.get_teacher_unique_state_id(id)
    return id if id.kind_of? String
    "tech-" + pad_id_with_zeroes(id, 10)
  end  
  #----------   INTERCHANGE: STAFF ASSOCIATION   ----------

  #-----------   INTERCHANGE: MASTER SCHEDULE   -----------
  # create a course offering code
  def self.get_course_offering_code(id)
    id
  end

  # create a unique section id
  def self.get_unique_section_id(id)
    id
  end
  #-----------   INTERCHANGE: MASTER SCHEDULE   -----------
  #-----------   INTERCHANGE: STUDENT PROGRAM   -----------

  # create a program id
  def self.get_program_id(id)
    id
  end

  #-----------   INTERCHANGE: STUDENT PROGRAM   -----------

  # returns a randomly selected grade for the specified school 'type'
  def self.get_random_grade_for_type(prng, type)
    return select_random_from_options(prng, GradeLevelType.elementary) if type == "elementary"
    return select_random_from_options(prng, GradeLevelType.middle)     if type == "middle"
    return select_random_from_options(prng, GradeLevelType.high)       if type == "high"
    return nil
  end

  # returns randomly selected academic subjects for the specified school 'type'
  def self.get_random_academic_subjects_for_type(prng, type)
    subjects = []
    if type == "elementary"
      while subjects.size < 1
        subjects << select_random_from_options(prng, AcademicSubjectType.elementary)
      end
    elsif type == "middle"
      while subjects.size < 2
        subject  = select_random_from_options(prng, AcademicSubjectType.middle)
        subjects << subject unless subjects.include? subject
      end
    elsif type == "high"
      while subjects.size < 3
        subject  = select_random_from_options(prng, AcademicSubjectType.high)
        subjects << subject unless subjects.include? subject
      end
    end
    subjects
  end

  # create the id for the school based on the given type
  def self.get_school_id(id, type)
    id
  end

  def self.pad_id_with_zeroes(id, num_zeroes)
    id.to_s.rjust(num_zeroes, '0')
  end

  # selects a random object from the list of options
  def self.select_random_from_options(prng, options)
    options[prng.rand(options.size) - 1]
  end

  # randomly selects a subset of the specified 'choices' that will have size equal to the specified number of choices
  # -> if num is larger than size of choices, choices array will be returned (no error condition raised)
  def self.select_num_from_options(prng, num, choices)
    return []      if choices.nil?
    return choices if num >= choices.size
    subset = []
    while subset.size < num
      choice = select_random_from_options(prng, choices)
      subset << choice unless subset.include?(choice)
    end
    subset
  end
end
