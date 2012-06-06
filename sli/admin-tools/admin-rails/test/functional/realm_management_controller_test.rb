require 'test_helper'

class RealmManagementControllerTest < ActionController::TestCase
  #setup do
  #  @realm_editor = realm_management(:one)
  #end

  test "should get index" do
    get :index
    assert_response 404
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @realm_fixtures['one']['id']
    assert_response :success
  end

end
