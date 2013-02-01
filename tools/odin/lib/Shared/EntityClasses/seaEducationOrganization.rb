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
  
  attr_accessor :state_org_id, :programs

  def initialize(rand, id, programs = nil)
    if id.kind_of? String
      @state_org_id = id
    else
      @state_org_id = DataUtility.get_state_education_agency_id(id)
    end
    @rand     = rand
    @programs = programs
  end

end
