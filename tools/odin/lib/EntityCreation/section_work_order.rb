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

class SectionWorkOrder
  @@next_id = 0

  def self.reset
    @@next_id = 0
  end

  def self.gen_sections(ed_org, ed_org_type, yaml, yielder)
    students_per_section = students_per_section(ed_org_type, yaml)
    school_id = DataUtility.get_school_id(ed_org['id'], ed_org_type.to_sym)
    unless ed_org['students'].nil?
      ed_org['students'].each{|year, student_map|
        unless ed_org['offerings'].nil?
          offering_map = ed_org['offerings'][year].group_by{|a| a['grade']}
          student_map.each{|grade, num|
            offerings = offering_map[grade].cycle
            (num.to_f/students_per_section).ceil.times{
              yielder.yield SectionWorkOrder.new(@@next_id, school_id, offerings.next)
              @@next_id += 1
            }
          }
        end
      }
    end
  end

  def initialize(id, school_id, offering)
    @id = id
    @school_id = school_id
    @offering = offering
  end

  def build(writer)
    writer.create_section(@id, @school_id, @offering)
  end

  private

  def self.students_per_section(type, yaml)
    case type
    when "elementary"
      yaml['AVERAGE_ELEMENTARY_STUDENTS_PER_SECTION']
    when "middle"
      yaml['AVERAGE_MIDDLE_SCHOOL_STUDENTS_PER_SECTION']
    when "high"
      yaml['AVERAGE_HIGH_SCHOOL_STUDENTS_PER_SECTION']
    end
  end

end
