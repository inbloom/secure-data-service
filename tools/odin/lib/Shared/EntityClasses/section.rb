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

require_relative "../data_utility.rb"
require_relative "baseEntity.rb"
require_relative "enum/EducationalEnvironmentType.rb"
require_relative "enum/MediumOfInstructionType.rb"
require_relative "enum/PopulationServedType.rb"

# creates section
class Section < BaseEntity

  attr_accessor :school_id

  def initialize(id, sequence, environment, medium, population, school_id, course_offering, session, program = nil)
    @id                                      = id
    @sequence                                = sequence
    @environment                             = environment
    @medium                                  = medium
    @population                              = population
    @school_id                               = school_id
    @course_offering                         = Hash.new
    @course_offering["code"]                 = course_offering[""] # probably need to use DataUtility.create_course_offering_id()
    @course_offering["ed_org_id"]            = course_offering[""]
    @course_offering["session"]              = Hash.new
    @course_offering["session"]["name"]      = course_offering["session"]["name"]
    @course_offering["session"]["ed_org_id"] = course_offering["session"]["ed_org_id"]
    @session                                 = Hash.new
    @session["name"]                         = session["name"]
    @session["ed_org_id"]                    = session["ed_org_id"]
    #@program              = program
    # --> programs are not currently implemented
  end

  def unique_section_code
    DataUtility.get_unique_section_id(@id)
  end 

  def sequence
    @sequence
  end

  def environment
    EducationalEnvironmentType.to_string(@environment)
  end

  def medium 
    MediumOfInstructionType.to_string(@medium)
  end

  def population
    PopulationServedType.to_string(@population)
  end

  def course_offering
    @course_offering
  end

  def session
    @session
  end

  def program
    @program
  end
end
