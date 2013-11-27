=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0-

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'rubygems'
require 'mongo'
require 'fileutils'
require 'socket'
require 'net/sftp'
require 'net/http'
require 'rest-client'
require 'rbconfig'

require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../odin/step_definitions/data_generation_steps'
require_relative '../../../security/step_definitions/securityevent_util_steps.rb'

Then /^in tenant "(.*?)", I should see an attendance record with id "(.*?)" containing "(.*?)" attendance events matching event type "(.*?)", date "(.*?)" and section id "(.*?)", containing event reason "(.*?)" and educational environment "(.*?)"$/ do |tenant, attendance_id, expected_count, event_type, date, section_id, reason, environment|
  compare_attendance_events_counts(tenant, attendance_id, expected_count, event_type, date, section_id, reason, environment,)
end

Then /^in tenant "(.*?)", I should see an attendance record with id "(.*?)" containing "(.*?)" attendance events matching event type "(.*?)", date "(.*?)" and section id "(.*?)", without event reason or educational environment$/ do |tenant, attendance_id, expected_count, event_type, date, section_id|
  compare_attendance_events_counts(tenant, attendance_id, expected_count, event_type, date, section_id, reason = nil, environment = nil)
end

Then /^in tenant "(.*?)", I should see an attendance record with id "(.*?)" containing "(.*?)" attendance events matching event type "(.*?)", date "(.*?)" without a section id, containing event reason "(.*?)" and educational environment "(.*?)"$/ do |tenant, attendance_id, expected_count, event_type, date, reason, environment|
  compare_attendance_events_counts(tenant, attendance_id, expected_count, event_type, date, section_id = nil, reason, environment)
end

def compare_attendance_events_counts(tenant, attendance_id, expected_count, event_type, date, section_id = nil, reason = nil, environment = nil)
   disable_NOTABLESCAN()
   @db = @conn[convertTenantIdToDbName(tenant)]
   @coll = @db['attendance']
   attendance_events = @coll.find({'_id' => attendance_id}).to_a.reduce(Hash.new, :update)["body"]['attendanceEvent']
   actual_count = 0
   attendance_events.each do |attendance_event|
     if attendance_event['sectionId'] == section_id && attendance_event['event'] == event_type && attendance_event['date'] == date && attendance_event['reason'] == reason && attendance_event['educationalEnvironment'] == environment
       actual_count += 1
       puts attendance_event.to_s
     end
   end
   assert(expected_count.to_i==actual_count, "Count mismatch. Actual count is #{actual_count}" + attendance_events.to_s)
   enable_NOTABLESCAN()
end