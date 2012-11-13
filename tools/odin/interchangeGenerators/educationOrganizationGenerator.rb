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

require_relative "./interchangeGenerator.rb"
Dir["#{File.dirname(__FILE__)}/../baseEntityClasses/*.rb"].each { |f| load(f) }

class EducationOrganizationGenerator < InterchangeGenerator
  def initialize
    @header = <<-HEADER
<?xml version="1.0"?>
<InterchangeEducationOrganization xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/domain/src/main/resources/edfiXsd-SLI/SLI-Interchange-EducationOrganization.xsd ">
HEADER
    @footer = <<-FOOTER
</InterchangeEducationOrganization>
FOOTER
  end

  def write(prng, yamlHash)
    File.open("generated/InterchangeEducationOrganization.xml", 'w') do |f|
      f.write(@header)
      for seaId in 0..yamlHash['numSEA']-1 do
        sea = SeaEducationOrganization.new seaId, prng
        f.write(sea.render)
        for leaId in 0..yamlHash['numLEA']-1 do
          lea = LeaEducationOrganization.new leaId, seaId, prng
          f.write(lea.render)
          for schoolId in 0..(1.0*yamlHash['studentCount']/yamlHash['studentsPerSchool']).ceil - 1 do
            school = SchoolEducationOrganization.new schoolId, leaId, prng
            f.write(school.render)
          end
        end
      end

      for id in programs(yamlHash)
        f.write Program.new(id.to_s, prng).render
      end

      f.write(@footer)
    end
  end
  
end

def programs(yaml)
  (0..yaml['numPrograms'])
end
