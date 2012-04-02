require 'test_helper'

class StaffsControllerTest < ActionController::TestCase
  setup do
    @staff = staffs(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:staffs)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create staff" do
    assert_difference('Staff.count') do
      post :create, staff: @staff.attributes
    end

    assert_redirected_to staff_path(assigns(:staff))
  end

  test "should show staff" do
    get :show, id: @staff.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @staff.to_param
    assert_response :success
  end

  test "should update staff" do
    put :update, id: @staff.to_param, staff: @staff.attributes
    assert_redirected_to staff_path(assigns(:staff))
  end

  test "should destroy staff" do
    assert_difference('Staff.count', -1) do
      delete :destroy, id: @staff.to_param
    end

    assert_redirected_to staffs_path
  end
end
