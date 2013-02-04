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
class StaffProgramAssociation < BaseEntity
  
  attr_accessor :staff, :program, :begin_date, :end_date, :access

  def initialize(staff, program, begin_date, access = false, end_date = nil)
    @staff      = staff
    @program    = program
    @begin_date = begin_date
    @end_date   = end_date
    @access     = access
  end
end
