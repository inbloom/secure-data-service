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

require_relative 'baseEntity'

# creates an discipline incident
class DisciplineIncident < BaseEntity
  attr_accessor :incident_identifier, :date, :time, :location, :behaviors,
    :school_id, :staff_id

  def initialize(id, section_id, school, staff, interval, location, behaviors = nil)
    rand = Random.new(id + section_id * 10)
    @incident_identifier = DisciplineIncident.gen_id(id, section_id)
    @date = interval.random_day(rand)
    eight_hours = 8 * 60 * 60
    @time = (@date.to_time + eight_hours + rand.rand(eight_hours)).strftime("%H:%M:%S")
    @school_id = school
    @staff_id = staff
    @location = location
    @behaviors = behaviors || [DisciplineIncident.gen_behavior(id, section_id)]
  end

  def self.gen_behavior(id, section_id)
    "BE#{(id + section_id * 10) % @@scenario['BEHAVIORS'].count}"
  end

  def self.gen_id(id, section_id)
    "#{section_id}##{id}"
  end

end
