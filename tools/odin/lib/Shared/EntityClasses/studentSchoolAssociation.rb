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
  attr_accessor :student, :school, :start_date, :grade, :exit_date, :exit_type, :gradPlan,
                :schoolYear, :entryType, :repeatGradeIndicator, :classOf, :schoolChoiceTransfer, :educationalPlans

  def initialize(student, schoolId, start_date, grade, gradPlan = nil, exit_withdraw_date = nil, exit_type = nil)
    exit -1 if grade.nil?

    @rand = Random.new(student.hash + schoolId.hash)
    @student    = student
    @school     = DataUtility.get_school_id(schoolId, GradeLevelType.school_type(grade))
    @start_date = start_date
    @grade      = GradeLevelType.to_string(grade)
    @exit_date  = exit_withdraw_date
    @exit_type  = exit_type
    @gradPlan   = gradPlan

    optional {@schoolYear = choose([
       "2013-2014",
       "2014-2015",
       "2015-2016",
       "2016-2017",
       "2017-2018",
       "2018-2019",
       "2019-2020"])
     }

    optional {@entryType = choose([
      "Transfer from a public school in the same local education agency",
      "Transfer from a private, non-religiously-affiliated school in a different local education agency in the same state",
      "Transfer from a school outside of the country",
      "Re-entry from the same school with no interruption of schooling",
      "Original entry into a United States school",
      "Original entry into a United States school from a foreign country with no interruption in schooling",
      "Other"])
    }

    optional {@repeatGradeIndicator = {:b => choose([false, true])}}

    optional {@classOf = choose([
      "2013-2014",
      "2014-2015",
      "2015-2016",
      "2016-2017",
      "2017-2018",
      "2018-2019",
      "2019-2020"])
    }

    optional {@schoolChoiceTransfer = {:b => choose([false, true])}}

    optional {@educationalPlans = choose([
      "504 Plan",
      "Career Pathways",
      "Career Suggestions",
      "Completion and Reach Age 22",
      "Full Time Employment",
      "Employability Skills",
      "High School Education Plan",
      "IDEA IEP",
      "Outside Service Access",
      "Personal Graduation Plan",
      "Student Success Plan"])
    }

  end
end
