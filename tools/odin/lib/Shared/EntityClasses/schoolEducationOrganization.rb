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

require_relative "../data_utility.rb"
require_relative "baseEntity.rb"
require_relative "enum/GradeLevelType.rb"

# creates school
class School < BaseEntity

  attr_accessor :state_org_id,
    :org_category,
    :grades,
    :parent_id,
    :address,
    :programs,
    :schoolType,
    :charterStatus,
    :titleIPartASchoolDesignation,
    :magnetSpecialProgramEmphasisSchool,
    :administrativeFundingControl

  def initialize(id, parent_id, type, programs = nil)
    @rand = Random.new(id.hash)
    @id = id
    if parent_id.kind_of? String
      @parent_id = parent_id
    else
      @parent_id = DataUtility.get_local_education_agency_id(parent_id)
    end
    @address   = get_address
    @type      = type
    @grades    = []
    if @type == "elementary"
      if id.kind_of? String
        @state_org_id = id
      else
        @state_org_id = DataUtility.get_elementary_school_id(@id)
      end
      GradeLevelType.elementary.each { |level| @grades << GradeLevelType.to_string(level) }
    elsif @type == "middle"
      if id.kind_of? String
        @state_org_id = id
      else
        @state_org_id = DataUtility.get_middle_school_id(@id)
      end
      GradeLevelType.middle.each { |level| @grades << GradeLevelType.to_string(level) }
    else
      if id.kind_of? String
        @state_org_id = id
      else
        @state_org_id = DataUtility.get_high_school_id(@id)
      end
      GradeLevelType.high.each { |level| @grades << GradeLevelType.to_string(level) }
    end
    @programs = programs
    @org_category = "School"

    optional {@schoolType = choose([
      "Alternative",
      "Regular",
      "Special Education",
      "Vocational",
      "JJAEP",
      "DAEP"])
    }
  
    optional {@charterStatus = choose([
      "School Charter",
      "College/University Charter",
      "Open Enrollment",
      "Not a Charter School"])
    }
    
    optional {@titleIPartASchoolDesignation = choose([
      "Not designated as a Title I Part A school",
      "Title I Part A Schoolwide Assistance Program School",
      "Title I Part A Targeted Assistance School",
      "Title I targeted eligible school - no program",
      "Title I targeted school",
      "Title I school wide eligible - Title I targeted program",
      "Title I school wide eligible school - no program"])
    }
  
    optional {@magnetSpecialProgramEmphasisSchool = choose([
      "All students participate",
      "No students participate",
      "Some, but not all, students participate"])
    }
  
    optional {@administrativeFundingControl = choose([
      "Public School",
      "Private School",
      "Other"])
    }

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

    
  # generates the address
  def get_address
        address = {}
        begin
            address[:line_one] = @rand.rand(1000).to_s + " " + DataUtility.select_random_from_options(@rand, BaseEntity.demographics['street'])
            address[:city] = BaseEntity.demographics['city']
            address[:state] = BaseEntity.demographics['state']
            address[:postal_code] = BaseEntity.demographics['postalCode']
            rescue NameError
            # occurs when @@d in BaseEntity hasn't been initialized (will happen during testing)
            return nil
        end
        address
  end
    
end
