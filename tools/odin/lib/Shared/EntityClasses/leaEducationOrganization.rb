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

require_relative "../data_utility.rb"
require_relative "baseEntity.rb"

# creates local education agency
class LeaEducationOrganization < BaseEntity

  def initialize(id, parent_id, rand)
    @id = id
    @parent_id = parent_id
    @rand = rand
  end

  def stateOrgId
    DataUtility.get_local_education_agency_id(@id)
  end

  def parentId
    DataUtility.get_state_education_agency_id(@parent_id)
  end
end