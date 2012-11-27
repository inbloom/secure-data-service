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

require_relative 'baseEntity.rb'

class Program < BaseEntity
  
  def initialize(id, rand)
    @id = id
    @rand = rand
  end

  def id
    @id
  end

  def type
    choose(["Adult/Continuing Education",
            "Alternative Education",
            "Athletics",
            "Bilingual",
            "Bilingual Summer",
            "Career and Technical Education",
            "Cocurricular Programs",
            "College Preparatory",
            "Community Service Program",
            "Community/Junior College Education Program",
            "Compensatory Services for Disadvantaged Students",
            "Counseling Services",
            "District-Funded GED",
            "English as a Second Language (ESL)",
            "Even Start",
            "Expelled Education",
            "Extended Day/Child Care Services",
            "Gifted and Talented",
            "Head Start",
            "Health Services Program",
            "High School Equivalency Program (HSEP)",
            "IDEA",
            "Immigrant Education",
            "Independent Study",
            "Indian Education",
            "International Baccalaureate",
            "Library/Media Services Program",
            "Magnet/Special Program Emphasis",
            "Migrant Education",
            "Neglected and Delinquent Program",
            "Optional Flexible School Day Program (OFSDP)",
            "Other",
            "Regular Education",
            "Remedial Education",
            "Section 504 Placement",
            "Service Learning",
            "Special Education",
            "Student Retention/Dropout Prevention",
            "Substance Abuse Education/Prevention",
            "Teacher Professional Development/Mentoring",
            "Technical Preparatory",
            "Title I Part A",
            "Vocational Education"])
  end

  def sponsor
    choose(["Federal",
            "State Education Agency",
            "Education Service Center",
            "Local Education Agency",
            "School",
            "Private Organization"])
  end

  def service
    "Service for program" + id
  end
end
