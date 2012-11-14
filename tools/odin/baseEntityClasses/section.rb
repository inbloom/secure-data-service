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

require 'mustache'
require_relative './baseEntity.rb'

class Section < BaseEntity

  def initialize(id, school, course, session, program, rand)
    @id = id
    @school = school
    @course = course
    @session = session
    @rand = rand
  end

  def id
    @id
  end 

  def sequence
    @rand.rand(8) + 1
  end

  def environment
    choose(["Classroom",
            "Homebound",
            "Hospital class",
            "In-school suspension",
            "Laboratory",
            "Mainstream (Special Education)",
            "Off-school center",
            "Pull-out class",
            "Resource room",
            "Self-contained (Special Education)",
            "Self-study",
            "Shop"])
  end

  def medium 
    choose(["Televised",
            "Telepresence/video conference",
            "Videotaped/prerecorded video",
            "Other technology-based instruction",
            "Technology-based instruction in classroom",
            "Correspondence instruction",
            "Face-to-face instruction",
            "Virtual/On-line Distance learning",
            "Center-based instruction",
            "Independent study",
            "Internship",
            "Other"])
  end

  def population
    choose(["Students",
            "Bilingual Students",
            "Compensatory/Remedial Education Students",
            "Gifted and Talented Students",
            "Career and Technical Education Students",
            "Special Education Students",
            "ESL Students",
            "Adult Basic Education Students",
            "Honors Students",
            "Migrant Students"])
  end

  def courseCode
    @course
  end

  def edOrgId
    @school
  end

  def session
    @session
  end

  def program
    @program
  end

end
