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

# creates a student academic record
class StudentAcademicRecord < BaseEntity
  attr_accessor :student_id, :report_card, :cumulative_credits, :session,
                :cumulativeCreditsEarned, :cumulativeGradePointsEarned,
                :gradeValueQualifier, :classRanking, :academicHonors,
                :recognitions, :projectedGraduationDate, :sessionCreditsEarned,
                :sessionCreditsAttempted, :sessionGradePointsEarned

  def initialize(student_id, session, report_card)
    @rand = Random.new(student_id)
    @student_id = student_id
    @session = session
    @report_card = report_card

    optional {@cumulativeCreditsEarned = @rand.rand(10)/10}

    optional {@cumulativeGradePointsEarned = @rand.rand(10)/10}

    optional {@gradeValueQualifier = choose(["90-100%=A, 80-90%=B", "80-100%=A, 70-80%=B"])}

    optional {@classRanking = {
      :classRank => @rand.rand(30)+1,
      :totalNumberInClass => @rand.rand(30)+1,
      :percentageRanking => @rand.rand(100)+1,
      :classRankingDate => Date.new(2000+@rand.rand(12), 1+@rand.rand(12), 1+@rand.rand(28))
    }}

    optional {@academicHonors = {
      :academicHonorsType => choose([
        "Honor roll",
        "Honor society",
        "Honorable mention",
        "Honors program",
        "Prize awards",
        "Scholarships",
        "Awarding of units of value",
        "Citizenship award/recognition",
        "Completion of requirement, but no units of value awarded",
        "Attendance award",
        "Certificate",
        "Honor award",
        "Letter of student commendation",
        "Medals",
        "National Merit scholar",
        "Points",
        "Promotion or advancement",
        "Other"]),
      :honorsDescription => choose(["Honor Desc a", "Honor Desc BBB"]),
      :honorAwardDate => Date.new(2000+@rand.rand(12), 1+@rand.rand(12), 1+@rand.rand(28))
    }}

    optional {@recognitions = {
      :recognitionType => choose([
        "Athletic awards",
        "Awarding of units of value",
        "Citizenship award/recognition",
        "Completion of requirement, but no units of value awarded",
        "Certificate",
        "Honor award",
        "Letter of commendation",
        "Medals",
        "Monogram/letter",
        "Points",
        "Promotion or advancement",
        "Other"]),
      :recognitionDescription => choose(["Recognition Desc a", "Recognition Desc BBB"]),
      :recognitionAwardDate => Date.new(2000+@rand.rand(12), 1+@rand.rand(12), 1+@rand.rand(28))
    }}

    optional {@projectedGraduationDate = Date.new(2012+@rand.rand(8), 1+@rand.rand(12), 1+@rand.rand(28))}

    optional {@sessionCreditsEarned = @rand.rand(10)/10}

    optional {@sessionCreditsAttempted = @rand.rand(10)/10}

    optional {@sessionGradePointsEarned = @rand.rand(10)/10}

  end

  def ed_org_id
    @session['edOrgId']
  end

  def session_name
    @session['name']
  end

  def session_gpa
    @report_card.gpa_given_grading_period
  end

  def cumulative_gpa
    @report_card.gpa_cumulative
  end
end
