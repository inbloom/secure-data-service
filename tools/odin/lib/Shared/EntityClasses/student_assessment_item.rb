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

require_relative 'baseEntity'
class StudentAssessmentItem < BaseEntity
  attr_accessor :response, :result, :student_assessment, :assessment_item,
                :responseIndicator, :rawScoreResult

  def initialize(response, student_assessment, assessment_item)
    @rand = Random.new(student_assessment.hash + assessment_item.hash)
    @response = response
    @result = response == assessment_item.correctResponse ? "Correct" : "Incorrect"
    @student_assessment = student_assessment
    @assessment_item = assessment_item

    optional {@responseIndicator = choose([
      "Nonscorable response",
      "Ineffective response",
      "Effective response",
      "Partial response"])
    }

    optional {@rawScoreResult = @rand.rand(100)+1}

  end
end
