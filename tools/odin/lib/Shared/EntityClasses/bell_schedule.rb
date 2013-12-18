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

class BellSchedule < BaseEntity

  attr_accessor :ed_org_id, :calendar_date, :class_period_name, :start_time, :end_time

  def initialize(ed_org_id, calendar_date, class_period_name, start_time, end_time)
    # raise [ ed_org_id, calendar_date, class_period_name, start_time, end_time ].to_s()
    @ed_org_id = ed_org_id
    @calendar_date = calendar_date
    @class_period_name = class_period_name
    @start_time = start_time
    @end_time = end_time
  end
  
  # Bell schedule name
  def name
    "Bell Schedule"
  end

  # Grade levels
  def grade_levels
    [ "Early Education" ]
  end  

end
