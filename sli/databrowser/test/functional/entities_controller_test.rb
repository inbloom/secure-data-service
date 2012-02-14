require 'test_helper'
require 'entities_helper'

class EntitiesControllerTest < ActionController::TestCase
  
  test "should get index for students" do
    get :index, type: "students"
    assert_response :success
    assert_not_nil assigns(:entities)
  end

  test "should show entity for students" do
    get :show, other: "students/"+ @student_fixtures['one']['id'].to_param
    assert_response :success
  end

  test "get full teacher objects through association" do
    get :show, other: "teacher-school-associations/1".to_param
    assert_response :success
  end
  
end
