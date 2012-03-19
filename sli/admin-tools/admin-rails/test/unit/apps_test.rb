require 'test_helper'

class AppTest < ActiveSupport::TestCase
  test "get apps" do
    apps = App.all
    assert_not_nil(apps)
    assert_equal(apps.size, @app_fixtures.size)
  end
  
  test "new app" do
    app = App.new
    assert_not_nil(app)
  end
end