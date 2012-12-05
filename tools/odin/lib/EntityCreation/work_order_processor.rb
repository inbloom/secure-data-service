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

require 'set'

require_relative 'student_work_order'
require_relative 'section_work_order'

# class for processing student work orders
class WorkOrderProcessor
  
  def initialize(writer, scenario)
    @data_writer = writer
    @scenario = scenario
  end

  # builds the specified work order by calling its native 'build' method
  def build(work_order)
    work_order.build(@data_writer)
  end

  # uses the input writer and a snapshot of the 'world' to generate student work orders
  # -> data writer is used to initialize work order processor (for output of generated entities)
  # -> world       is used to generate student work orders to be processed
  def self.run(world,  writer, scenario)
    processor = WorkOrderProcessor.new(writer, scenario)
    for work_order in generate_work_orders(world, scenario) do
      processor.build(work_order)
    end
  end

  # uses the snapshot of the 'world' to generate student work orders
  def self.generate_work_orders(world, scenario)
    next_student = 0
    SectionWorkOrder.reset
    Enumerator.new do |y|
      world.each{|type, edOrgs|
        edOrgs.each{|edOrg|
          SectionWorkOrder.gen_sections(edOrg, type, scenario, y)
          students = edOrg['students']
          next_student = StudentWorkOrder.generate_work_orders(students, edOrg, next_student, y) unless students.nil?
        }
      }
    end
  end
end
