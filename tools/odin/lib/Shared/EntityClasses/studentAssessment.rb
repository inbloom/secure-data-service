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

class StudentAssessment < BaseEntity
  attr_accessor :studentId, :assessment, :date, :score,
                :administrationEndDate, :serialNumber, :specialAccommodations,
                :linguisticAccommodations, :reasonNotTested,
                :gradeLevelWhenAssessed, :performanceLevels
  
  
  def initialize(student_id, assessment, date, rand = nil)
    @rand = Random.new(student_id.hash)
    @studentId = student_id
    @assessment = assessment
    @date = date.to_s
    @score = rand.rand(100) unless rand.nil?
    
    optional {@administrationEndDate = Date.new(2000+@rand.rand(12), 1+@rand.rand(12), 1+@rand.rand(28))}
    
    optional {@serialNumber = @studentId.to_s + " code"}
    
    optional {@specialAccommodations = choose([
      "Presentation",
      "Response",
      "Setting",
      "Timing and Scheduling",
      "Large Print",
      "Dyslexia Bundled",
      "Oral Administration",
      "Adjustable swivel arm"
      ])
    }
    
    optional {@linguisticAccommodations = choose([
      "Bilingual Dictionary",
      "English Dictionary",
      "Reading Aloud - Word or Phrase",
      "Reading Aloud - Entire Test Item",
      "Oral Translation - Word or Phrase",
      "Clarification - Word or Phrase",
      "Linguistic Accommodations allowed but not used",
      "Linguistic Simplification"
      ])
    }
    
    optional {@reasonNotTested = choose([
      "Absent",
      "LEP exempt",
      "LEP postponement",
      "Not appropriate (ARD decision)",
      "Not tested (ARD decision)",
      "Alternate assessment administered",
      "Parental waiver",
      "Foreign exchange student waiver",
      "Refusal by parent"
      ])
    }

    optional {@gradeLevelWhenAssessed = choose([
      "Adult Education",
      "Early Education",
      "Eighth grade",
      "Eleventh grade",
      "Fifth grade",
      "First grade",
      "Fourth grade",
      "Grade 13",
      "Infant/toddler",
      "Kindergarten"
      ])
    }

    optional {@performanceLevels = {
      :codeValue => @studentId.to_s + " code",
      :performanceLevelMet => choose([false, true])
    }}

    
  end
end
