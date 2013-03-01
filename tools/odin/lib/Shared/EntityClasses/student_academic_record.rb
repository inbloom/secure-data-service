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
  attr_accessor :student_id, :school_year, :report_card, :cumulative_credits, :session

  def initialize(student_id, session, report_card)
    @student_id = student_id
    @session = session
    @report_card = report_card
    @school_year = session['year'].to_s + "-" + (session['year'] + 1).to_s
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
