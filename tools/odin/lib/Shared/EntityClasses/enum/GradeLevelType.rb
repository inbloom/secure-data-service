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

require_relative 'Enum.rb'

# Enumerates each grade level for when a student started at a given school. From Ed-Fi-Core.xsd: 
# <xs:simpleType name="GradeLevelType"> 
#   <xs:annotation>
#     <xs:documentation>The enumeration items for the set of grade levels.</xs:documentation>
#   </xs:annotation> 
#   <xs:restriction base="xs:token">
#     <xs:enumeration value="Adult Education"/> 
#     <xs:enumeration value="Early Education"/> 
#     <xs:enumeration value="Eighth grade"/>
#     <xs:enumeration value="Eleventh grade"/> 
#     <xs:enumeration value="Fifth grade"/> 
#     <xs:enumeration value="First grade"/> 
#     <xs:enumeration value="Fourth grade"/>
#     <xs:enumeration value="Grade 13"/> 
#     <xs:enumeration value="Infant/toddler"/> 
#     <xs:enumeration value="Kindergarten"/>
#     <xs:enumeration value="Ninth grade"/> 
#     <xs:enumeration value="Other"/>
#     <xs:enumeration value="Postsecondary"/> 
#     <xs:enumeration value="Preschool/Prekindergarten"/> 
#     <xs:enumeration value="Second grade"/>
#     <xs:enumeration value="Seventh grade"/> 
#     <xs:enumeration value="Sixth grade"/>
#     <xs:enumeration value="Tenth grade"/> 
#     <xs:enumeration value="Third grade"/>
#     <xs:enumeration value="Transitional Kindergarten"/> 
#     <xs:enumeration value="Twelfth grade"/> 
#     <xs:enumeration value="Ungraded"/> 
#   </xs:restriction>
# </xs:simpleType>
class GradeLevelType
  include Enum

  GradeLevelType.define :ADULT_EDUCATION, "Adult Education"
  GradeLevelType.define :EARLY_EDUCATION, "Early Education"
  GradeLevelType.define :EIGHTH_GRADE, "Eighth grade"
  GradeLevelType.define :ELEVENTH_GRADE, "Eleventh grade"
  GradeLevelType.define :FIFTH_GRADE, "Fifth grade"
  GradeLevelType.define :FIRST_GRADE, "First grade"
  GradeLevelType.define :FOURTH_GRADE, "Fourth grade"
  GradeLevelType.define :GRADE_13, "Grade 13"
  GradeLevelType.define :INFANT_TODDLER, "Infant/toddler"
  GradeLevelType.define :KINDERGARTEN, "Kindergarten"
  GradeLevelType.define :NINTH_GRADE, "Ninth grade"
  GradeLevelType.define :OTHER, "Other"
  GradeLevelType.define :POSTSECONDARY, "Postsecondary"
  GradeLevelType.define :PRESCHOOL_PREKINDERGARTEN, "Preschool/Prekindergarten"
  GradeLevelType.define :SECOND_GRADE, "Second grade"
  GradeLevelType.define :SEVENTH_GRADE, "Seventh grade"
  GradeLevelType.define :SIXTH_GRADE, "Sixth grade"
  GradeLevelType.define :TENTH_GRADE, "Tenth grade"
  GradeLevelType.define :THIRD_GRADE, "Third grade"
  GradeLevelType.define :TRANSITIONAL_KINDERGARTEN, "Transitional Kindergarten"
  GradeLevelType.define :TWELFTH_GRADE, "Twelfth grade"
  GradeLevelType.define :UNGRADED, "Ungraded"

  # returns the set of elementary school grades (K - 5)
  def self.elementary
    [] << :KINDERGARTEN << :FIRST_GRADE << :SECOND_GRADE << :THIRD_GRADE << :FOURTH_GRADE << :FIFTH_GRADE
  end

  # returns the set of middle school grades (6 - 8)
  def self.middle
    [] << :SIXTH_GRADE << :SEVENTH_GRADE << :EIGHTH_GRADE
  end

  # returns the set of high school grades (9 - 12)
  def self.high
    [] << :NINTH_GRADE << :TENTH_GRADE << :ELEVENTH_GRADE << :TWELFTH_GRADE
  end

  # returns a set of ordered grades (K - 12)
  def self.get_ordered_grades
    (elementary << middle << high).flatten
  end

  # returns true if the specified grade is an elementary school grade, false otherwise
  def self.is_elementary_school_grade(grade)
    elementary.include?(grade)
  end

  # returns true if the specified grade is a middle school grade, false otherwise
  def self.is_middle_school_grade(grade)
    middle.include?(grade)
  end

  # returns true if the specified grade is a high school grade, false otherwise
  def self.is_high_school_grade(grade)
    high.include?(grade)
  end

  # decrement from the current grade by num_grades (calls increment with negative number of grades)
  def self.decrement(grade, num_grades = 1)
    increment(grade, -num_grades)
  end

  # increment from the current grade by num_grades
  def self.increment(grade, num_grades = 1)
    ordered = GradeLevelType.get_ordered_grades
    current = ordered.index(grade)
    if current != nil
      new_grade = current + num_grades
      case
      when new_grade < 0
        nil
      when new_grade > 12
        nil
      else ordered[new_grade]
      end
    else grade
    end
  end

  # return what type of school offers this grade
  def self.school_type(grade)
    return :elementary if elementary.include?(grade)
    return :middle if middle.include?(grade)
    return :high if high.include?(grade)
    return nil
  end
end
