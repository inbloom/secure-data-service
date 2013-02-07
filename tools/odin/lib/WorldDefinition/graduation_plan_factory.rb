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

require_relative '../Shared/EntityClasses/graduation_plan'

# factory for creating graduation plans
class GraduationPlanFactory

  def initialize(ed_org_id, scenario)
    @ed_org_id = ed_org_id
    @scenario = scenario
  end

  def build
    plans   = @scenario["GRADUATION_PLANS"]
    courses = @scenario["TWELFTH_GRADE_COURSES"]
    unless plans.nil?
      plans.map { |plan_type, credits_by_subject| 
        credits_by_course = {}
        courses.each { |course| credits_by_course[course] = 12 }
        GraduationPlan.new(plan_type, @ed_org_id, credits_by_subject, credits_by_course) 
      }
    end or []
  end

end
