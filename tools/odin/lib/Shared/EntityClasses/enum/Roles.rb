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

require_relative 'Enum.rb'

class Roles
  include Enum

  Roles.define :ART_THERAPIST, "Art Therapist"
  Roles.define :ASSISTANT_PRINCIPAL, "Assistant Principal"
  Roles.define :ASSISTANT_SUPERINTENDENT, "Assistant Superintendent"
  Roles.define :ATHLETIC_TRAINER, "Athletic Trainer"
  Roles.define :CERTIFIED_INTERPRETER, "Certified Interpreter"
  Roles.define :COUNSELOR, "Counselor"
  Roles.define :HIGH_SCHOOL_COUNSELOR, "High School Counselor"
  Roles.define :INSTRUCTIONAL_AIDE, "Instructional Aide"
  Roles.define :INSTRUCTIONAL_COORDINATOR, "Instructional Coordinator"
  Roles.define :LEA_ADMINISTRATIVE_SUPPORT_STAFF, "LEA Administrative Support Staff"
  Roles.define :LEA_ADMINISTRATOR, "LEA Administrator"
  Roles.define :LEA_SPECIALIST, "LEA Specialist"
  Roles.define :LEA_SYSTEM_ADMINISTRATOR, "LEA System Administrator"
  Roles.define :LIBRARIAN, "Librarian"
  Roles.define :LIBRARIANS_MEDIA_SPECIALISTS, "Librarians/Media Specialists"
  Roles.define :OTHER, "Other"
  Roles.define :PHYSICAL_THERAPIST, "Physical Therapist"
  Roles.define :PRINCIPAL, "Principal"
  Roles.define :SCHOOL_ADMINISTRATIVE_SUPPORT_STAFF, "School Administrative Support Staff"
  Roles.define :SCHOOL_ADMINISTRATOR, "School Administrator"
  Roles.define :SCHOOL_LEADER, "School Leader"
  Roles.define :SCHOOL_NURSE, "School Nurse"
  Roles.define :SCHOOL_SPECIALIST, "School Specialist"
  Roles.define :SPECIALIST_CONSULTANT, "Specialist/Consultant"
  Roles.define :STUDENT_SUPPORT_SERVICES_STAFF, "Student Support Services Staff"
  Roles.define :SUBSTITUTE_TEACHER, "Substitute Teacher"
  Roles.define :SUPERINTENDENT, "Superintendent"
  Roles.define :TEACHER, "Teacher"
end
