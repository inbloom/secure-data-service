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

require_relative '../data_utility.rb'
require_relative 'staff.rb'

# creates teacher (sub-class of staff)
class Teacher < Staff

  attr_accessor :teacher_unique_state_id, :highly_qualified_teacher

  def initialize(id, year_of, name = nil)
    super(id, year_of, name)
    @id = DataUtility.get_teacher_unique_state_id(id) if id.kind_of? Integer
    @id = id if id.kind_of? String
    @teacher_unique_state_id = @id
    @highly_qualified_teacher = wChoose(BaseEntity.demographics['highly_qualified_teacher'])
  end

end
