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

require 'set'

require_relative '../WorldDefinition/student_work_order'
require_relative '../WorldDefinition/section_work_order'

# class for processing student work orders
class WorkOrderProcessor

  # uses the snapshot of the 'world' to generate student work orders
  def self.generate_work_orders(world, scenario, prng)
    section_factory = SectionWorkOrderFactory.new(world, scenario, prng)
    student_factory = StudentWorkOrderFactory.new(world, scenario, section_factory, nil)
    Enumerator.new do |y|
      world.each{|type, edOrgs|
        edOrgs.each{|edOrg|
          section_factory.generate_sections_with_teachers(edOrg, type, y)
          student_factory.generate_work_orders(edOrg, y)
        }
      }
    end
  end
end
