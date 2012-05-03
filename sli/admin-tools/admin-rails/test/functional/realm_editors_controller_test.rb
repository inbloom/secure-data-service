require 'test_helper'

class RealmEditorsControllerTest < ActionController::TestCase
  #setup do
  #  @realm_editor = realm_editors(:one)
  #end

  test "should get index" do
    puts("The realm fixtures are #{@realm_fixtures['one'].inspect}")
    get :index
    assert_response :success
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
