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

# creates a student discipline association
class StudentDisciplineIncidentAssociation < BaseEntity
  attr_accessor :incident, :behaviors, :student, :ed_org_id, :participation
  def initialize(student_id, discipline_id, section_id, school, behaviors = nil)
    @incident = DisciplineIncident.gen_id(discipline_id, section_id)
    @behaviors = behaviors || [DisciplineIncident.gen_behavior(discipline_id, section_id)]
    @student = student_id
    @ed_org_id = school
    @participation = "Perpetrator"
  end
end
