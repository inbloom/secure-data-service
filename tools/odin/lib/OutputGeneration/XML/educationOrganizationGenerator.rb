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

require_relative "./EntityWriter"
require_relative "./interchangeGenerator"
require_relative "../../Shared/util"

# event-based education organization interchange generator
class EducationOrganizationGenerator < InterchangeGenerator

  # initialization will define the header and footer for the education organization interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(yaml, interchange)
    super(yaml, interchange)

    @header, @footer = build_header_footer("EducationOrganization")

    @writers[ StateEducationAgency ] = EntityWriter.new("state_education_organization.mustache")
    @writers[ LocalEducationAgency ] = EntityWriter.new("local_education_organization.mustache")
    @writers[ School ] = EntityWriter.new("school.mustache")
    @writers[ Course ] = EntityWriter.new("course.mustache")
    @writers[ Program ] = EntityWriter.new("program.mustache")
    @writers[ CompetencyLevelDescriptor ] = EntityWriter.new("competency_level_descriptor.mustache")
  end

end
