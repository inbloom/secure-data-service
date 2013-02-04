=begin
#--

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


require 'test_helper'

class EntityTest < ActiveSupport::TestCase

  test "simple value generator with nil" do
    v = Entity.value_for_simple_view(VIEW_CONFIG['teacher'].first, nil)
    assert_nil(v)
  end

  test "simple value generator with nil type" do
    v = Entity.value_for_simple_view(nil, @teacher_fixtures['one'])
    assert_nil(v)
  end

  test "simple value generator with valid" do
    v = Entity.value_for_simple_view(VIEW_CONFIG['teacher'].first, @teacher_fixtures['one'])
    assert_not_nil(v)
    assert_equal(v, '11111111-1111-1111-1111-111111111111')
  end

  test "simple value generator with nested type" do
    v = Entity.value_for_simple_view(VIEW_CONFIG['teacher'][1], @teacher_fixtures['one'])
    assert_not_nil(v)
  end

  test "build simple hash with invalid type" do
    v = Entity.build_simple_hash(nil, @teacher_fixtures['one'])
    assert_not_nil(v)
  end

  test "build simple hash with invalid hash" do
    assert_nil(Entity.build_simple_hash(VIEW_CONFIG['teacher'], nil))
  end

  test "build simple hash with valid data" do
    assert_not_nil(Entity.build_simple_hash(VIEW_CONFIG['teacher'], @teacher_fixtures['one']))
  end
  
  test "try to get address/city from teacher" do
    v = Entity.value_for_simple_view('address/city', @teacher_fixtures['one'])
    assert_not_nil(v)
    assert(v == 'Durham', "City should be Durham.")
    
  end
  
  test "getting keys with type" do
    v = Entity.build_simple_hash(VIEW_CONFIG['teacher'], @teacher_fixtures['one'])
    assert_not_nil(v)
    assert(v.has_key?("firstName"), "Should have 'firstName'")
  end
  
  test "try to get values for invalid entity" do
    v = Entity.value_for_simple_view('address/city', @school_fixtures['one'])
    assert_nil(v)
  end
  
  test "getting basic keys" do
     v = Entity.get_basic_types(@teacher_fixtures['one'])
     assert_not_nil(v)
     assert(v.include?("birthDate"), "Should have birthdate")
     assert(v.include?("sex"), "Should have sex")
     assert(v.size == 5, "Should be 5 of the fields")
   end
  
  test "get students" do
    Entity.url_type = "students/"
    Entity._format = ActiveResource::Formats::JsonLinkFormat
    students = Entity.all
    assert_equal(students.size, @student_fixtures.size)
  end
  
  test "get teachers" do
    Entity.url_type = "teachers/"
    Entity._format = ActiveResource::Formats::JsonLinkFormat
    students = Entity.all
    assert_equal(students.size, @teacher_fixtures.size)
  end
end
