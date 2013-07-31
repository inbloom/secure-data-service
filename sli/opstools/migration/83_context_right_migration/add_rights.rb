#!/usr/bin/ruby
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
STUDENT_RIGHTS = ['READ_STUDENT_GENERAL','WRITE_STUDENT_GENERAL','READ_STUDENT_OWNED','WRITE_STUDENT_OWNED','READ_STUDENT_RESTRICTED','WRITE_STUDENT_RESTRICTED']

def add_context_right(role, context_right)
  rights = role['rights']
  self_rights = role['selfRights']
  is_student = false
  exists = false
  if rights != nil
    rights.each do |right|
      if right.eql?('STAFF_CONTEXT') || right.eql?('TEACHER_CONTEXT')
        exists = true
      end
      if STUDENT_RIGHTS.include? right
        is_student = true
      end
    end

    if self_rights != nil
      self_rights.each do |self_right|
        if STUDENT_RIGHTS.include? self_right
          is_student = true
        end
      end
    end

    if !exists && !is_student
      rights << context_right
    end

  else
    @log.info "No rights for custom role #{custom_role['_id']}, skipping"
  end
end