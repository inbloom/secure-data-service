require 'test_helper'
require 'entities_helper'

class EntitiesControllerTest < ActionController::TestCase
  test "should get index for students" do
    get :index, type: "students"
    assert_response :success
    assert_not_nil assigns(:entities)
  end

  test "should show entity for students" do
    get :show, id: @student_fixtures['one']['id'].to_param, type: "students"
    assert_response :success
  end

  test "get full teacher objects through association" do
    get :show, id: "1".to_param , type:'teacher-school-associations'
    assert_response :success
  end
  
end
