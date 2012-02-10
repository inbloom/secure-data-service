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
    assert_equal(v, "Female")
  end

  test "simple value generator with nested type" do
    v = Entity.value_for_simple_view(VIEW_CONFIG['teacher'][1], @teacher_fixtures['one'])
    assert_not_nil(v)
  end

  test "build simple hash with invalid type" do
    assert_not_nil(Entity.build_simple_hash(nil, @teacher_fixtures['one']))
  end

  test "build simple hash with invalid hash" do
    assert_nil(Entity.build_simple_hash(VIEW_CONFIG['teacher'], nil))
  end

  test "build simple hash with valid data" do
    assert_not_nil(Entity.build_simple_hash(VIEW_CONFIG['teacher'], @teacher_fixtures['one']))
  end
  
  test "get students" do
    Entity.url_type = "students"
    students = Entity.all
    assert_equal(students.size, @student_fixtures.size)
  end
  
  test "get teachers" do
    Entity.url_type = "teachers"
    students = Entity.all
    assert_equal(students.size, @teacher_fixtures.size)
  end
end
