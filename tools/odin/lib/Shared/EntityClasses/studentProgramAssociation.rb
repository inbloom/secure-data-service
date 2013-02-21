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

require_relative '../data_utility.rb'
require_relative 'baseEntity.rb'

# creates a student program association
class StudentProgramAssociation < BaseEntity
  
  attr_accessor :student, :program, :begin_date, :ed_org_id, :end_date,
                :services, :reasonExited

  def initialize(student, program, ed_org_id, begin_date, end_date = nil)
    @rand = Random.new(student.hash + program.hash)
    @student    = student
    @program    = program
    @ed_org_id  = ed_org_id
    @begin_date = begin_date
    @end_date   = end_date

   optional {@services = {
     :description => "Reading Intervention",
     }
   }

    optional {@reasonExited = choose([
      "Transfer to regular education",
      "Received a certificate",
      "Reached maximum age",
      "Discontinued schooling"])
    }

  end
end
