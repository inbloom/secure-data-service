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
  attr_accessor :assessments_per_grade, :item_counts

  def initialize(scenario)
    @scenario = scenario
    @assessments_per_grade = @scenario['ASSESSMENTS_PER_GRADE']
    @item_counts = @scenario['ASSESSMENT_ITEMS_PER_ASSESSMENT']
    @rand = Random.new(123456789)
  end

  #get a list of assessment work orders
  def gen_assessments(yielder, opts = {})
    grade = (opts[:grade] or :UNGRADED)
    year = opts[:year]
    section = opts[:section]
    if section.nil?
      grade_wide_assessments(grade, year).each{|a|
        yielder.yield(a)
      }
    else
      [] #TODO implement section specific assessments
    end
  end

  def grade_wide_assessments(grade, year, family = nil)
    item_count = DataUtility.rand_float_to_int(@rand, @item_counts['GRADE_WIDE_ASSESSMENTS'])
    (1..@assessments_per_grade).map{|i|
      Assessment.new("#{year}-#{GradeLevelType.to_string(grade)} Assessment #{i}", year, grade, item_count, family)
    }
  end

end

class GradeWideAssessmentWorkOrder

  def initialize(grade, year, gen_parent, factory)
    @grade = grade
    @year = year
    @factory = factory
    @parent_family = AssessmentFamily.new("#{year} Standard", year)
    @gen_parent = gen_parent
  end

  def build
    generated = []
    generated << @parent_family if @gen_parent
    family = AssessmentFamily.new("#{@year} #{GradeLevelType.to_string(@grade)} Standard", @year, @parent_family)
    generated << family
    assessments = @factory.grade_wide_assessments(@grade, @year, family)
    generated += assessments
    assessments.each{|assessment|
      generated += assessment.assessment_items
    }
    generated
  end
end
