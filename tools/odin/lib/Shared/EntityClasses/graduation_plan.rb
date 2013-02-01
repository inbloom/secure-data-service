
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

require_relative 'baseEntity'
class GraduationPlan < BaseEntity
  attr_accessor :type, :individual, :total_credits, :subjects, :ed_org_id
  
  def initialize(type, credits_by_subject, edOrgId)
    @type = type
    @individual = false
    @subjects = credits_by_subject.map{|subject, credits| {subject: subject, credits: credits}}
    @total_credits = credits_by_subject.values.inject(:+)
    @ed_org_id = edOrgId
  end
end
