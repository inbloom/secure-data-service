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
require 'json'
class StudentParentAssociation < BaseEntity
  
  attr_accessor :studentId, :parentId, :relation, :primaryContactStatus, :livesWith,
    :emergencyContactStatus, :contactPriority, :contactRestrictions

  def initialize(kid, type)
    @studentId = kid.id
    @parentId = Parent.parentId(kid, type)
    @type = type
    @rand = kid.rand
    buildAssociation
  end

  def buildAssociation
    @relation = @type == :mom ? 'Mother' : 'Father'
    @primaryContactStatus = bit_choose
    @livesWith = bit_choose

    @emergencyContactStatus = bit_choose
    @contactPriority =    @rand.rand(4)

    restrictions = [nil, 'No pickup', 'Custody on Mondays, Wednesdays, Fridays only' ]
    @contactRestrictions = choose ( restrictions )
  end
end
