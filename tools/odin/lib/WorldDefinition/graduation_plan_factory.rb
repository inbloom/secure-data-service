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

class GraduationPlanFactory
  def initialize(ed_org_id, scenario)
    @ed_org_id = ed_org_id
    @scenario = scenario
  end

  def build
    plans = @scenario["GRADUATION_PLANS"]
    unless plans.nil?
      plans.map{|plan_type, credits_by_subject|
        GraduationPlan.new(plan_type, credits_by_subject, @ed_org_id)
      }
    end or []
  end

end
