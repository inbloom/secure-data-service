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

require_relative 'spec_helper'
require_relative '../lib/OutputGeneration/XML/studentAttendanceGenerator'

describe "StudentAttendanceGenerator" do
  let(:scenario) {{}}
  let(:output) {StringIO.new('', 'w')}
  let(:generator) {StudentAttendanceGenerator.new(scenario, output)}
  let(:event) {AttendanceEvent.new(100, "student123", "school123", "2012", Date.new(2012, 12, 20), :ABSENT, nil, nil, "excused: sick")}
  describe "<<" do
    it "will output an Attendance Event to ed-fi xml interchange" do
      generator.start
      generator << event
      generator.finalize
      output.string.should match(/<SchoolYear>2012-2013<\/SchoolYear>/)
      output.string.should match(/<EventDate>2012-12-20<\/EventDate>/)
      output.string.should match(/<AttendanceEventType>Daily Attendance<\/AttendanceEventType>/)
      output.string.should match(/<AttendanceEventCategory>Absence<\/AttendanceEventCategory>/)
      output.string.should match(/<AttendanceEventReason>excused: sick<\/AttendanceEventReason>/)
      output.string.should match(/<EducationalEnvironment>Classroom<\/EducationalEnvironment>/)
      output.string.should match(/<StudentUniqueStateId>student123<\/StudentUniqueStateId>/)
      output.string.should match(/<StateOrganizationId>school123<\/StateOrganizationId>/)
    end
  end
end
