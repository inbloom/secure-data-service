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

require_relative "./EntityWriter"
require_relative "./interchangeGenerator"
require_relative "../../Shared/util"

# event-based student attendace interchange generator 
class StudentGradeGenerator < InterchangeGenerator

  # initialization will define the header and footer for the student attendance interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(yaml, interchange)
    super(yaml, interchange)

    @header, @footer = build_header_footer( "StudentGrade" )    
    #@writers[ CompetencyLevelDescriptor ] = EntityWriter.new("competency_level_descriptor.mustache")
    @writers[ CourseTranscript ] = EntityWriter.new("course_transcript.mustache")
    @writers[ Grade ] = EntityWriter.new("grade.mustache")
    @writers[ GradebookEntry ] = EntityWriter.new("gradebook_entry.mustache")
    #@writers[ LearningObjective ] = EntityWriter.new("learning_objective.mustache")
    @writers[ ReportCard ] = EntityWriter.new("report_card.mustache")
    @writers[ StudentAcademicRecord ] = EntityWriter.new("student_academic_record.mustache")
    @writers[ StudentCompetency ] = EntityWriter.new("student_competency.mustache")
    #@writers[ StudentCompetencyObjective ] = EntityWriter.new("student_competency_objective.mustache")
    @writers[ StudentGradebookEntry ] = EntityWriter.new("student_gradebook_entry.mustache")
  end
end
