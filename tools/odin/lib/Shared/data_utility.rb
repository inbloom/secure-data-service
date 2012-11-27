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

require_relative "EntityClasses/enum/GradeLevelType.rb"

# Data Utility class
# -> helper class for ed-fi entity and interchange generators
class DataUtility

  #-----------   INTERCHANGE: STUDENT PARENT   ------------
  # create a student unique state id
  def self.get_student_unique_state_id(id)
    "sdnt-" + pad_id_with_zeroes(id, 10)
  end

  # create a parent unique state id
  def self.get_parent_unique_state_id(id)
    "prnt-" + pad_id_with_zeroes(id, 10)
  end
  #-----------   INTERCHANGE: STUDENT PARENT   ------------

  #-------   INTERCHANGE: EDUCATION ORGANIZATION   --------
  # create state education agency's state organization id
  def self.get_state_education_agency_id(id)
    "stte-" + pad_id_with_zeroes(id, 10)
  end

  # create local education agency's state organization id
  def self.get_local_education_agency_id(id)
    "locl-" + pad_id_with_zeroes(id, 10)
  end

  # create elementary school's state organization id
  def self.get_elementary_school_id(id)
  	"elem-" + pad_id_with_zeroes(id, 10)
  end

  # create middle school's state organization id
  def self.get_middle_school_id(id)
  	"midl-" + pad_id_with_zeroes(id, 10)
  end

  # create high school's state organization id
  def self.get_high_school_id(id)
  	"high-" + pad_id_with_zeroes(id, 10)
  end

  # create course's unique id
  def self.get_course_unique_id(id)
    "crse-" + pad_id_with_zeroes(id, 10)
  end

  # get course title
  def self.get_course_title(grade, subject)
    GradeLevelType.get(grade) + " " + subject
  end
  #-------   INTERCHANGE: EDUCATION ORGANIZATION   --------

  #----------   INTERCHANGE: STAFF ASSOCIATION   ----------
  # create a staff unique state id
  def self.get_staff_unique_state_id(id)
    "stff-" + pad_id_with_zeroes(id, 10)
  end

  # create a teacher unique state id
  def self.get_teacher_unique_state_id(id)
    "tech-" + pad_id_with_zeroes(id, 10)
  end  
  #----------   INTERCHANGE: STAFF ASSOCIATION   ----------

  #-----------   INTERCHANGE: MASTER SCHEDULE   -----------
  def self.get_course_offering_code(id)
    "cofr-" + pad_id_with_zeroes(id, 10)
  end

  def self.get_unique_section_id(id)
    "sctn-" + pad_id_with_zeroes(id, 10)
  end
  #-----------   INTERCHANGE: MASTER SCHEDULE   -----------

  def self.pad_id_with_zeroes(id, num_zeroes)
  	id.to_s.rjust(num_zeroes, '0')
  end
end