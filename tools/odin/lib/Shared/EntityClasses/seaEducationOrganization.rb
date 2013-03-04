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

# creates state education agency
class StateEducationAgency < BaseEntity
  
  attr_accessor :state_org_id, :programs,
                :educationOrgIdentificationCode, :shortNameOfInstitution, :telephone,
                :webSite, :operationalStatus, :accountabilityRatings, :educationOrganizationPeerReference

  def initialize(rand, id, programs = nil)
    if id.kind_of? String
      @state_org_id = id
    else
      @state_org_id = DataUtility.get_state_education_agency_id(id)
    end
    @rand     = Random.new(state_org_id.hash)
    @programs = programs

    optional {@educationOrgIdentificationCode = @state_org_id.to_s + " ID code"}

    optional {@shortNameOfInstitution = @state_org_id.to_s + " shortName"}

    optional {@telephone = "(" + @rand.rand(1000).to_s.rjust(3, '0') + ")555-" + @rand.rand(10000).to_s.rjust(4, '0')}

    optional {@webSite = "www." + @state_org_id.to_s + ".org"}

    optional {@operationalStatus = choose([
      "Active",
      "Added",
      "Changed Agency",
      "Closed",
      "Continuing",
      "Future",
      "Inactive",
      "New",
      "Reopened"])}

    optional {@accountabilityRatings = {
      :ratingTitle => choose(["School Rating", "Safety Score"]),
      :rating => choose(["Good", "Bad", "Ugly"]),
      :ratingDate => Date.new(2000+@rand.rand(12), 1+@rand.rand(12), 1+@rand.rand(28)),
      :schoolYear => choose([
        "2009-2010",
        "2010-2011",
        "2011-2012",
        "2012-2013",
        "2013-2014",
        "2014-2015",
        "2015-2016"]),
      :ratingOrganization => choose(["Rating Org 1", "RO #2"]),
      :ratingProgram => choose(["NCLB", "Another Rating Program"]),
    }}

    optional {@educationOrganizationPeerReference = {
        :stateOrganizationId => @state_org_id.to_s + " peer ref"
      }
    }
  
  end

end
