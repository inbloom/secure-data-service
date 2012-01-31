require 'test_helper'

class EntitiesControllerTest < ActionController::TestCase
  test "should get indexfor students" do
    Entity.url_type = "students"
    get :index
    assert_response :success
    assert_not_nil assigns(:entities)
  end


  test "should show entity for students" do
    Entity.url_type = "students"
    get :show, id: @student_fixtures['one']['id'].to_param
    assert_response :success
  end


end
