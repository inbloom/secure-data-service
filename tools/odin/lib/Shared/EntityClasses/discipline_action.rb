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

require_relative 'baseEntity'

# creates discipline action
class DisciplineAction < BaseEntity
  attr_accessor :action_id, :disciplines, :date, :students, :incidents, :staff_id, :ed_org_id

  def initialize(student_id, ed_org_id, incident)
    @action_id = "#{incident.incident_identifier},#{student_id}"
    @disciplines = ["DI#{incident.index}"]
    @date = incident.date + 1
    @students = [student_id]
    @incidents = [incident]
    @ed_org_id = ed_org_id
  end
end
