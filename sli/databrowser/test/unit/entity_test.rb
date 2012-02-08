require 'test_helper'

class EntityTest < ActiveSupport::TestCase
  test "get students" do
    Entity.url_type = "students"
    students = Entity.all
    assert_equal(students.size, @student_fixtures.size)
  end
  
  test "get teachers" do
    Entity.url_type = "teachers"
    students = Entity.all
    assert_equal(students.size, @student_fixtures.size)
  end
end
