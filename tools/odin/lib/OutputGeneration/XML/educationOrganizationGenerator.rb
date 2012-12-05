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

require "mustache"

require_relative "./EntityWriter"
require_relative "interchangeGenerator.rb"
require_relative "../../Shared/data_utility.rb"
require_relative "../../Shared/util.rb"

Dir["#{File.dirname(__FILE__)}/../EntityClasses/*.rb"].each { |f| load(f) }

# event-based education organization interchange generator
class EducationOrganizationGenerator < InterchangeGenerator

  # initialization will define the header and footer for the education organization interchange
  # leaves file handle open for event-based writing of ed-fi entities
  def initialize(yaml, interchange)
    super(yaml, interchange)

    @header, @footer = build_header_footer("EducationOrganization")

    @writers[SeaEducationOrganization] = EntityWriter.new("state_education_organization.mustache")
    @writers[LeaEducationOrganization] = EntityWriter.new("local_education_organization.mustache")
    @writers[SchoolEducationOrganization] = EntityWriter.new("school.mustache")
    @writers[Course] = EntityWriter.new("course.mustache")
    @writers[Program] = EntityWriter.new("program.mustache")
  end

  # creates and writes state education agency to interchange
  def create_state_education_agency(rand, id)
    self.<< SeaEducationOrganization.new(id, rand)
  end

  # creates and writes local education agency to interchange
  def create_local_education_agency(rand, id, parent_id)
    self.<< LeaEducationOrganization.new(id, parent_id, rand)
  end

  # creates and writes school to interchange
  def create_school(rand, id, parent_id, type)
    self.<< SchoolEducationOrganization.new(rand, id, parent_id, type)
  end

  # creates and writes course to interchange
  def create_course(rand, id, title, ed_org_id)
    self.<< Course.new(id, title, ed_org_id)
  end

  # creates and writes program to interchange
  def create_program(rand, id)
    self.<< Program.new(id.to_s, prng)
  end
end
