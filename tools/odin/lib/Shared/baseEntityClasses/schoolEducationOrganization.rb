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

require_relative "./baseEntity.rb"
require_relative "../data_utility.rb"
require_relative "enum/GradeLevelType.rb"

# creates school
class SchoolEducationOrganization < BaseEntity

  def initialize(rand, id, parent_id, type)
    @rand      = rand
    @id        = id
    @parent_id = parent_id
    @type      = type
    @grades    = []
    if @type == "elementary"
      GradeLevelType.elementary.each do |level|
        @grades << GradeLevelType.get(level)
      end
    elsif @type == "middle"
      GradeLevelType.middle.each do |level|
        @grades << GradeLevelType.get(level)
      end
    else
      GradeLevelType.high.each do |level|
        @grades << GradeLevelType.get(level)
      end
    end
  end

  def stateOrgId
    if @type == "elementary"
      DataUtility.get_elementary_school_id(@id)
    elsif @type == "middle"
      DataUtility.get_middle_school_id(@id)
    elsif @type == "high"
      DataUtility.get_high_school_id(@id)
    end 
  end

  def parentId
    DataUtility.get_local_education_agency_id(@parentId)
  end

  def grades
    @grades
  end

  def type
    if @type == "elementary"
      "Elementary School"
    elsif @type == "middle"
      "Middle School"
    elsif @type == "high"
      "High School"
    end 
  end
end