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

require_relative 'baseEntity.rb'
require_relative '../data_utility'
require_relative 'enum/ClassroomPositionType.rb'

# creates teacher school association
class TeacherSectionAssociation < BaseEntity

  attr_accessor :teacher_id, :section, :position,
                :beginDate, :endDate, :highlyQualifiedTeacher

  def initialize(teacher_id, section_id, school_id, position, beginDate = nil, endDate = nil)
    @rand = Random.new(teacher_id.hash + section_id.hash)
    @teacher_id = teacher_id
    @section = {'unique_section_code'=>DataUtility.get_unique_section_id(section_id), 'school_id'=>school_id}
    @position = ClassroomPositionType.to_string(position)
    
    #optional {@beginDate = Date.new(2013+@rand.rand(1), 1+@rand.rand(12), 1+@rand.rand(28))}
    optional {@beginDate = beginDate}
    #optional {@endDate = Date.new(2013+@rand.rand(2), 1+@rand.rand(12), 1+@rand.rand(28))}
    optional {@endDate = endDate}
    optional {@highlyQualifiedTeacher = {:b => choose([false, true])}}

  end
  
end
