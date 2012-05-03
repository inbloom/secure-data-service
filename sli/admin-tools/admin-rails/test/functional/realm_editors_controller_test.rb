require 'test_helper'

class RealmEditorsControllerTest < ActionController::TestCase
  setup do
    @realm_editor = realm_editors(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:realm_editors)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create realm_editor" do
    assert_difference('RealmEditor.count') do
      post :create, realm_editor: @realm_editor.attributes
    end

    assert_redirected_to realm_editor_path(assigns(:realm_editor))
  end

  test "should show realm_editor" do
    get :show, id: @realm_editor.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @realm_editor.to_param
    assert_response :success
  end

  test "should update realm_editor" do
    put :update, id: @realm_editor.to_param, realm_editor: @realm_editor.attributes
    assert_redirected_to realm_editor_path(assigns(:realm_editor))
  end

  test "should destroy realm_editor" do
    assert_difference('RealmEditor.count', -1) do
      delete :destroy, id: @realm_editor.to_param
    end

    assert_redirected_to realm_editors_path
  end
end
