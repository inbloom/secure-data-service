require 'test_helper'

class MappingsControllerTest < ActionController::TestCase
  # setup do
  #   @mapping = mappings(:one)
  # end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:mappings)
  end

  test "should show mapping" do
    get :show, id: @realm_fixtures['one']['state']
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @realm_fixtures['one']['state']
    assert_response :success
  end

end
