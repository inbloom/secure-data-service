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

require_relative "baseEntity.rb"

# creates cohort
class Cohort < BaseEntity
  attr_accessor :identifier, :description, :type, :scope, :subject, :ed_org_id, :programs
  def initialize(id, ed_org_id, classification, program_id_count, opts = {})
    @identifier = id
    @ed_org_id = ed_org_id
    @classification = classification
    @program_id_count = program_id_count
    @opts = opts
  end

  def build
    @rand = Random.new(@identifier)
    @description = (@opts[:description] or "Cohort #{@identifier} at Edorg #{@ed_org_id}")
    @type = (@opts[:type] or
      choose(["Academic Intervention",
              "Attendance Intervention",
              "Discipline Intervention",
              "Classroom Pullout",
              "Extracurricular Activity",
              "Field Trip",
              "Principal Watch List",
              "Counselor List",
              "In-school Suspension",
              "Study Hall",
              "Other"]))
    @scope = (@opts[:scope] or
      choose(["District",
              "School",
              "Classroom",
              "Teacher",
              "Principal",
              "Counselor",
              "Statewide"]))
    @subject = (@opts[:subject] or nil)
    @programs = (@opts[:programs] or [])

    if(optional? && @subject.nil? && @programs.empty? && !@classification.nil? && !@program_id_count.nil?)
      @subject = AcademicSubjectType.to_string(choose(AcademicSubjectType.send(@classification)))
      @programs = [rand(@program_id_count)]
    end
    self
  end

end
