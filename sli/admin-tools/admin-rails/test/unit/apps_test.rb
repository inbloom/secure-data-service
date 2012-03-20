require 'test_helper'

class AppTest < ActiveSupport::TestCase
  test "get apps" do
    apps = App.all
    assert_not_nil(apps)
    assert_equal(apps.size, 2)
  end
  
  test "new app" do
    app = App.new
    assert_not_nil(app)
  end
  
  # test "save app" do
  #   app = App.new(@app_fixtures["new"])
  #   assert(app.valid?, "This should be valid")
  #   assert_nothing_raised(Exception) { app.save }
  # end
end