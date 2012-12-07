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

require_relative '../Shared/EntityClasses/enum/GradeLevelType'
require_relative '../Shared/EntityClasses/assessment'

class AssessmentFactory

  def initialize(scenario)
    @scenario = scenario
  end

  #get a list of assessments
  def assessments(opts = {})
    grade = GradeLevelType.get((opts[:grade] or :UNGRADED))
    year = opts[:year]
    section = opts[:section]
    if section.nil?
      grade_wide_assessments(grade, year)
    else
      [] #TODO implement section specific assessments
    end
  end

  def grade_wide_assessments(grade, year)
    n = @scenario['ASSESSMENTS_PER_GRADE']
    (1..n).map{|i|
      Assessment.new("#{year}-#{grade} Assessment #{i}", year, grade)
    }
  end

end
