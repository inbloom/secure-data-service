require 'test_helper'

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


end
