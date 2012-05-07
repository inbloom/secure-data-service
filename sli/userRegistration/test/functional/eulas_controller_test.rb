require 'test_helper'

class EulasControllerTest < ActionController::TestCase
  setup do
    @eula = eulas(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:eulas)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create eula" do
    assert_difference('Eula.count') do
      post :create, eula: {  }
    end

    assert_redirected_to eula_path(assigns(:eula))
  end

  test "should show eula" do
    get :show, id: @eula
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @eula
    assert_response :success
  end

  test "should update eula" do
    put :update, id: @eula, eula: {  }
    assert_redirected_to eula_path(assigns(:eula))
  end

  test "should destroy eula" do
    assert_difference('Eula.count', -1) do
      delete :destroy, id: @eula
    end

    assert_redirected_to eulas_path
  end
end
