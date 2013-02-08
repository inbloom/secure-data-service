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

Dir["../../Shared/EntityClasses/*.rb"].each {|file| require file }

class EntityFactory

  def initialize(prnd)
    @prnd = prnd
  end

  def create(work_order)
    if work_order.kind_of? Hash then
      type = work_order[:type]

      rval = []

      #
      # Refactor this so that each type can construct itself given a work order. Would clean this up considerably.
      #

      case [type]
        when [StateEducationAgency]
          rval << StateEducationAgency.new(@prnd, work_order[:id], work_order[:programs])

        when [LocalEducationAgency]
          rval << LocalEducationAgency.new(work_order[:id], work_order[:parent], work_order[:programs], work_order[:years])

        when [School]
          rval << School.new(work_order[:id], work_order[:parent], work_order[:classification], work_order[:programs])

        when [Program]
          rval << Program.new(@prnd, work_order[:id], work_order[:program_type], work_order[:sponsor])

        when [Session]
          rval << Session.new(work_order[:name], work_order[:year], work_order[:term], work_order[:interval], work_order[:edOrg], work_order[:gradingPeriods])

        when [GradingPeriod]
          rval << GradingPeriod.new(work_order[:period_type], work_order[:year], work_order[:interval], work_order[:edOrg], work_order[:calendarDates])

        when [CalendarDate]
          rval << CalendarDate.new(work_order[:date], work_order[:event], work_order[:edOrgId])

        when [CourseOffering]
          rval << CourseOffering.new(work_order[:id], work_order[:title], work_order[:edOrgId], work_order[:session], work_order[:course])

        when [Course]
          rval << Course.new(work_order[:id], work_order[:grade], work_order[:title], work_order[:edOrgId])

        when [Section]
          rval << Section.new(work_order[:id], work_order[:edOrg], work_order[:offering])

        when [Staff]
          rval << Staff.new(work_order[:id], work_order[:year], work_order[:name])

        when [StaffEducationOrgAssignmentAssociation]
          rval << StaffEducationOrgAssignmentAssociation.new(work_order[:id], work_order[:edOrg], work_order[:classification], work_order[:title],
                                                             work_order[:beginDate], work_order[:endDate])

        when [StaffProgramAssociation]
          rval << StaffProgramAssociation.new(work_order[:staff], work_order[:program], work_order[:begin_date], work_order[:access], work_order[:end_date])

        when [Teacher]
          rval << Teacher.new(work_order[:id], work_order[:year], work_order[:name])

        when [TeacherSchoolAssociation]
          rval << TeacherSchoolAssociation.new(work_order[:id], work_order[:school], work_order[:assignment], work_order[:grades], work_order[:subjects])

        when [TeacherSectionAssociation]
          rval << TeacherSectionAssociation.new(work_order[:teacher], work_order[:section], work_order[:school], work_order[:position])

        when [GradebookEntry]
          rval << GradebookEntry.new(work_order[:gbe_type], work_order[:date_assigned], work_order[:section], work_order[:description], work_order[:grading_period],  work_order[:learning_objectives])

        else
          puts "factory not found for #{work_order}"
      end
      rval
    else
      work_order.respond_to?('build') ?  work_order.build : [work_order]
    end

  end
end
